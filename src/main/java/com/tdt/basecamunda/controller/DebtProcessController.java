package com.tdt.basecamunda.controller;

import com.tdt.basecamunda.dto.StartProcessRequest;
import com.tdt.basecamunda.service.DebtProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/debt-process")
public class DebtProcessController {

    @Autowired
    private DebtProcessService debtProcessService;

    @PostMapping
    public ResponseEntity<?> start(@RequestBody StartProcessRequest request) {

        String processInstanceId =
                debtProcessService.startProcess(request);

        return ResponseEntity.ok(Map.of(
                "processInstanceId", processInstanceId,
                "caseId", request.getCaseId()
        ));
    }
}
