package models;

import lombok.Data;

@Data
public class SolutionResult {
  private Long projectId;
  private Long employeeOneId;
  private Long employeeTwoId;
  private int daysWorkedTogether;
}
