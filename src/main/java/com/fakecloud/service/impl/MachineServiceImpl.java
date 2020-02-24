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

@Service
@Slf4j
public class MachineServiceImpl implements MachineService {

    private MachineRepository machineRepository;

    public MachineServiceImpl(MachineRepository machineRepository) {
        this.machineRepository = machineRepository;
    }

    @Override
    public Machine findByUid(String uid) {
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
            Thread.sleep(randomTimeInterval(10000, 15000));
            machine.setStatus(MachineStatus.RUNNING);
            machine.setProcessed(false);

            machineRepository.save(machine);

            log.info("IN start - machine: {} successfully started", machine);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @Async
    public void stop(Machine machine) {
        try {
            Thread.sleep(randomTimeInterval(5000, 10000));
            machine.setStatus(MachineStatus.STOPPED);
            machine.setProcessed(false);

            machineRepository.save(machine);

            log.info("IN stop - machine: {} successfully stopped", machine);
        } catch (Exception e) {
            log.error(e.getMessage());
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
            Thread.sleep(randomTimeInterval(5000, 10000));
            Machine machine = new Machine();
            machine.setUid(uid);
            machine.setCreatedBy(user);
            machine.setStatus(MachineStatus.STOPPED);
            machine.setCreatedAt(new Date());
            machine.setActive(true);
            machine.setProcessed(false);

            machineRepository.save(machine);

            log.info("IN create - machine: {} successfully created", machine);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    @Async
    public void destroy(Machine machine) {
        try {
            Thread.sleep(randomTimeInterval(5000, 10000));
            machine.setActive(false);
            machine.setProcessed(false);

            machineRepository.save(machine);

            log.info("IN destroy - machine: {} successfully destroyed", machine);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public void setProcessing(Machine machine) {
        machine.setProcessed(true);
        machineRepository.save(machine);
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
