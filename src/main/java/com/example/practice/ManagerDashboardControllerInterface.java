package com.example.practice;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public interface ManagerDashboardControllerInterface extends EmployeeDashboardControllerInterface {

    @FXML
    void handleArchiveUser(ActionEvent event);

    @FXML
    void handleChangeSalary(ActionEvent event);

    @FXML
    void handleGenerateRandomEmployee(ActionEvent event);

    @FXML
    void handleAddDepartment(ActionEvent event);

    @FXML
    void handleCountEmployeesInDepartment(ActionEvent event);

    @FXML
    void handleChangeEmployeeDepartment(ActionEvent event);

    @FXML
    void handleUnarchiveUser(ActionEvent event);

    @FXML
    void handleViewAllDepartments(ActionEvent event);
}

