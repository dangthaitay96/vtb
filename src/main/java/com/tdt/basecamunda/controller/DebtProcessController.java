package com.tdt.basecamunda.controller;

import com.tdt.basecamunda.dto.CompleteTaskRequest;
import com.tdt.basecamunda.dto.StartProcessRequest;
import com.tdt.basecamunda.dto.StartProcessResponse;
import com.tdt.basecamunda.dto.TaskDto;
import com.tdt.basecamunda.service.DebtProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/debt-process")
public class DebtProcessController {

    @Autowired
    private DebtProcessService debtProcessService;

    @PostMapping("/start2")
    public ResponseEntity<StartProcessResponse> start2(@RequestBody StartProcessRequest request) {
        return ResponseEntity.ok(debtProcessService.startDebtProcess(request));
    }

    /** GET: list task active theo businessKey (vd 001-DOC1) */
    @GetMapping("/{businessKey}/tasks")
    public ResponseEntity<List<TaskDto>> getTasks(@PathVariable("businessKey") String businessKey) {
        return ResponseEntity.ok(debtProcessService.getActiveTasksByBusinessKey(businessKey));
    }

    @GetMapping("/{caseId}/tasks/all")
    public ResponseEntity<List<TaskDto>> getAllTasks(@PathVariable("caseId") String caseId) {
        return ResponseEntity.ok(debtProcessService.getActiveTasksByCaseId(caseId));
    }

    /** POST: complete task active hiện tại theo businessKey */
    @PostMapping("/{businessKey}/tasks/complete")
    public ResponseEntity<?> completeByBusinessKey(
            @PathVariable String businessKey,
            @RequestBody(required = false) CompleteTaskRequest req
    ) {
        Map<String, Object> vars = (req == null || req.getVariables() == null) ? Map.of() : req.getVariables();
        debtProcessService.completeCurrentTaskByBusinessKey(businessKey, vars);
        return ResponseEntity.ok(Map.of("businessKey", businessKey, "status", "COMPLETED"));
    }

    /**  xem variables của process instance theo businessKey */
    @GetMapping("/{businessKey}/variables")
    public ResponseEntity<Map<String, Object>> getVars(@PathVariable String businessKey) {
        return ResponseEntity.ok(debtProcessService.getVariablesByBusinessKey(businessKey));
    }
}
