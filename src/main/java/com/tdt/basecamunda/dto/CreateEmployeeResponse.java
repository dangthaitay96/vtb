package com.tdt.basecamunda.dto;

public record CreateEmployeeResponse(
        Long employeeId,
        String businessKey,
        String processInstanceId
) {}
