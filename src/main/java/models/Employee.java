package models;

import java.time.LocalDate;

import lombok.Data;

/** The Employee class represents an employee entity w/ three attributes:
 *   employeeId: A unique identifier for the employee.
 *   dateFrom: The date when the employee started working on the project.
 *   dateTo: The date when the employee left the project (or null if the employee is still
 * working there).
 */
@Data
public class Employee {
  private Long employeeId;
  private LocalDate dateFrom;
  private LocalDate dateTo;
}
