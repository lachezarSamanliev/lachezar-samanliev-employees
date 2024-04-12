package services;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import models.Employee;
import models.EmployeeWithProject;
import models.LongestPair;
import models.SolutionResult;

@Service
public class SolutionsService {

  public EmployeeWithProject returnSolutionFromLine(String[] line) {
    String csvUserId = line[0];
    String csvProjectId = line[1];
    String csvDateFrom = line[2];
    String csvDateTo = line[3];

    if (!canParseLong(csvUserId) || !canParseLong(csvProjectId)) {
      // if null is returned, it will be ignored
      printIgnoredLine(csvUserId, csvProjectId, csvDateFrom, csvDateTo);
      return null;
    }
    if (!canParseDate(csvDateFrom) || !canParseDate(csvDateTo)) {
      printIgnoredLine(csvUserId, csvProjectId, csvDateFrom, csvDateTo);
      return null;
    }

    Long userId = Long.parseLong(csvUserId);
    Long projectId = Long.parseLong(csvProjectId);
    LocalDate dateFrom = parseDate(csvDateFrom);
    LocalDate dateTo = parseDate(csvDateTo);

    return getSolutionFinal(userId, projectId, dateFrom, dateTo);
  }

  private void printIgnoredLine(String csvUserId, String csvProjectId, String csvDateFrom,
                                String csvDateTo) {
    System.out.printf("Ignored line with values empId: %s projId: %s From: %s To: %s%n", csvUserId,
                      csvProjectId, csvDateFrom, csvDateTo);
  }

  private EmployeeWithProject getSolutionFinal(Long userId, Long projectId, LocalDate dateFrom,
                                               LocalDate dateTo) {
    EmployeeWithProject employeeWithProject = new EmployeeWithProject();
    employeeWithProject.setEmployeeId(userId);
    employeeWithProject.setProjectId(projectId);
    employeeWithProject.setDateFrom(dateFrom);
    employeeWithProject.setDateTo(dateTo);
    return employeeWithProject;
  }

  private LocalDate parseDate(String dateString) {
    if (dateString == null || dateString.equals("NULL")) {
      //toDate is null, we use LocalDate.now()
      return LocalDate.now();
    }
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return LocalDate.parse(dateString, dateFormatter);
  }

  private static boolean canParseDate(String currDate) {
    if (currDate == null || currDate.equals("NULL")) {
      return true;
    }
    try {
      DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      LocalDate.parse(currDate, dateFormatter);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }

  public static boolean canParseLong(String currId) {
    try {
      Long.parseLong(currId);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }

  public Set<Long> getUniqueProjectIds(List<EmployeeWithProject> employeeWithProjectList) {
    // create List of Unique projectIds
    return employeeWithProjectList.stream()
                                  .map(EmployeeWithProject::getProjectId)
                                  .collect(Collectors.toSet());
  }

  public List<SolutionResult> getSolutionResultsBasedOnProjects(
      HashMap<Long, List<Employee>> projectsAndUsersMap) {
    List<SolutionResult> solutionResultList = new ArrayList<>();
    // we can for loop through each Map instance
    for (Map.Entry<Long, List<Employee>> entry : projectsAndUsersMap.entrySet()) {
      Long projectId = entry.getKey();
      List<Employee> employeesList = entry.getValue();

      // Loop through the list of solutions for the current project
      LongestPair longestPair = getLongestPeriodForProject(employeesList);

      // end of for loop for project Map
      // we have the projectId and the longest Pair
      SolutionResult solutionResult = new SolutionResult();
      solutionResult.setProjectId(projectId);
      solutionResult.setEmployeeOneId(longestPair.getEmployeeOneId());
      solutionResult.setEmployeeTwoId(longestPair.getEmployeeTwoId());
      solutionResult.setDaysWorkedTogether(longestPair.getOverlapDays());

      // make sure two employees have worked on this project
      if (solutionResult.getEmployeeOneId() != null && solutionResult.getEmployeeTwoId() != null) {
        solutionResultList.add(solutionResult);
      }
    }
    return solutionResultList;
  }

  private LongestPair getLongestPeriodForProject(List<Employee> employeesList) {
    LongestPair longestPair = new LongestPair();
    int longestOverlap = 0;

    for (int i = 0; i < employeesList.size() - 1; i++) {
      for (int j = i + 1; j < employeesList.size(); j++) {

        Employee employee1 = employeesList.get(i);
        Employee employee2 = employeesList.get(j);

        LocalDate overlapStart = employee1.getDateFrom().isAfter(employee2.getDateFrom())
            ? employee1.getDateFrom() : employee2.getDateFrom();
        LocalDate overlapEnd = employee1.getDateTo().isBefore(employee2.getDateTo())
            ? employee1.getDateTo() : employee2.getDateTo();

        if (overlapEnd.isAfter(overlapStart) || overlapEnd.isEqual(overlapStart)) {
          int overlapDays = (int) ChronoUnit.DAYS.between(overlapStart, overlapEnd);
          if (overlapDays > longestOverlap) {
            longestOverlap = overlapDays;

            longestPair.setOverlapDays(longestOverlap);
            longestPair.setEmployeeOneId(employee1.getEmployeeId());
            longestPair.setEmployeeTwoId(employee2.getEmployeeId());
          }
        }
      }
    }
    return longestPair;
  }

  public HashMap<Long, List<Employee>> filterUsersInProject(
      List<EmployeeWithProject> employeeWithProjectList,
      Set<Long> projectIds) {
    HashMap<Long, List<Employee>> projectsAndUsersMap = new HashMap<>();
    // for loop the set
    for (Long currProjectId : projectIds) {
      List<Employee> filteredSolutionsPerProject = new ArrayList<>();
      for (EmployeeWithProject employeeWithProject : employeeWithProjectList) {
        if (employeeWithProject.getProjectId().equals(currProjectId)) {
          // HERE might be an issue
          filteredSolutionsPerProject.add(employeeWithProject);
        }
      }
      // populate map
      projectsAndUsersMap.put(currProjectId, filteredSolutionsPerProject);
    }
    return projectsAndUsersMap;
  }

}
