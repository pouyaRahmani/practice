package com.example.practice;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class SignUpController {

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField ssnTextField;

    @FXML
    private DatePicker birthDatePicker;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private PasswordField confirmPasswordTextField;

    @FXML
    private TextField departmentIdTextField;

    @FXML
    private ComboBox<Boolean> managerComboBox;

    @FXML
    private ComboBox<String> salaryTypeComboBox;

    @FXML
    private TextField salaryField1;

    @FXML
    private TextField salaryField2;

    @FXML
    private TextField salaryField3;
    @FXML
    private TextField managerBaseSalaryTextField;

    @FXML
    private TextField employeeIdTextField;

    @FXML
    private DatePicker salaryStartDatePicker;

    @FXML
    private DatePicker salaryEndDatePicker;

    @FXML
    private ComboBox<Boolean> arciveEmployeeComboBox;
    @FXML
    private ComboBox<Activity> inactiveReasonComboBox; // Add this ComboBox for inactive reasons
    private Organization organization;
    public SignUpController() {
        organization = new Organization(); // Initialize the Organization instance
    }

    @FXML
    private void initialize() {
        managerComboBox.setItems(FXCollections.observableArrayList(true, false));
        salaryTypeComboBox.setItems(FXCollections.observableArrayList("Fixed", "Hourly", "Commission", "Base Plus Commission"));
        arciveEmployeeComboBox.setItems(FXCollections.observableArrayList(false, true));
        inactiveReasonComboBox.setItems(FXCollections.observableArrayList(Activity.values())); // Set items for the ComboBox

        salaryTypeComboBox.setOnAction(event -> {
            String selectedSalaryType = salaryTypeComboBox.getValue();
            switch (selectedSalaryType) {
                case "Fixed":
                    salaryField1.setPromptText("Monthly Salary");
                    salaryField2.setVisible(false);
                    salaryField3.setVisible(false);
                    break;
                case "Hourly":
                    salaryField1.setPromptText("Hourly Wage");
                    salaryField2.setPromptText("Hours Worked");
                    salaryField2.setVisible(true);
                    salaryField3.setVisible(false);
                    break;
                case "Commission":
                    salaryField1.setPromptText("Commission Rate");
                    salaryField2.setPromptText("Gross Sales");
                    salaryField2.setVisible(true);
                    salaryField3.setVisible(false);
                    break;
                case "Base Plus Commission":
                    salaryField1.setPromptText("Base Salary");
                    salaryField2.setPromptText("Commission Rate");
                    salaryField3.setPromptText("Gross Sales");
                    salaryField2.setVisible(true);
                    salaryField3.setVisible(true);
                    break;
            }
        });

        arciveEmployeeComboBox.setOnAction(event -> {
            boolean isArchived = arciveEmployeeComboBox.getValue();
            inactiveReasonComboBox.setDisable(!isArchived);
            inactiveReasonComboBox.setVisible(isArchived);
        });
        // Configure managerBaseSalaryTextField visibility based on managerComboBox selection
        managerComboBox.setOnAction(event -> {
            boolean isManager = managerComboBox.getValue();
            managerBaseSalaryTextField.setVisible(isManager);
            if (!isManager) {
                managerBaseSalaryTextField.clear(); // Clear the field if not visible
            }
        });
    }



    @FXML
    private void onSignUpButtonClick() {
        try {
            System.out.println("SignUp button clicked");

            String firstName = firstNameTextField.getText().trim();
            String lastName = lastNameTextField.getText().trim();
            String ssn = ssnTextField.getText().trim();
            LocalDate birthDate = birthDatePicker.getValue();
            String username = usernameTextField.getText().trim();
            String password = passwordTextField.getText();
            String confirmPassword = confirmPasswordTextField.getText();
            int departmentId = Integer.parseInt(departmentIdTextField.getText().trim());
            boolean isManager = managerComboBox.getValue();
            String salaryType = salaryTypeComboBox.getValue();
            boolean isActive = arciveEmployeeComboBox.getValue(); // Determine if the employee is archived
            int employeeId = Integer.parseInt(employeeIdTextField.getText().trim());
            LocalDate salaryStartDate = salaryStartDatePicker.getValue();
            LocalDate salaryEndDate = salaryEndDatePicker.getValue();
            Activity status = isActive ? inactiveReasonComboBox.getValue() : Activity.ACTIVE; // Set status based on archive state

            if (firstName.isEmpty() || lastName.isEmpty() || ssn.isEmpty() || username.isEmpty() ||
                    password.isEmpty() || confirmPassword.isEmpty() || salaryType == null ||
                    birthDate == null || salaryStartDate == null || salaryEndDate == null) {
                throw new IllegalArgumentException("All fields must be filled out.");
            }

            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }

            if (isUsernameExists(username, "Employees.ser")) {
                throw new IllegalArgumentException("Username already exists.");
            }

            if (isEmployeeIdExists(employeeId, "Employees.ser")) {
                throw new IllegalArgumentException("Employee ID already exists.");
            }
            // Check if the department ID is valid
            if (!organization.isValidDepartmentId(departmentId)) {
                throw new IllegalArgumentException("Invalid department ID.");
            }

            Set<Employee> employees = readEmployeesFromFile("Employees.ser");

            if (isManager && isDepartmentHasManager(departmentId, employees)) {
                throw new IllegalArgumentException("The department already has a manager.");
            }

            double salary1 = Double.parseDouble(salaryField1.getText().trim());
            double salary2 = salaryField2.isVisible() ? Double.parseDouble(salaryField2.getText().trim()) : 0;
            double salary3 = salaryField3.isVisible() ? Double.parseDouble(salaryField3.getText().trim()) : 0;
            double managerBaseSalary = managerBaseSalaryTextField.isVisible() ? Double.parseDouble(managerBaseSalaryTextField.getText().trim()) : 0;


            java.util.Date birthDateConverted = java.sql.Date.valueOf(birthDate);
            java.util.Date salaryStartDateConverted = java.sql.Date.valueOf(salaryStartDate);
            java.util.Date salaryEndDateConverted = java.sql.Date.valueOf(salaryEndDate);

            Activity inactiveReason = isActive ? inactiveReasonComboBox.getValue() : null;

            Employee employee = new Employee(firstName, lastName, ssn, birthDateConverted, username, password, departmentId, isManager, salaryType, salary1, salary2, salary3, employeeId, salaryStartDateConverted, salaryEndDateConverted, isActive,status, inactiveReason);
            if (isManager) {
                employee.setManagerBaseSalary(managerBaseSalary);
            }
            employee.addDepartmentHistory(departmentId);

            saveEmployeeToFile(employee);

            System.out.println("Employee created successfully: " + employee);
        } catch (Exception e) {
            System.err.println("Error creating employee: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Set<Employee> readEmployeesFromFile(String filename) {
        Set<Employee> employees = null;
        File file = new File(filename);

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(filename);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                employees = (Set<Employee>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File does not exist: " + filename);
        }

        return employees != null ? employees : new HashSet<>();
    }

    private void saveEmployeeToFile(Employee employee) {
        System.out.println("Saving employee to file...");
        Set<Employee> employees = readEmployeesFromFile("Employees.ser");

        employees.add(employee);
        System.out.println("Employee added to set: " + employee);

        try (FileOutputStream fileOut = new FileOutputStream("Employees.ser");
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(employees);
            System.out.println("Employee data is saved in Employees.ser");
        } catch (IOException i) {
            i.printStackTrace();
        }
    }


    public static boolean isUsernameExists(String username, String filename) {
        Set<Employee> employees = readEmployeesFromFile(filename);
        for (Employee employee : employees) {
            if (employee.getUserName().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmployeeIdExists(int id, String filename) {
        Set<Employee> employees = readEmployeesFromFile(filename);
        for (Employee employee : employees) {
            if (employee.getId() == id) {
                return true;
            }
        }
        return false;
    }

    // Check if a department already has a manager
    public boolean isDepartmentHasManager(int departmentId, Set<Employee> employees) {
        for (Employee employee : employees) {
            if (employee.isManager() && employee.getDepartmentId() == departmentId) {
                return true;
            }
        }
        return false;
    }

    @FXML
    private void switchToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load login FXML file.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
