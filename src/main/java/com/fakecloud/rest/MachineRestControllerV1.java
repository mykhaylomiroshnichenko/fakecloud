package com.fakecloud.rest;

import com.fakecloud.dto.bridge.MachineToMachineDtoBridge;
import com.fakecloud.dto.model.MachineDto;
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
import org.springframework.web.context.request.WebRequest;

import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.springframework.util.StringUtils.isEmpty;

@RestController
@RequestMapping(value = "/api/v1/machine/")
public class MachineRestControllerV1 {
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private MachineService machineService;

    private UserService userService;

    @Autowired
    public MachineRestControllerV1(MachineService machineService, UserService userService) {
        this.machineService = machineService;
        this.userService = userService;
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
    }

    @GetMapping("create")
    public ResponseEntity<?> create(Authentication authentication) {
        String uid = UUID.randomUUID().toString();
        User user = userService.findByUsername(authentication.getName());
        executorService.submit(() -> machineService.create(uid, user));

        Map<Object, Object> response = new HashMap<>();
        response.put("uid", uid);

        return ResponseEntity.ok(response);
    }

    @GetMapping("{uid}/get")
    public ResponseEntity<MachineDto> get(@PathVariable(name = "uid") String uid){
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
    public ResponseEntity<?> start(WebRequest webRequest, @PathVariable(name = "uid") String uid){
        Machine machine = machineService.findByUid(uid);
        if (machine == null) {
            throw new BadRequestException("Error machine with uid:" + uid + " has not found");
        }
        if (!machine.getStatus().equals(MachineStatus.STOPPED)) {
            throw new BadRequestException("Error machine with uid:" + uid + " already started");
        }
        checkVersionAndProcessing(webRequest, machine);
        machineService.setProcessing(machine);

        executorService.submit(() -> machineService.start(machine));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{uid}/stop")
    public ResponseEntity<?> stop(WebRequest webRequest, @PathVariable(name = "uid") String uid){
        Machine machine = machineService.findByUid(uid);
        if (machine == null) {
            throw new BadRequestException("Error machine with uid:" + uid + " has not found");
        }
        if (!machine.getStatus().equals(MachineStatus.RUNNING)) {
            throw new BadRequestException("Error machine with uid:" + uid + " already stopped");
        }
        checkVersionAndProcessing(webRequest, machine);
        machineService.setProcessing(machine);

        executorService.submit(() -> machineService.stop(machine));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{uid}/restart")
    public ResponseEntity<?> restart(WebRequest webRequest, @PathVariable(name = "uid") String uid){
        Machine machine = machineService.findByUid(uid);
        if (machine == null) {
            throw new BadRequestException("Error machine with uid:" + uid + " has not found");
        }
        if (!machine.getStatus().equals(MachineStatus.RUNNING)) {
            throw new BadRequestException("Error machine with uid:" + uid + " already stopped");
        }
        checkVersionAndProcessing(webRequest, machine);
        machineService.setProcessing(machine);

        executorService.submit(() -> machineService.restart(machine));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("{uid}/destroy")
    public ResponseEntity<?> destroy(WebRequest webRequest, @PathVariable(name = "uid") String uid){
        Machine machine = machineService.findByUid(uid);
        if (machine == null) {
            throw new BadRequestException("Error machine with uid:" + uid + " has not found");
        }
        if (!machine.getStatus().equals(MachineStatus.STOPPED)) {
            throw new BadRequestException("Error machine with uid:" + uid + " is Running");
        }
        checkVersionAndProcessing(webRequest, machine);
        machineService.setProcessing(machine);

        executorService.submit(() -> machineService.destroy(machine));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void checkVersionAndProcessing(WebRequest webRequest, Machine machine) {
        String ifMatchValue = webRequest.getHeader("If-Match");
        if (isEmpty(ifMatchValue)) {
            throw new BadRequestException("Header If-Match is required");
        }
        if (!ifMatchValue.equals("\"" + machine.getVersion() + "\"")) {
            throw new PreconditionFailedException("Incorrect version of machine");
        }
        if (machine.isProcessed()) {
            throw new PreconditionFailedException("Machine is processing");
        }
    }
}
