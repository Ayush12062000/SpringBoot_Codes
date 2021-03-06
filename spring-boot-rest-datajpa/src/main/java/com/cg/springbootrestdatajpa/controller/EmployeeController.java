package com.cg.springbootrestdatajpa.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cg.springbootrestdatajpa.exception.EmployeeNotFoundException;
import com.cg.springbootrestdatajpa.model.Employee;
import com.cg.springbootrestdatajpa.service.EmployeeService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
@Api(produces = "application/json", value = "Operation related to Employee")
public class EmployeeController {
	@Autowired
	public EmployeeService employeeService;
	
	@PostMapping("/employees/new")
	@ApiOperation(value = "Create New employee")
	public Employee createEmployee(@Valid @RequestBody Employee employee) {
		return employeeService.createEmployee(employee);
	}
	@GetMapping("/employees/all")
	@ApiOperation(value = "View all employees")
	public List<Employee> getAllEmployee()
	{
		return employeeService.getAllEmployees();
	}
	//http://localhost:808/api/employees/byid/103
	@GetMapping("/employees/byid/{id}")
	@ApiOperation(value = "Retrieve Specific employee with given Id")
	public ResponseEntity<Employee> getEmployeeById(@PathVariable(value="id") Long empId) throws EmployeeNotFoundException
	{
		Employee emp = employeeService.getEmployeeById(empId).orElseThrow(()-> new EmployeeNotFoundException("No employee found with this ID:"+ empId));
		return ResponseEntity.ok().body(emp);
	}
	
	@PutMapping("/employees/update/{id}")
	@ApiOperation(value = "Update existing Employee")
	public ResponseEntity<Employee> updateEmployee(@PathVariable(value="id") Long empId,@Valid @RequestBody Employee empDetails) throws EmployeeNotFoundException
	{
		Employee emp = employeeService.getEmployeeById(empId).orElseThrow(()-> new EmployeeNotFoundException("No employee found with this ID:"+ empId));
		emp.setFirstName(empDetails.getFirstName());
		emp.setLastName(empDetails.getLastName());
		emp.setEmailId(empDetails.getEmailId());
		
		Employee updatedEmployee = employeeService.updateEmployee(emp);
		return ResponseEntity.ok(updatedEmployee);
	}
	
	@DeleteMapping("/employees/delete/id/{id}")
	@ApiOperation(value = "Delete an employee with given Id")
	public Map<String, Boolean> deleteEmployee(@PathVariable(value="id") Long empId) throws EmployeeNotFoundException
	{
		Employee emp = employeeService.getEmployeeById(empId).orElseThrow(()-> new EmployeeNotFoundException("No employee found with this ID:"+ empId));
		employeeService.deleteEmployee(emp);
		Map<String, Boolean> response = new HashMap<>();
		response.put("Deleted", Boolean.TRUE);
		return response;
	}
	
	@GetMapping("/employees/bylastname/{lastname}")
	@ApiOperation(value = "Retrieve Employees with same Last Name")
	public List<Employee> getEmployeesWithSameLastName(@PathVariable(value="lastname") String lname)
	{
		return employeeService.getEmployeesByLastName(lname);
	}
	
	@GetMapping("/employees/byemail/{e}")
	@ApiOperation(value = "Retrieve Employees with Specific Email")
	public Employee getByEmailId(@PathVariable(value="e") String email)
	{
		return employeeService.getEmployeeByEmailId(email);
	}
	
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public Map<String, String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex)
	{
		Map<String,String> errors = new HashMap<>();
		ex.getBindingResult().getFieldErrors().forEach(error ->errors.put(error.getField(), error.getDefaultMessage()));
		return errors;
	}
}
