package com.example.employeemanager.controller;

import com.example.employeemanager.model.Employee;
import com.example.employeemanager.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class EmployeeController {
    private static final Logger log = LoggerFactory.getLogger(EmployeeController.class);
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {
        log.info("Health endpoint called");
        return Map.of("status", "UP", "application", "employee-management-springboot");
    }

    @GetMapping("/employees")
    public List<Employee> getEmployees() {
        log.info("Fetching all employees");
        return employeeService.findAll();
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<?> getEmployee(@PathVariable long id) {
        Employee employee = employeeService.findById(id);
        if (employee == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Employee not found"));
        }
        return ResponseEntity.ok(employee);
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeRequest request) {
        if (request == null || blank(request.name()) || blank(request.email())
                || blank(request.department()) || blank(request.role())) {
            return ResponseEntity.badRequest().build();
        }
        Employee employee = employeeService.create(
            request.name().trim(), request.email().trim(),
            request.department().trim(), request.role().trim()
        );
        log.info("Created employee id={}", employee.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(employee);
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable long id) {
        boolean deleted = employeeService.delete(id);
        if (!deleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Employee not found"));
        }
        log.info("Deleted employee id={}", id);
        return ResponseEntity.noContent().build();
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }

    public record EmployeeRequest(String name, String email, String department, String role) {}
}
