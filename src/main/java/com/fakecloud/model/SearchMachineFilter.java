package com.fakecloud.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SearchMachineFilter {
    private List<MachineStatus> status;
    private Date dateFrom;
    private Date dateTo;
}
