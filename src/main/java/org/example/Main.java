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
    Options options = new Options();
    options.addOption("f", "file", true, "path and csv file");
    CommandLineParser parser = new DefaultParser();
    try {
      CommandLine line = parser.parse(options, args);
      String pathAndFile = line.getOptionValue("f", "C:\\");
      CsvReader csvReader = new CsvReader();
      SolutionsService solutionsService = new SolutionsService();

      // read csv
      // catch duplicates
      // catch not existing places except lastDate
      //catch not Long for id
      // ignore and log maybe?
      List<EmployeeWithProject> employeeWithProjectList = csvReader.readCsv(pathAndFile);

      // pair of employees who have worked
      //together on common projects for the longest period of time.
      ///////
      // get the final list
      // for loop that fucker
      // create list of UserProjectTimeLine


      // create List of Unique projectIds
      Set<Long> projectIds = solutionsService.getUniqueProjectIds(employeeWithProjectList);

      // create HashMap
      // we will have to use Solution instead of UserProject.....

      HashMap<Long, List<Employee>> projectsAndUsersMap = solutionsService.filterUsersInProject(
          employeeWithProjectList, projectIds);

      // create Result List
      List<SolutionResult> solutionResultList = solutionsService.getSolutionResultsBasedOnProjects(
          projectsAndUsersMap);
      for(SolutionResult result: solutionResultList){
        System.out.printf("Project ID: %s, Total Days: %s, Emp1: %s, Emp2: %s%n", result.getProjectId(), result.getDaysWorkedTogether(), result.getEmployeeOneId(), result.getEmployeeTwoId());
      }

    } catch (ParseException exp) {
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
    }
  }
}