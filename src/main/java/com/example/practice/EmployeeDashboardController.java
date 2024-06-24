package com.example.practice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class EmployeeDashboardController implements EmployeeDashboardControllerInterface {

    @FXML
    private TextArea resultTextArea;

    private Employee employee;
    private static final String FILENAME = "Employees.ser";

    public EmployeeDashboardController(Employee employee) {
        this.employee = employee;
    }
@Override
    @FXML
public void handleViewEarningsById(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter user ID:");
        if (id != -1) {
            double earnings = Employee.calculateEarnings(id, FILENAME);
            resultTextArea.appendText("Total earnings: " + earnings);
        }
    }
@Override
    @FXML
public void handleViewPaymentHistory(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter employee ID:");
        if (id != -1) {
            Set<Employee> employees = Employee.readEmployeesFromFile(FILENAME);
            for (Employee employee : employees) {
                if (employee.getId() == id) {
                    resultTextArea.appendText("Payment History for Employee ID " + id + ":\n");
                    for (Salary salary : employee.getPaymentHistory()) {
                        resultTextArea.appendText(salary.toString() + "\n");
                        if (salary.activeSalary) {
                            resultTextArea.appendText("(Active Salary)\n");
                        }
                    }
                    return;
                }
            }
            resultTextArea.appendText("Employee not found.");
        }
    }

@Override
    @FXML
public void handleSearchUserById(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter user ID:");
        if (id != -1) {
            Employee foundEmployee = Employee.findById(id, FILENAME);
            if (foundEmployee != null) {
                resultTextArea.appendText("User found: " + foundEmployee);
            } else {
                resultTextArea.appendText("User not found.");
            }
        }
    }
@Override
    @FXML
public void handleSearchUserBySalaryType(ActionEvent event) {
        resultTextArea.clear();
        int salaryTypeChoice = getUserInputAsInt("Enter salary type (1: Fixed, 2: Hourly, 3: Commission, 4: Base Plus Commission):");
        if (salaryTypeChoice != -1) {
            Class<? extends Salary> salaryType = null;
            switch (salaryTypeChoice) {
                case 1:
                    salaryType = Fixed.class;
                    break;
                case 2:
                    salaryType = HourlyWage.class;
                    break;
                case 3:
                    salaryType = Commission.class;
                    break;
                case 4:
                    salaryType = BasePlusCommission.class;
                    break;
                default:
                    resultTextArea.appendText("Invalid salary type. Please try again.");
                    return;
            }

            ArrayList<Employee> result = Employee.searchBySalaryType(salaryType, FILENAME);
            if (result.isEmpty()) {
                resultTextArea.appendText("No employees found with the specified salary type.");
            } else {
                resultTextArea.appendText("Employees with specified salary type:\n");
                for (Employee e : result) {
                    resultTextArea.appendText(e.toString() + "\n");
                }
            }
        }
    }

@Override
    @FXML
public void handleShowAllEmployees(ActionEvent event) {
        resultTextArea.clear();
        Set<Employee> employees = Employee.readEmployeesFromFile(FILENAME);
        resultTextArea.appendText("All employees:\n");
        for (Employee employee : employees) {
            resultTextArea.appendText(employee.toString() + "\n");
        }
    }
    @Override
    @FXML
    public void handleViewAllArchivedEmployees() {
        List<Employee> archivedEmployees = Employee.showAllArchivedEmployees(FILENAME);
        resultTextArea.clear();
        resultTextArea.appendText("Total archived employees: " + archivedEmployees.size() + "\n\n");
        resultTextArea.appendText("Archived Employee list:\n");
        for (Employee employee : archivedEmployees) {
            resultTextArea.appendText(employee.toString() + "\n\n");
        }
    }
    @Override
    @FXML
    public void handleShowAllManagers(ActionEvent event) {
        resultTextArea.clear();
        Set<Employee> employees = Employee.readEmployeesFromFile(FILENAME);
        resultTextArea.appendText("All managers:\n");
        for (Employee employee : employees) {
            if (employee.isManager()) {
                resultTextArea.appendText(employee.toString() + "\n");
            }
        }
    }
@Override
    @FXML
public void handleUpdateProfile(ActionEvent event) {
        resultTextArea.clear();
        int id = getUserInputAsInt("Enter employee ID:");
        if (id != -1) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Input Required");
            dialog.setHeaderText(null);
            dialog.setContentText("Enter username:");
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String username = result.get();
                Set<Employee> employees = Employee.readEmployeesFromFile(FILENAME);
                Employee employeeToUpdate = null;
                for (Employee employee : employees) {
                    if (employee.getId() == id && employee.getUserName().equals(username)) {
                        employeeToUpdate = employee;
                        break;
                    }
                }
                if (employeeToUpdate == null) {
                    resultTextArea.appendText("Employee with ID " + id + " and username " + username + " not found.");
                    return;
                }

                resultTextArea.appendText("Current details of the employee:\n");
                resultTextArea.appendText(employeeToUpdate.toString() + "\n");

                TextInputDialog updateDialog = new TextInputDialog();
                updateDialog.setTitle("Update Profile");
                updateDialog.setHeaderText(null);
                updateDialog.setContentText("Select field to update (1: First Name, 2: Last Name, 3: Social Security Number, 4: Birth Date):");
                Optional<String> fieldChoice = updateDialog.showAndWait();
                if (fieldChoice.isPresent()) {
                    int choice = Integer.parseInt(fieldChoice.get());
                    switch (choice) {
                        case 1:
                            updateDialog.setContentText("Enter new first name:");
                            Optional<String> newFirstName = updateDialog.showAndWait();
                            if (newFirstName.isPresent()) {
                                employeeToUpdate.setFirstName(newFirstName.get());
                            }
                            break;
                        case 2:
                            updateDialog.setContentText("Enter new last name:");
                            Optional<String> newLastName = updateDialog.showAndWait();
                            if (newLastName.isPresent()) {
                                employeeToUpdate.setLastName(newLastName.get());
                            }
                            break;
                        case 3:
                            updateDialog.setContentText("Enter new social security number:");
                            Optional<String> newSSN = updateDialog.showAndWait();
                            if (newSSN.isPresent()) {
                                employeeToUpdate.setSocialSecurityNumber(newSSN.get());
                            }
                            break;
                        case 4:
                            updateDialog.setContentText("Enter new birth date (yyyy-MM-dd):");
                            Optional<String> newBirthDateStr = updateDialog.showAndWait();
                            if (newBirthDateStr.isPresent()) {
                                Date newBirthDate = Date.valueOf(newBirthDateStr.get());
                                employeeToUpdate.setBirthDate(newBirthDate);
                            }
                            break;
                        default:
                            resultTextArea.appendText("Invalid choice. Please try again.");
                            return;
                    }

                    Employee.writeEmployeesToFile(employees, FILENAME);
                    resultTextArea.appendText("Employee details updated successfully.");
                }
            }
        }
    }


@Override
    @FXML
public void handleViewDepartmentEarnings(ActionEvent event) {
        resultTextArea.clear();
        int departmentId = getUserInputAsInt("Enter department ID:");
        if (departmentId != -1) {
            double departmentEarnings = Employee.calculateDepartmentEarnings(departmentId, FILENAME);
            if (departmentEarnings == 0) {
                resultTextArea.appendText("No earnings for the department.");
            } else {
                resultTextArea.appendText("Total department earnings: " + departmentEarnings);
            }
        }
    }
@Override
    @FXML
public void handleViewAllEmployeesEarnings(ActionEvent event) {
        resultTextArea.clear();
        double totalEarnings = Employee.calculateAllEmployeesEarnings(FILENAME);
        resultTextArea.appendText("Total earnings of all employees: " + totalEarnings);
    }
@Override
    @FXML
public void handleLogout(ActionEvent event) {
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

    private int getUserInputAsInt(String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Input Required");
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        Optional<String> result = dialog.showAndWait();
        try {
            return result.map(Integer::parseInt).orElse(-1);
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number.");
            return -1;
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
