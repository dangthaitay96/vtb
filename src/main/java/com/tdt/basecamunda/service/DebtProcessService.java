package com.tdt.basecamunda.service;

import com.tdt.basecamunda.dto.StartProcessRequest;
import com.tdt.basecamunda.dto.StartProcessResponse;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public StartProcessResponse startDebtProcess(StartProcessRequest request) {
        String caseId = request.getCaseId();

        List<Map<String, Object>> docs = new ArrayList<>();
        docs.add(Map.of(
                "docNo", "DOC1",
                "docId", request.getDocuments().getDoc1Id(),
                "childBusinessKey", caseId + "-DOC1"
        ));
        docs.add(Map.of(
                "docNo", "DOC2",
                "docId", request.getDocuments().getDoc2Id(),
                "childBusinessKey", caseId + "-DOC2"
        ));
        docs.add(Map.of(
                "docNo", "DOC3",
                "docId", request.getDocuments().getDoc3Id(),
                "childBusinessKey", caseId + "-DOC3"
        ));

        Map<String, Object> vars = new HashMap<>();
        vars.put("docs", docs);

        // businessKey = caseId
        ProcessInstance parent = runtimeService.startProcessInstanceByKey(
                "DEBT_PROCESS",
                request.getCaseId(),
                vars
        );

        // query child processes (do callActivity tạo ra)
        var children = runtimeService.createProcessInstanceQuery()
                .superProcessInstanceId(parent.getId())
                .list();

        StartProcessResponse resp = new StartProcessResponse();
        resp.setCaseId(request.getCaseId());
        resp.setParentProcessInstanceId(parent.getId());
        resp.setParentBusinessKey(parent.getBusinessKey());

        resp.setChildren(
                children.stream()
                        .map(pi -> new StartProcessResponse.Child(pi.getId(), pi.getBusinessKey()))
                        .toList()
        );

        return resp;
    }
}
