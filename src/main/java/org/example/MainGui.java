package org.example;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import models.Employee;
import models.EmployeeWithProject;
import models.SolutionResult;
import services.CsvReader;
import services.SolutionsService;

class MainGUI extends JFrame {
  private JButton processButton;
  private JTextArea outputTextArea;
  private JButton openFileButton;
  private JFileChooser fileChooser;
  private JTable table;
  private String pathAndFile = "";

  public MainGUI() {
    setTitle("Tool #1");
    setSize(800, 640);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    initComponents();
    addComponents();
    setListeners();
  }

  private void initComponents() {
    processButton = new JButton("Process Solutions");
    outputTextArea = new JTextArea();
    outputTextArea.setEditable(false);
    openFileButton = new JButton("Open File");
    table = new JTable();
  }

  private void addComponents() {
    JPanel panel = new JPanel();
    panel.setLayout(new BorderLayout());
    panel.add(openFileButton, BorderLayout.NORTH);
    panel.add(processButton, BorderLayout.SOUTH);
    panel.add(new JScrollPane(table), BorderLayout.CENTER);
    add(panel);
  }

  private void setListeners() {
    processButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          processSolutions(pathAndFile);
        } catch (IOException ex) {
          ex.printStackTrace();
          outputTextArea.setText("Error processing solutions: " + ex.getMessage());
        }
      }
    });
    openFileButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          loadFile();
        } catch (IOException ex) {
          ex.printStackTrace();
          outputTextArea.setText("Error processing solutions: " + ex.getMessage());
        }
      }
    });
  }

  private void processSolutions(String filePath) throws IOException {
    if (filePath.equals("")) {
      System.out.println("No File Loaded");
      return;
    }
    // Same Logic followed as Main class
    CsvReader csvReader = new CsvReader();
    SolutionsService solutionsService = new SolutionsService();

    List<EmployeeWithProject> employeeWithProjectList = csvReader.readCsv(filePath);
    Set<Long> projectIds = solutionsService.getUniqueProjectIds(employeeWithProjectList);
    HashMap<Long, List<Employee>> projectsAndUsersMap = solutionsService.filterUsersInProject(
        employeeWithProjectList, projectIds);

    List<SolutionResult> solutionResultList = solutionsService.getSolutionResultsBasedOnProjects(
        projectsAndUsersMap);

    populateTable(solutionResultList);
  }

  private void populateTable(List<SolutionResult> solutionResultList) {
    Vector<Vector<String>> data = new Vector<>();
    Vector<String> columnNames = new Vector<>();
    columnNames.add("Employee ID #1");
    columnNames.add("Employee ID #2");
    columnNames.add("Project ID");
    columnNames.add("Days worked");

    for (SolutionResult result : solutionResultList) {
      Vector<String> currRow = new Vector<>();
      currRow.add(String.valueOf(result.getEmployeeOneId()));
      currRow.add(String.valueOf(result.getEmployeeTwoId()));
      currRow.add(String.valueOf(result.getProjectId()));
      currRow.add(String.valueOf(result.getDaysWorkedTogether()));
      data.add(currRow);
    }
    DefaultTableModel model = new DefaultTableModel(data, columnNames);
    table.setModel(model);
  }

  private void loadFile() throws IOException {
    String filePathAndName = "";
    // show only CSV files when loading
    FileFilter fileFilter = new FileNameExtensionFilter(null, "csv");
    fileChooser = new JFileChooser();
    fileChooser.setFileFilter(fileFilter);
    int response = fileChooser.showOpenDialog(null);
    if (response == JFileChooser.APPROVE_OPTION) {

      filePathAndName = fileChooser.getSelectedFile().getAbsolutePath();
      pathAndFile = filePathAndName;
    }
  }

  public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        MainGUI mainGUI = new MainGUI();
        mainGUI.setVisible(true);
      }
    });
  }
}