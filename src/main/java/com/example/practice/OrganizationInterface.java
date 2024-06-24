package com.example.practice;

import java.util.List;

public interface OrganizationInterface {
    void addDepartment(int id, String name);
    List<String> showAllDepartments();
    List<Department> getDepartments();
    void loadDepartments();
    void saveDepartments();
    boolean isValidDepartmentId(int id);
    void changeEmployeeDepartment(int employeeId, int newDepartmentId, String filename);
}
