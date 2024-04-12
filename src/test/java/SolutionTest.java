import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.springframework.util.Assert;

import models.Employee;
import models.EmployeeWithProject;
import models.SolutionResult;
import services.CsvReader;
import services.SolutionsService;

public class SolutionTest {

  public void testMethod1() throws IOException {
    System.out.println("Running test method 1");
    String pathAndFile = "src/test/java/checkTwo.csv";

    CsvReader csvReader = new CsvReader();
    SolutionsService solutionsService = new SolutionsService();
    List<EmployeeWithProject> employeeWithProjectList = csvReader.readCsv(pathAndFile);

    Set<Long> projectIds = solutionsService.getUniqueProjectIds(employeeWithProjectList);

    HashMap<Long, List<Employee>> projectsAndUsersMap = solutionsService.filterUsersInProject(
        employeeWithProjectList, projectIds);

    List<SolutionResult> solutionResultList = solutionsService.getSolutionResultsBasedOnProjects(
        projectsAndUsersMap);

    if(solutionResultList.size() == 5){
      System.out.println("Test OKAY");
    } else {
      System.out.println("Test FAIL");
    }
  }

  public static void main(String[] args) throws IOException {
    SolutionTest testClass = new SolutionTest();
    testClass.testMethod1();
  }
}
