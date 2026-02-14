package com.tdt.basecamunda.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class CheckApprovalDelegate implements JavaDelegate {

  @Override
  public void execute(DelegateExecution execution) {

    Boolean approved = (Boolean) execution.getVariable("approved");

    if (approved == null) {
      approved = false;
    }
    execution.setVariable("approved", approved);
  }
}
