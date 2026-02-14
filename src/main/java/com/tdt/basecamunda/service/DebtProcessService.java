package com.tdt.basecamunda.service;

import com.tdt.basecamunda.dto.StartProcessRequest;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;


@Service
public class DebtProcessService {

    private final RuntimeService runtimeService;

    public DebtProcessService(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    public String startProcess(StartProcessRequest request) {

        Map<String, Object> variables = new HashMap<>();

        // id tài liệu
        variables.put("doc1Id", request.getDocuments().getDoc1Id());
        variables.put("doc2Id", request.getDocuments().getDoc2Id());
        variables.put("doc3Id", request.getDocuments().getDoc3Id());

        // trạng thái duyệt (BẮT BUỘC phải Boolean)
        variables.put("doc1Approved", false);
        variables.put("doc2Approved", false);
        variables.put("doc3Approved", false);

        // trạng thái ký số
        variables.put("doc1Signed", false);
        variables.put("doc2Signed", false);
        variables.put("doc3Signed", false);

        ProcessInstance instance =
                runtimeService.startProcessInstanceByKey(
                        "DEBT_PROCESS",
                        request.getCaseId(),   // business key
                        variables
                );

        return instance.getProcessInstanceId();
    }


}
