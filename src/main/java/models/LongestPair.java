package models;

import lombok.Data;


/** The LongestPair class represents a pair of employees who have worked together on a project
 *   for the longest period of time.
 *   overlapDays: The number of days that the employees worked together.
 *   employeeOneId: Unique identifier of employee.
 *   employeeTwoId: Unique identifier of employee.
 */
@Data
public class LongestPair {
  private int overlapDays;
  private Long employeeOneId;
  private Long employeeTwoId;
}
