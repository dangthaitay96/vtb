package com.tdt.basecamunda.service;

import com.tdt.basecamunda.dto.StartProcessRequest;
import com.tdt.basecamunda.dto.StartProcessResponse;
import com.tdt.basecamunda.dto.TaskDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class DebtProcessService {

    private final RuntimeService runtimeService;
    private final TaskService taskService;

    public DebtProcessService(RuntimeService runtimeService, TaskService taskService) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
    }

//    public StartProcessResponse startDebtProcess(StartProcessRequest request) {
//        String caseId = request.getCaseId();
//        String solutionType = request.getSolutionType();
//        List<Map<String, Object>> docs = new ArrayList<>();
//        docs.add(Map.of(
//                "docNo", "DOC1",
//                "docId", request.getDocuments().getDoc1Id(),
//                "childBusinessKey", caseId + "-DOC1"
//        ));
//        docs.add(Map.of(
//                "docNo", "DOC2",
//                "docId", request.getDocuments().getDoc2Id(),
//                "childBusinessKey", caseId + "-DOC2"
//        ));
//        docs.add(Map.of(
//                "docNo", "DOC3",
//                "docId", request.getDocuments().getDoc3Id(),
//                "childBusinessKey", caseId + "-DOC3"
//        ));
//        Map<String, Object> vars = new HashMap<>();
//        vars.put("docs", docs);
//        ProcessInstance parent = runtimeService.startProcessInstanceByKey(
//                "DEBT_PROCESS",
//                request.getCaseId(),
//                vars
//        );
//        var children = runtimeService.createProcessInstanceQuery()
//                .superProcessInstanceId(parent.getId())
//                .list();
//        StartProcessResponse resp = new StartProcessResponse();
//        resp.setCaseId(request.getCaseId());
//        resp.setParentProcessInstanceId(parent.getId());
//        resp.setParentBusinessKey(parent.getBusinessKey());
//        resp.setChildren(
//                children.stream()
//                        .map(pi -> new StartProcessResponse.Child(pi.getId(), pi.getBusinessKey()))
//                        .toList()
//        );
//        return resp;
//    }

    public StartProcessResponse startDebtProcess(StartProcessRequest request) {
        String caseId = request.getCaseId();
        String solutionType = request.getSolutionType(); // "HDXLTDCN" | "GIAM_DOC"

        Map<String, Object> vars = new HashMap<>();
        vars.put("solutionType", solutionType);

        // map variables đúng tên BPMN đang dùng
        if (request.getDocuments() != null) {
            vars.put("doc1Id", request.getDocuments().getDoc1Id());
            vars.put("doc2Id", request.getDocuments().getDoc2Id());
            vars.put("doc3Id", request.getDocuments().getDoc3Id());
            vars.put("doc6Id", request.getDocuments().getDoc6Id());
        }

        // start parent: businessKey = caseId
        ProcessInstance parent = runtimeService.startProcessInstanceByKey(
                "DEBT_PROCESS",
                caseId,
                vars
        );

        // tìm tất cả child instances được tạo bởi callActivity
        var children = runtimeService.createProcessInstanceQuery()
                .superProcessInstanceId(parent.getId())
                .list();

        StartProcessResponse resp = new StartProcessResponse();
        resp.setCaseId(caseId);
        resp.setParentProcessInstanceId(parent.getId());
        resp.setParentBusinessKey(parent.getBusinessKey());
        resp.setChildren(
                children.stream()
                        .map(pi -> new StartProcessResponse.Child(pi.getId(), pi.getBusinessKey()))
                        .toList()
        );
        return resp;
    }

    /**
     * Complete task active hiện tại theo businessKey.
     * Nếu có >1 task active -> throw để m khỏi complete nhầm.
     */
    public void completeCurrentTaskByBusinessKey(String businessKey, Map<String, Object> vars) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .active()
                .orderByTaskCreateTime()
                .asc()
                .list();

        if (tasks.isEmpty()) {
            throw new IllegalStateException("No active task for businessKey=" + businessKey);
        }
        if (tasks.size() > 1) {
            throw new IllegalStateException("More than 1 active task for businessKey=" + businessKey
                    + ". Please complete by taskId to avoid wrong completion.");
        }

        taskService.complete(tasks.get(0).getId(), vars);
    }

    /**
     * Lấy list task active theo businessKey (vd 001-DOC1)
     */
    public List<TaskDto> getActiveTasksByBusinessKey(String businessKey) {
        List<Task> tasks = taskService.createTaskQuery()
                .processInstanceBusinessKey(businessKey)
                .active()
                .orderByTaskCreateTime()
                .asc()
                .list();

        return tasks.stream()
                .map(t -> new TaskDto(
                        t.getId(),
                        t.getName(),
                        t.getAssignee(),
                        t.getProcessInstanceId(),
                        t.getProcessDefinitionId(),
                        t.getTaskDefinitionKey(),
                        t.getCreateTime() == null ? null : t.getCreateTime().toInstant().toString()
                ))
                .toList();
    }

    public List<TaskDto> getActiveTasksByCaseId(String caseId) {
        var parent = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey("DEBT_PROCESS")
                .processInstanceBusinessKey(caseId)
                .singleResult();

        if (parent == null) return List.of();

        var childInstances = runtimeService.createProcessInstanceQuery()
                .superProcessInstanceId(parent.getId())
                .list();

        if (childInstances.isEmpty()) return List.of();

        var childIds = childInstances.stream().map(ProcessInstance::getId).toList();

        var tasks = taskService.createTaskQuery()
                .processInstanceIdIn(childIds.toArray(String[]::new))
                .active()
                .list();

        return tasks.stream().map(TaskDto::from).toList();
    }

    /**
     * Lấy 1 task detail theo taskId
     */
    public TaskDto getTaskById(String taskId) {
        Task t = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (t == null) throw new IllegalStateException("Task not found: " + taskId);

        return new TaskDto(
                t.getId(),
                t.getName(),
                t.getAssignee(),
                t.getProcessInstanceId(),
                t.getProcessDefinitionId(),
                t.getTaskDefinitionKey(),
                t.getCreateTime() == null ? null : t.getCreateTime().toInstant().toString()
        );
    }

    /**
     * Complete theo taskId (best safe)
     */
    public void completeTaskById(String taskId, Map<String, Object> vars) {
        Task t = taskService.createTaskQuery().taskId(taskId).active().singleResult();
        if (t == null) throw new IllegalStateException("Active task not found: " + taskId);
        taskService.complete(taskId, vars);
    }

    /**
     * lấy processInstanceId theo businessKey
     */
    public String getProcessInstanceIdByBusinessKey(String businessKey) {
        ProcessInstance pi = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .active()
                .singleResult();
        if (pi == null) throw new IllegalStateException("ProcessInstance not found for businessKey=" + businessKey);
        return pi.getId();
    }

    /**
     * xem variables của process instance theo businessKey
     */
    public Map<String, Object> getVariablesByBusinessKey(String businessKey) {
        String piId = getProcessInstanceIdByBusinessKey(businessKey);
        return runtimeService.getVariables(piId);
    }
}
