package com.fakecloud.repository;

import com.fakecloud.model.Machine;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MachineRepository extends JpaRepository<Machine, Long> {
    Machine findByUidAndActive(String uid, boolean active);
}
