package com.tdt.basecamunda.service;


import org.springframework.stereotype.Service;

@Service
public class ApprovalLogicHandler {

  public void handleStep(String step, String processType, String businessId, String processId) {
    if (processType.equals("TWO_STEP_APPROVAL")) {
      if (step.equals("Manager Approval")) {
        // duyệt 1
      } else if (step.equals("Director Approval")) {
        // duyệt 2
      }
    } else if (processType.equals("EIGHT_STEP_APPROVAL")) {
      // handle các step khác nhau
    }
  }
}
