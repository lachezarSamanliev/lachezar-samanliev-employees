package org.example;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import models.Employee;
import models.EmployeeWithProject;
import models.SolutionResult;
import services.CsvReader;
import services.SolutionsService;

public class Main {

  public static void main(String[] args) throws IOException {

    // init command line options
    Options options = new Options();
    options.addOption("f", "file", true, "path and csv file");

    // init comman line parser
    CommandLineParser parser = new DefaultParser();
    try {
      // parse command line arguments
      CommandLine line = parser.parse(options, args);
      String pathAndFile = line.getOptionValue("f", "C:\\");

      // init csvReader and services
      CsvReader csvReader = new CsvReader();
      SolutionsService solutionsService = new SolutionsService();

      // read csv data
      List<EmployeeWithProject> employeeWithProjectList = csvReader.readCsv(pathAndFile);

      // create List of unique projectIds
      Set<Long> projectIds = solutionsService.getUniqueProjectIds(employeeWithProjectList);

      // create HashMap of each project with a list of employees who worked on that project

      HashMap<Long, List<Employee>> projectsAndUsersMap = solutionsService.filterUsersInProject(
          employeeWithProjectList, projectIds);

      // create SolutionResult List of each project and the pair of employees with longest overlap
      List<SolutionResult> solutionResultList = solutionsService.getSolutionResultsBasedOnProjects(
          projectsAndUsersMap);
      // print solution for each project
      for(SolutionResult result: solutionResultList){
        System.out.printf("Project ID: %s, Total Days: %s, Emp1: %s, Emp2: %s%n", result.getProjectId(), result.getDaysWorkedTogether(), result.getEmployeeOneId(), result.getEmployeeTwoId());
      }

    } catch (ParseException exp) {
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
    }
  }
}