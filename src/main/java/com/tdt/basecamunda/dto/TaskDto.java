package com.tdt.basecamunda.dto;

public record TaskDto(
    String taskId, String name, String assignee, String processInstanceId, String businessKey) {}
