package com.tdt.basecamunda.controller;

import com.tdt.basecamunda.dto.CreateEmployeeResponse;
import com.tdt.basecamunda.model.Employee;
import com.tdt.basecamunda.repository.EmployeeRepository;
import com.tdt.basecamunda.service.ProcessService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/test")
public class EmployeeController {

  private final EmployeeRepository employeeRepository;
  private final ProcessService processService;

  public EmployeeController(EmployeeRepository employeeRepository, ProcessService processService) {
    this.employeeRepository = employeeRepository;
      this.processService = processService;
  }

    @PostMapping("/employees")
    public CreateEmployeeResponse createEmployee(@RequestBody Employee req) {

        Employee emp = employeeRepository.save(req);

        String businessKey = "EMP-" + emp.getId();

        ProcessInstance pi = processService.startProcess(
                "employee_approval",
                businessKey,
                Map.of("employeeId", emp.getId())
        );

        return new CreateEmployeeResponse(
                emp.getId(),
                businessKey,
                pi.getProcessInstanceId()
        );
    }

}
