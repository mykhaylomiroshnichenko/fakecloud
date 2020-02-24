package com.fakecloud.repository;

import com.fakecloud.model.Machine;
import com.fakecloud.model.SearchMachineFilter;

import java.util.List;

public interface MachineRepositoryCustom {
    List<Machine> search(SearchMachineFilter filter);
}
