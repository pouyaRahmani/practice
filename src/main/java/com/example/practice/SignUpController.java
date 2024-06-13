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
    private TextField employeeIdTextField;

    @FXML
    private DatePicker salaryStartDatePicker;

    @FXML
    private DatePicker salaryEndDatePicker;

    @FXML
    private ComboBox<Boolean> activeSalaryComboBox;

    @FXML
    private void initialize() {
        managerComboBox.setItems(FXCollections.observableArrayList(true, false));
        salaryTypeComboBox.setItems(FXCollections.observableArrayList("Fixed", "Hourly", "Commission", "Base Plus Commission"));
        activeSalaryComboBox.setItems(FXCollections.observableArrayList(true, false));

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
            boolean isActive = activeSalaryComboBox.getValue();
            String employeeId = employeeIdTextField.getText().trim();
            LocalDate salaryStartDate = salaryStartDatePicker.getValue();
            LocalDate salaryEndDate = salaryEndDatePicker.getValue();

            if (firstName.isEmpty() || lastName.isEmpty() || ssn.isEmpty() || username.isEmpty() ||
                    password.isEmpty() || confirmPassword.isEmpty() || salaryType == null ||
                    birthDate == null || salaryStartDate == null || salaryEndDate == null) {
                throw new IllegalArgumentException("All fields must be filled out.");
            }

            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }

            double salary1 = Double.parseDouble(salaryField1.getText().trim());
            double salary2 = salaryField2.isVisible() ? Double.parseDouble(salaryField2.getText().trim()) : 0;
            double salary3 = salaryField3.isVisible() ? Double.parseDouble(salaryField3.getText().trim()) : 0;

            java.util.Date birthDateConverted = java.sql.Date.valueOf(birthDate);
            java.util.Date salaryStartDateConverted = java.sql.Date.valueOf(salaryStartDate);
            java.util.Date salaryEndDateConverted = java.sql.Date.valueOf(salaryEndDate);

            Employee employee = new Employee(firstName, lastName, ssn, birthDateConverted, username, password, departmentId, isManager, salaryType, salary1, salary2, salary3, employeeId, salaryStartDateConverted, salaryEndDateConverted, isActive);

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
