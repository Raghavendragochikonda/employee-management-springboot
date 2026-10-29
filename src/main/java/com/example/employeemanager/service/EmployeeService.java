package com.example.employeemanager.service;

import com.example.employeemanager.model.Employee;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class EmployeeService {
    private final AtomicLong sequence = new AtomicLong(3);
    private final List<Employee> employees = new CopyOnWriteArrayList<>(List.of(
        new Employee(1L, "Ananya Rao", "ananya@example.com", "Engineering", "Software Engineer"),
        new Employee(2L, "Rahul Mehta", "rahul@example.com", "HR", "HR Executive"),
        new Employee(3L, "Priya Sharma", "priya@example.com", "Finance", "Analyst")
    ));

    public List<Employee> findAll() {
        return List.copyOf(employees);
    }

    public Employee findById(long id) {
        return employees.stream().filter(employee -> employee.id() == id).findFirst().orElse(null);
    }

    public Employee create(String name, String email, String department, String role) {
        Employee employee = new Employee(sequence.incrementAndGet(), name, email, department, role);
        employees.add(employee);
        return employee;
    }

    public boolean delete(long id) {
        return employees.removeIf(employee -> employee.id() == id);
    }
}
