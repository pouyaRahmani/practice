package com.example.practice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

public class LoginController {

    @FXML
    private TextField userNameTextField;

    @FXML
    private PasswordField passwordTextField; // Changed to PasswordField for better security
    private static final String FILENAME = "Employees.ser";

    @FXML
    public void switchToSignUp(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("signup.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load sign-up FXML file.");
        }
    }


    @FXML
    public void onSignInButtonClick(ActionEvent actionEvent) {
        String username = userNameTextField.getText();
        String password = passwordTextField.getText();

        if (validateLogin(username, password)) {
            Employee employee = authenticateEmployee(username, password, FILENAME);

            if (employee != null) {
                try {
                    FXMLLoader loader;
                    Parent dashboard;
                    if (employee.isManager()) {
                        loader = new FXMLLoader(getClass().getResource("manager_dashboard.fxml"));
                        loader.setControllerFactory(param -> new ManagerDashboardController(employee));
                    } else {
                        loader = new FXMLLoader(getClass().getResource("employee_dashboard.fxml"));
                        loader.setControllerFactory(param -> new EmployeeDashboardController(employee));
                    }

                    dashboard = loader.load();
                    Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(dashboard));
                    stage.setTitle("Dashboard");
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to load the dashboard.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Employee not found.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid credentials. Please try again.");
        }
    }


    private boolean validateLogin(String username, String password) {
        Set<Employee> employees = readEmployeesFromFile(FILENAME);
        if (employees == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "Could not read employees from file.");
            return false;
        }

        // Trimming input
        username = username.trim();
        password = password.trim();

        for (Employee employee : employees) {
            if (employee != null && employee.getUserName() != null && employee.getPassword() != null) {
                System.out.println("Checking employee: " + employee.getUserName());
                if (employee.getUserName().trim().equals(username) && employee.getPassword().trim().equals(password)) {
                    System.out.println("Login successful for user: " + username);
                    return true;
                }
            }
        }

        System.out.println("Invalid credentials for user: " + username);
        return false;
    }

    private static Set<Employee> readEmployeesFromFile(String filename) {
        Set<Employee> employees = null;
        File file = new File(FILENAME);

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

    private Employee authenticateEmployee(String username, String password, String filename) {
        Set<Employee> employees = readEmployeesFromFile(FILENAME);

        for (Employee employee : employees) {
            if (employee != null && employee.getUserName() != null && employee.getPassword() != null) {
                if (employee.getUserName().equals(username) && employee.getPassword().equals(password)) {
                    return employee;
                }
            }
        }
        return null;
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
