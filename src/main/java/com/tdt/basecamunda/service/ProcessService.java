package com.tdt.basecamunda.service;

import java.util.Map;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProcessService {

  @Autowired private RuntimeService runtimeService;

    public ProcessInstance startProcess(
            String processDefinitionKey,
            String businessKey,
            Map<String, Object> variables
    ) {
        return runtimeService.startProcessInstanceByKey(
                processDefinitionKey,
                businessKey,
                variables
        );
    }
}
