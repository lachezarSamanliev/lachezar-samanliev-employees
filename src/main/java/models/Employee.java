package models;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Employee {
  private Long employeeId;
  private LocalDate dateFrom;
  private LocalDate dateTo;
}
