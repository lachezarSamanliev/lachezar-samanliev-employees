package services;

import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

import models.EmployeeWithProject;

@Service
public class CalculatorService {
  // remove
  public Integer countDaysForUserOnProject(EmployeeWithProject employeeWithProject){
    return Math.toIntExact(
        Math.abs(ChronoUnit.DAYS.between(employeeWithProject.getDateFrom(), employeeWithProject.getDateTo())));

  }
}
