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

  /**
   * Parses a line of CSV data into an EmployeeWithProject object.
   *
   * @param line An array containing the CSV elements.
   * @return An EmployeeWithProject object representing the parsed CSV data, or null if parsing fails.
   */
  public EmployeeWithProject returnSolutionFromLine(String[] line) {
    String csvUserId = line[0];
    String csvProjectId = line[1];
    String csvDateFrom = line[2];
    String csvDateTo = line[3];

    // check if UserId and projectId can be parsed to a Long
    if (!canParseLong(csvUserId) || !canParseLong(csvProjectId)) {
      // if can't be parsed - print ignore message and ignore line
      printIgnoredLine(csvUserId, csvProjectId, csvDateFrom, csvDateTo);
      return null;
    }

    // check if DateFrom and DateTo can be parsed to a LocalDate object
    if (!canParseDate(csvDateFrom) || !canParseDate(csvDateTo)) {
      // if can't be parsed - ignore line
      printIgnoredLine(csvUserId, csvProjectId, csvDateFrom, csvDateTo);
      return null;
    }

    // parse all csv Strings
    Long userId = Long.parseLong(csvUserId);
    Long projectId = Long.parseLong(csvProjectId);
    LocalDate dateFrom = parseDate(csvDateFrom);
    LocalDate dateTo = parseDate(csvDateTo);

    // populate EmployeeWithProject object
    return getSolutionFinal(userId, projectId, dateFrom, dateTo);
  }


  /**
   * Prints a message indicating that a line of the CSV data has been ignored.
   *
   * @param csvUserId    The user ID from the ignored line.
   * @param csvProjectId The project ID from the ignored line.
   * @param csvDateFrom  The start date from the ignored line.
   * @param csvDateTo    The end date from the ignored line.
   */
  private void printIgnoredLine(String csvUserId, String csvProjectId, String csvDateFrom,
                                String csvDateTo) {
    System.out.printf("Ignored line with values empId: %s projId: %s From: %s To: %s%n", csvUserId,
                      csvProjectId, csvDateFrom, csvDateTo);
  }


  /**
   * Creates an EmployeeWithProject object with the provided attributes.
   *
   * @param userId    The ID of the employee.
   * @param projectId The ID of the project.
   * @param dateFrom  The start date of the employee's work on the project.
   * @param dateTo    The end date of the employee's work on the project.
   * @return An EmployeeWithProject object initialized with the provided attributes.
   */
  private EmployeeWithProject getSolutionFinal(Long userId, Long projectId, LocalDate dateFrom,
                                               LocalDate dateTo) {
    EmployeeWithProject employeeWithProject = new EmployeeWithProject();
    employeeWithProject.setEmployeeId(userId);
    employeeWithProject.setProjectId(projectId);
    employeeWithProject.setDateFrom(dateFrom);
    employeeWithProject.setDateTo(dateTo);
    return employeeWithProject;
  }


  /**
   * Parse a String to a LocalDate object.
   *
   * @param dateString String to be parsed
   * @return LocalDate object of the dateString
   */

  private LocalDate parseDate(String dateString) {
    if (dateString == null || dateString.equals("NULL")) {
      //if toDate is null - use LocalDate.now()
      return LocalDate.now();
    }
    // TO DO: Include additional DateTimeFormats
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return LocalDate.parse(dateString, dateFormatter);
  }


  /**
   * Check if a String can be parsed to a LocalDate object.
   *
   * @param currDate String to be parsed
   * @return true if the String can be parsed to a LocalDate, false if not
   */
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

  /**
   * Check if a String can be parsed to a Long.
   *
   * @param currId String to be parsed
   * @return true if the String can be parsed to a Long, false if not
   */

  public static boolean canParseLong(String currId) {
    try {
      Long.parseLong(currId);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }


  /**
   * Collect unique project IDs in a list.
   *
   * @param employeeWithProjectList List of all EmployeesWithProject
   * @return A set with unique project IDs.
   */
  public Set<Long> getUniqueProjectIds(List<EmployeeWithProject> employeeWithProjectList) {
    return employeeWithProjectList.stream()
                                  .map(EmployeeWithProject::getProjectId)
                                  .collect(Collectors.toSet());
  }

  /**
   * Generate a SolutionResult object of a project, the pair of employees
   * with longest overlay and their overlap days
   *
   * @param  projectsAndUsersMap Hashmap of a project and all employees that worked on it
   * @return A List of SolutionResult instances for each project
   */
  public List<SolutionResult> getSolutionResultsBasedOnProjects(
      HashMap<Long, List<Employee>> projectsAndUsersMap) {
    List<SolutionResult> solutionResultList = new ArrayList<>();
    // for each project and it's employee list
    for (Map.Entry<Long, List<Employee>> entry : projectsAndUsersMap.entrySet()) {
      Long projectId = entry.getKey();
      List<Employee> employeesList = entry.getValue();

      // get the pair of employees with longest overlap
      LongestPair longestPair = getLongestPeriodForProject(employeesList);

      // populate the SolutionResult for this projectId
      SolutionResult solutionResult = new SolutionResult();
      solutionResult.setProjectId(projectId);
      solutionResult.setEmployeeOneId(longestPair.getEmployeeOneId());
      solutionResult.setEmployeeTwoId(longestPair.getEmployeeTwoId());
      solutionResult.setDaysWorkedTogether(longestPair.getOverlapDays());

      // if only one employee has worked on this project - ignore the solution
      if (solutionResult.getEmployeeOneId() != null && solutionResult.getEmployeeTwoId() != null) {
        solutionResultList.add(solutionResult);
      }
    }
    return solutionResultList;
  }

  /**
   * Identifies the pair of employees that worked together for the longest period of time.
   *
   * @param  employeesList List of all Employees that worked on the same project
   * @return A LongestPair instance with the IDs of the two employees and their overlap period
   */
  private LongestPair getLongestPeriodForProject(List<Employee> employeesList) {
    LongestPair longestPair = new LongestPair();
    int longestOverlap = 0;

    for (int i = 0; i < employeesList.size() - 1; i++) {
      for (int j = i + 1; j < employeesList.size(); j++) {

        Employee employee1 = employeesList.get(i);
        Employee employee2 = employeesList.get(j);

        // overlapStarts with the starting date of the employee joining the project last
        LocalDate overlapStart = employee1.getDateFrom().isAfter(employee2.getDateFrom())
            ? employee1.getDateFrom() : employee2.getDateFrom();
        // overlapEnds with the end date of the employee leaving the project first
        LocalDate overlapEnd = employee1.getDateTo().isBefore(employee2.getDateTo())
            ? employee1.getDateTo() : employee2.getDateTo();

        // check if there is an overlap between employees
        if (overlapEnd.isAfter(overlapStart) || overlapEnd.isEqual(overlapStart)) {
          int overlapDays = (int) ChronoUnit.DAYS.between(overlapStart, overlapEnd);
          // update the longest overlap if the current overlap is longer
          if (overlapDays > longestOverlap) {
            longestOverlap = overlapDays;

            // update the LongestPair object with the new longest overlap and employee IDs
            longestPair.setOverlapDays(longestOverlap);
            longestPair.setEmployeeOneId(employee1.getEmployeeId());
            longestPair.setEmployeeTwoId(employee2.getEmployeeId());
          }
        }
      }
    }
    return longestPair;
  }

  /**
   * Collects all employees that worked on the same project
   *
   * @param  employeeWithProjectList List of all EmployeesWithProject
   * @param  projectIds Set of all project IDs
   * @return A hashmap of project IDs and the employees working on each project.
   */
  public HashMap<Long, List<Employee>> filterUsersInProject(
      List<EmployeeWithProject> employeeWithProjectList,
      Set<Long> projectIds) {
    HashMap<Long, List<Employee>> projectsAndUsersMap = new HashMap<>();
    // for each project
    for (Long currProjectId : projectIds) {
      //list of employees who worked on the project
      List<Employee> filteredSolutionsPerProject = new ArrayList<>();
      for (EmployeeWithProject employeeWithProject : employeeWithProjectList) {
        //if employee worked on the project
        if (employeeWithProject.getProjectId().equals(currProjectId)) {
          // add to list of employees
          filteredSolutionsPerProject.add(employeeWithProject);
        }
      }
      // populate map
      projectsAndUsersMap.put(currProjectId, filteredSolutionsPerProject);
    }
    return projectsAndUsersMap;
  }

}
