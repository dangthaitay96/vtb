package com.tdt.basecamunda.service;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.springframework.stereotype.Service;

@Service
public class TaskAppService {

    private final TaskService taskService;
    private final RuntimeService runtimeService;

    public TaskAppService(TaskService taskService, RuntimeService runtimeService) {
        this.taskService = taskService;
        this.runtimeService = runtimeService;
    }
}
