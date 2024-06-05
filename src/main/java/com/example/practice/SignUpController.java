package com.example.practice;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.sql.Date;
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
    private TextField passwordTextField;

    @FXML
    private TextField confirmPasswordTextField;

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
    public void switchToLogin(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // نمایش پیام خطا
            System.out.println("Failed to load login FXML file.");
        }
    }

    @FXML
    public void onSignUpButtonClick(ActionEvent actionEvent) {
        if (validateInput()) {
            Employee newEmployee = createEmployeeFromInput();
            if (saveEmployeeToFile(newEmployee, "Employees.dat")) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Employee registered successfully.");
                switchToLogin(actionEvent);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to save employee. Please try again.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid input. Please check your input and try again.");
        }
    }

    private boolean validateInput() {
        if (usernameTextField.getText().isEmpty() ||
                passwordTextField.getText().isEmpty() ||
                !passwordTextField.getText().equals(confirmPasswordTextField.getText())) {
            return false;
        }
        return true;
    }

    private Employee createEmployeeFromInput() {
        String firstName = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String ssn = ssnTextField.getText();
        Date birthDate = Date.valueOf(birthDatePicker.getValue());
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();
        int departmentId = Integer.parseInt(departmentIdTextField.getText());
        boolean isManager = managerComboBox.getValue();
        String salaryType = salaryTypeComboBox.getValue();
        double salary1 = Double.parseDouble(salaryField1.getText());
        double salary2 = salaryField2.isVisible() ? Double.parseDouble(salaryField2.getText()) : 0.0;
        double salary3 = salaryField3.isVisible() ? Double.parseDouble(salaryField3.getText()) : 0.0;
        String employeeId = employeeIdTextField.getText();
        Date salaryStartDate = Date.valueOf(salaryStartDatePicker.getValue());
        Date salaryEndDate = salaryEndDatePicker.getValue() != null ? Date.valueOf(salaryEndDatePicker.getValue()) : null;
        boolean isActive = activeSalaryComboBox.getValue();

        return new Employee(firstName, lastName, ssn, birthDate, username, password, departmentId, isManager, salaryType, salary1, salary2, salary3, employeeId, salaryStartDate, salaryEndDate, isActive);
    }

    private boolean saveEmployeeToFile(Employee employee, String filename) {
        Set<Employee> employees = readEmployeesFromFile(filename);

        if (employees == null) {
            employees = new HashSet<>();
        }

        employees.add(employee);

        try (FileOutputStream fos = new FileOutputStream(filename);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(employees);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
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
        }

        return employees;
    }



    @FXML
    private void clearFields() {
        // Clear text fields
        firstNameTextField.clear();
        lastNameTextField.clear();
        ssnTextField.clear();
        usernameTextField.clear();
        passwordTextField.clear();
        confirmPasswordTextField.clear();
        departmentIdTextField.clear();
        salaryField1.clear();
        salaryField2.clear();
        salaryField3.clear();
        employeeIdTextField.clear();

        // Clear date pickers
        birthDatePicker.setValue(null);
        salaryStartDatePicker.setValue(null);
        salaryEndDatePicker.setValue(null);

        // Clear combo boxes
        managerComboBox.setValue(null);
        salaryTypeComboBox.setValue(null);
        activeSalaryComboBox.setValue(null);
    }



    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
