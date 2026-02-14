package com.tdt.basecamunda.controller;

import com.tdt.basecamunda.dto.TaskDto;
import com.tdt.basecamunda.service.TaskAppService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping()
public class TaskController {

  private final TaskAppService taskAppService;

  public TaskController(TaskAppService taskAppService) {
    this.taskAppService = taskAppService;
  }

  @GetMapping("/tasks")
  public List<TaskDto> getTasks(
      @RequestParam("businessKey") String businessKey,
      @RequestParam("candidateGroup") String candidateGroup) {
    return taskAppService.getTasks(businessKey, candidateGroup);
  }

  @PostMapping("/{taskId}/complete")
  public void completeTask(
      @PathVariable String taskId, @RequestBody Map<String, Object> variables) {
    taskAppService.completeTask(taskId, variables);
  }
}
