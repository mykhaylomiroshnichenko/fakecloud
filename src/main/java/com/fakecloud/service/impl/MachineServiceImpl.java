package com.fakecloud.service.impl;

import com.fakecloud.model.Machine;
import com.fakecloud.model.MachineStatus;
import com.fakecloud.model.SearchMachineFilter;
import com.fakecloud.model.User;
import com.fakecloud.repository.MachineRepository;
import com.fakecloud.service.MachineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class MachineServiceImpl implements MachineService {

    private MachineRepository machineRepository;

    private ConcurrentMap<String, String> map = new ConcurrentHashMap<>();

    public MachineServiceImpl(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Override
    public synchronized Machine findByUid(String uid) {
        Machine machine = machineRepository.findByUidAndActive(uid, true);

        if (machine == null) {
            log.warn("IN findByUid - no machine found by uid: {}", uid);
            return null;
        }

        log.info("IN findByUid - machine: {} found by id: {}", machine, uid);
        return machine;
    }

    @Override
    @Async
    public void start(Machine machine) {
        try {
            map.put(machine.getUid(), "start");
            Thread.sleep(randomTimeInterval(10000, 15000));
            machine.setStatus(MachineStatus.RUNNING);

            machineRepository.save(machine);

            log.info("IN start - machine: {} successfully started", machine);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            map.remove(machine.getUid());
        }
    }

    @Override
    @Async
    public void stop(Machine machine) {
        try {
            map.put(machine.getUid(), "stop");
            Thread.sleep(randomTimeInterval(5000, 10000));
            machine.setStatus(MachineStatus.STOPPED);

            machineRepository.save(machine);

            log.info("IN stop - machine: {} successfully stopped", machine);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            map.remove(machine.getUid());
        }
    }

    @Override
    @Transactional
    public void restart(Machine machine) {
        stop(machine);
        start(machine);
    }

    @Override
    @Async
    public void create(String uid, User user) {
        try {
            map.put(uid, "create");
            Thread.sleep(randomTimeInterval(5000, 10000));
            Machine machine = new Machine();
            machine.setUid(uid);
            machine.setCreatedBy(user);
            machine.setStatus(MachineStatus.STOPPED);
            machine.setCreatedAt(new Date());
            machine.setActive(true);

            machineRepository.save(machine);

            log.info("IN create - machine: {} successfully created", machine);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            map.remove(uid);
        }
    }

    @Override
    @Async
    public void destroy(Machine machine) {
        try {
            map.put(machine.getUid(), "destroy");
            Thread.sleep(randomTimeInterval(5000, 10000));
            machine.setActive(false);

            machineRepository.save(machine);

            log.info("IN destroy - machine: {} successfully destroyed", machine);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            map.remove(machine.getUid());
        }
    }

    @Override
    public boolean isProcessing(String uid) {
        return map.get(uid) != null;
    }

    @Override
    public List<Machine> search(SearchMachineFilter filter) {
        return machineRepository.search(filter);
    }

    private int randomTimeInterval(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
