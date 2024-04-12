package models;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Solution {
  private Long employeeId;
  private Long projectId;
  private LocalDate dateFrom;
  private LocalDate dateTo;

}
