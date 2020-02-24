package com.fakecloud.dto.bridge;

import com.fakecloud.dto.model.SearchMachineFilterDto;
import com.fakecloud.model.MachineStatus;
import com.fakecloud.model.SearchMachineFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchMachineFilterDtoToSearchMachineFilterBridge {
    public static SearchMachineFilter build(SearchMachineFilterDto filterDto) throws Exception {
        SearchMachineFilter filter = new SearchMachineFilter();
        if (filterDto.getStatus() != null && filterDto.getStatus().size() > 0) {
            List<MachineStatus> machineStatus = new ArrayList<>();
            for (String status: filterDto.getStatus()) {
                machineStatus.add(MachineStatus.valueOf(status));
            }
            filter.setStatus(machineStatus);
        }

        if (filterDto.getDateFrom() != null) {
            filter.setDateFrom( new SimpleDateFormat("yyyy-MM-dd").parse(filterDto.getDateFrom()));
        }

        if (filterDto.getDateTo() != null) {
            filter.setDateTo( new SimpleDateFormat("yyyy-MM-dd").parse(filterDto.getDateTo()));
        }

        return filter;
    }
}
