package models;

import lombok.Data;

@Data
public class EmployeeWithProject extends Employee {
  private Long projectId;
}
