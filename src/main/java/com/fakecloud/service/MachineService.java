package com.fakecloud.service;

import com.fakecloud.model.Machine;
import com.fakecloud.model.User;

public interface MachineService {

    Machine findByUid(String uid);

    void setProcessing(Machine machine);

    void start(Machine machine);

    void stop(Machine machine);

    void restart(Machine machine);

    void create(String uid, User user);

    void destroy(Machine machine);
}