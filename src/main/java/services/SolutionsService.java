package services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import models.Solution;

@Component
public class SolutionsConverter {

  public Solution returnSolutionFromLine(String[] line) {
    String csvUserId = line[0];
    String csvProjectId = line[1];
    String csvDateFrom = line[2];
    String csvDateTo = line[3];

    if(!canParseLong(csvUserId) || !canParseLong(csvProjectId)){
      return null;
      // if null is returned, it will be ignored
    }

    Long userId = Long.parseLong(csvUserId);
    Long projectId = Long.parseLong(csvProjectId);
    LocalDate dateFrom = parseDate(csvDateFrom);
    LocalDate dateTo = parseDate(csvDateTo);

    return getSolutionFinal(userId, projectId, dateFrom, dateTo);
  }

  private Solution getSolutionFinal(Long userId, Long projectId, LocalDate dateFrom, LocalDate dateTo) {
    Solution solution = new Solution();
    solution.setEmployeeId(userId);
    solution.setProjectId(projectId);
    solution.setDateFrom(dateFrom);
    solution.setDateTo(dateTo);
    return solution;
  }

  private LocalDate parseDate(String dateString) {
    if (dateString == null) {
      //toDate is null, we use LocalDate.now()
      return LocalDate.now();
    }

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate date = LocalDate.parse(dateString, dateFormatter);
    return date;
  }

  public static boolean canParseLong(String currId) {
    try {
      Long.parseLong(currId);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  // maybe add method for canParseDate
  // null string is fine.
}
