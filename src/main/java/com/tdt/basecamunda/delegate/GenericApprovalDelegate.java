package com.tdt.basecamunda.delegate;

import com.tdt.basecamunda.service.ApprovalLogicHandler;
import java.util.UUID;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("genericApprovalDelegate")
public class GenericApprovalDelegate implements JavaDelegate {

  private static final Logger log = LoggerFactory.getLogger(GenericApprovalDelegate.class);
  private final ApprovalLogicHandler approvalLogicHandler;

  public GenericApprovalDelegate(ApprovalLogicHandler approvalLogicHandler) {
    this.approvalLogicHandler = approvalLogicHandler;
  }

  @Override
  public void execute(DelegateExecution execution) throws Exception {
    String step = (String) execution.getVariable("step");
    String processType = (String) execution.getVariable("processType");
    String businessId = (String) execution.getVariable("businessId");

    // Auto-generate unique ID for this step execution
    String processId = UUID.randomUUID().toString();

    log.info("[Step] {} | [ProcessId] {} | [BusinessId] {}", step, processId, businessId);
    approvalLogicHandler.handleStep(step, processType, businessId, processId);
  }
}
