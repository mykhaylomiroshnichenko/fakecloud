package com.fakecloud.dto.model;

import lombok.Data;

import java.util.List;

@Data
public class SearchMachineFilterDto {
    private List<String> status;
    private String dateFrom;
    private String dateTo;
}
