package com.tdt.basecamunda.service;

import com.tdt.basecamunda.dto.TaskDto;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

@Service
public class TaskAppService {

  private final TaskService taskService;
  private final RuntimeService runtimeService;

  public TaskAppService(TaskService taskService, RuntimeService runtimeService) {
    this.taskService = taskService;
    this.runtimeService = runtimeService;
  }

  public void completeTask(String taskId, Map<String, Object> variables) {
    taskService.complete(taskId, variables);
  }

  /** Lấy task theo businessKey + candidateGroup */
  public List<TaskDto> getTasks(String businessKey, String candidateGroup) {

    return taskService
        .createTaskQuery()
        .processInstanceBusinessKey(businessKey) // 🔑 nghiệp vụ
        .taskCandidateGroup(candidateGroup) // 👤 role (HR/SA/PM)
        .active() // chỉ task đang mở
        .orderByTaskCreateTime()
        .desc()
        .list()
        .stream()
        .map(this::toDto)
        .toList();
  }

  private TaskDto toDto(Task task) {

    ProcessInstance pi =
        runtimeService
            .createProcessInstanceQuery()
            .processInstanceId(task.getProcessInstanceId())
            .singleResult();

    return new TaskDto(
        task.getId(),
        task.getName(),
        task.getAssignee(),
        task.getProcessInstanceId(),
        pi != null ? pi.getBusinessKey() : null);
  }
}
