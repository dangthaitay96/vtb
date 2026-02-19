package com.tdt.basecamunda.dto;

import java.util.List;

public class StartProcessResponse {
    private String caseId;
    private String parentProcessInstanceId;
    private List<Child> children;
    private String parentBusinessKey;

    public static class Child {
        private String processInstanceId;
        private String businessKey;
        public Child(String id, String bk) { this.processInstanceId = id; this.businessKey = bk; }

        public String getProcessInstanceId() {
            return processInstanceId;
        }

        public void setProcessInstanceId(String processInstanceId) {
            this.processInstanceId = processInstanceId;
        }

        public String getBusinessKey() {
            return businessKey;
        }

        public void setBusinessKey(String businessKey) {
            this.businessKey = businessKey;
        }
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getParentProcessInstanceId() {
        return parentProcessInstanceId;
    }

    public void setParentProcessInstanceId(String parentProcessInstanceId) {
        this.parentProcessInstanceId = parentProcessInstanceId;
    }

    public List<Child> getChildren() {
        return children;
    }

    public void setChildren(List<Child> children) {
        this.children = children;
    }

    public String getParentBusinessKey() {
        return parentBusinessKey;
    }

    public void setParentBusinessKey(String parentBusinessKey) {
        this.parentBusinessKey = parentBusinessKey;
    }
}