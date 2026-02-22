package com.tdt.basecamunda.controller;

import com.tdt.basecamunda.dto.CompleteTaskRequest;
import com.tdt.basecamunda.dto.TaskDto;
import com.tdt.basecamunda.service.DebtProcessService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping()
public class TaskController {

    private final DebtProcessService debtProcessService;

    public TaskController(DebtProcessService debtProcessService) {
        this.debtProcessService = debtProcessService;
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable String taskId) {
        return ResponseEntity.ok(debtProcessService.getTaskById(taskId));
    }

  @PostMapping("/{taskId}/complete")
  public ResponseEntity<?> complete(
          @PathVariable String taskId,
          @RequestBody(required = false) CompleteTaskRequest req
  ) {
    Map<String, Object> vars = (req == null || req.getVariables() == null) ? Map.of() : req.getVariables();
    debtProcessService.completeTaskById(taskId, vars);
    return ResponseEntity.ok(Map.of("taskId", taskId, "status", "COMPLETED"));
  }
}
