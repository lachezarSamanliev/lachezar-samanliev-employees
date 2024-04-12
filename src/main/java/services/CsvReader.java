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
  private final String SEPARATOR = ",";

  public List<EmployeeWithProject> readCsv(String pathAndFile) throws IOException {
    SolutionsService solutionsService = new SolutionsService();
    List<EmployeeWithProject> employeeWithProjectList = new ArrayList<>();
    try (BufferedReader br = Files.newBufferedReader(Path.of(pathAndFile), StandardCharsets.UTF_8)) {
      String line;
      while ((line = br.readLine()) != null) {
        String formattedLine = line.replaceAll(",\\s*", ",");
        String[] lineElements = formattedLine.split(SEPARATOR);
        EmployeeWithProject employeeWithProject = solutionsService.returnSolutionFromLine(lineElements);
        if(employeeWithProject != null){
          employeeWithProjectList.add(employeeWithProject);
        }
      }
    }
    return employeeWithProjectList;
  }
}
