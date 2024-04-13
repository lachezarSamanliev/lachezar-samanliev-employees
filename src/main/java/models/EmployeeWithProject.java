package models;

import lombok.Data;

/** The EmployeeWithProject class extends the Employee class by adding a project attribute:
 *   projectId: A unique identifier for the project this employee is working on.
 */
@Data
public class EmployeeWithProject extends Employee {
  private Long projectId;
}
