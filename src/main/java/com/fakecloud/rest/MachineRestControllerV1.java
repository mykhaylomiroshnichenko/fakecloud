package com.fakecloud.rest;

import com.fakecloud.dto.bridge.MachineToMachineDtoBridge;
import com.fakecloud.dto.bridge.SearchMachineFilterDtoToSearchMachineFilterBridge;
import com.fakecloud.dto.model.MachineDto;
import com.fakecloud.dto.model.SearchMachineFilterDto;
import com.fakecloud.exception.BadRequestException;
import com.fakecloud.exception.PreconditionFailedException;
import com.fakecloud.model.Machine;
import com.fakecloud.model.MachineStatus;
import com.fakecloud.model.User;
import com.fakecloud.service.MachineService;
import com.fakecloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@RestController
@RequestMapping(value = "/api/v1/machine/")
public class MachineRestControllerV1 {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private MachineService machineService;

    private UserService userService;

    @Autowired
    public MachineRestControllerV1(MachineService machineService, UserService userService) {
        this.machineService = machineService;
        this.userService = userService;
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdown();
    }

    @GetMapping("create")
    public ResponseEntity<?> create(Authentication authentication) {
        String uid = UUID.randomUUID().toString();
        User user = userService.findByUsername(authentication.getName());
        executor.submit(() -> machineService.create(uid, user));

        Map<String, String> response = new HashMap<>();
        response.put("uid", uid);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{uid}/get")
    public ResponseEntity<MachineDto> get(@PathVariable(name = "uid") String uid) throws BadRequestException{
        Machine machine = machineService.findByUid(uid);
        if (machine == null) {
            throw new BadRequestException("Error machine with uid:" + uid + " has not found");
        }

        return ResponseEntity
                .ok()
                .eTag("\"" + machine.getVersion() + "\"")
                .body(MachineToMachineDtoBridge.build(machine));
    }

    @GetMapping("{uid}/start")
    public ResponseEntity<?> start(@PathVariable(name = "uid") String uid) throws Exception {
        checkProcessing(uid);
        Machine machine = machineService.findByUid(uid);
        if (machine == null) {
            throw new BadRequestException("Error machine with uid:" + uid + " has not found");
        }
        if (!machine.getStatus().equals(MachineStatus.STOPPED)) {
            throw new BadRequestException("Error machine with uid:" + uid + " already started");
        }

        executor.submit(() -> machineService.start(machine));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{uid}/stop")
    public ResponseEntity<?> stop(@PathVariable(name = "uid") String uid) throws Exception {
        checkProcessing(uid);
        Machine machine = machineService.findByUid(uid);
        if (machine == null) {
            throw new BadRequestException("Error machine with uid:" + uid + " has not found");
        }
        if (!machine.getStatus().equals(MachineStatus.RUNNING)) {
            throw new BadRequestException("Error machine with uid:" + uid + " already stopped");
        }

        executor.submit(() -> machineService.stop(machine));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{uid}/restart")
    public ResponseEntity<?> restart(@PathVariable(name = "uid") String uid) throws Exception {
        checkProcessing(uid);
        Machine machine = machineService.findByUid(uid);
        if (machine == null) {
            throw new BadRequestException("Error machine with uid:" + uid + " has not found");
        }
        if (!machine.getStatus().equals(MachineStatus.RUNNING)) {
            throw new BadRequestException("Error machine with uid:" + uid + " already stopped");
        }

        executor.submit(() -> machineService.restart(machine));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{uid}/destroy")
    public ResponseEntity<?> destroy(@PathVariable(name = "uid") String uid) throws Exception {
        checkProcessing(uid);
        Machine machine = machineService.findByUid(uid);
        if (machine == null) {
            throw new BadRequestException("Error machine with uid:" + uid + " has not found");
        }
        if (!machine.getStatus().equals(MachineStatus.STOPPED)) {
            throw new BadRequestException("Error machine with uid:" + uid + " is Running");
        }

        executor.submit(() -> machineService.destroy(machine));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("search")
    public List<Machine> search(SearchMachineFilterDto filterDto) throws BadRequestException {
        try {
            return machineService.search(SearchMachineFilterDtoToSearchMachineFilterBridge.build(filterDto));
        } catch (Exception e) {
            throw new BadRequestException("Error filter params");
        }
    }

    private void checkProcessing(String uid) throws PreconditionFailedException {
        if (machineService.isProcessing(uid)) {
            throw new PreconditionFailedException("Machine is processing");
        }
    }
}
