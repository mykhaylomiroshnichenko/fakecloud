package com.fakecloud.dto.bridge;

import com.fakecloud.dto.model.MachineDto;
import com.fakecloud.model.Machine;

public class MachineToMachineDtoBridge {
    public static MachineDto build(Machine machine) {
        MachineDto machineDto = new MachineDto();
        machineDto.setUid(machine.getUid());
        machineDto.setStatus(machine.getStatus().toString());
        machineDto.setCreatedBy(UserToUserDtoBridge.build(machine.getCreatedBy()));
        machineDto.setCreatedAt(machine.getCreatedAt());
        machineDto.setActive(machine.isActive());

        return machineDto;
    }
}
