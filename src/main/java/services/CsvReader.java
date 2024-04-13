package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import models.EmployeeWithProject;

@Service
public class CsvReader {
  // Separator used to split CSV lines into elements
  private final String SEPARATOR = ",";

  /**
   * Reads a CSV file located at the specified path and converts its content into a list of
   * EmployeeWithProject objects.
   *
   * @param pathAndFile The path to the CSV file.
   * @return A list of EmployeeWithProject objects representing the data read from the CSV file.
   * @throws IOException If an I/O error occurs while reading the file.
   */

  public List<EmployeeWithProject> readCsv(String pathAndFile) throws IOException {
    SolutionsService solutionsService = new SolutionsService();
    List<EmployeeWithProject> employeeWithProjectList = new ArrayList<>();
    try (
        BufferedReader br = Files.newBufferedReader(Path.of(pathAndFile), StandardCharsets.UTF_8)) {
      String line;
      while ((line = br.readLine()) != null) {
        // Remove extra spaces after commas for consistent parsing
        String formattedLine = line.replaceAll(",\\s*", ",");
        String[] lineElements = formattedLine.split(SEPARATOR);
        EmployeeWithProject employeeWithProject = solutionsService.returnSolutionFromLine(
            lineElements);
        if (employeeWithProject != null) {
          employeeWithProjectList.add(employeeWithProject);
        }
      }
    }
    return employeeWithProjectList;
  }
}
