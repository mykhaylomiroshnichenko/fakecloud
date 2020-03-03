package com.fakecloud.dto.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MachineDto {
    private String uid;
    private String status;
    private UserDto createdBy;
    private Date createdAt;
    private boolean active;
}
