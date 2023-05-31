package com.dharani.employeemanagementsystem.service;


import com.dharani.employeemanagementsystem.dto.EmployeeDTO;
import com.dharani.employeemanagementsystem.dto.UpdateEmployeeRequestDTO;
import com.dharani.employeemanagementsystem.entity.Employee;
import com.dharani.employeemanagementsystem.exception.ApplicationException;
import com.dharani.employeemanagementsystem.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;

@Service()
@Transactional
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Integer addEmployee(EmployeeDTO employeeDTO) throws ApplicationException {

        if (employeeRepository.existsByAadhar(employeeDTO.getAadhar())) {
            throw new ApplicationException("Employee with this aadhar already exist!");
        }

        Employee employee = Employee.builder()
                .id(employeeDTO.getId())
                .name(employeeDTO.getName())
                .aadhar(employeeDTO.getAadhar())
                .age(calculateAgeFrom(employeeDTO.getDateOfBirth()))
                .dateOfBirth(employeeDTO.getDateOfBirth())
                .dept(employeeDTO.getDept())
                .city(employeeDTO.getCity())
                .build();

        employeeRepository.save(employee);
        return employee.getId();

    }

    private Integer calculateAgeFrom(LocalDate dateOfBirth) {
        LocalDate currentDate = LocalDate.now();
        Period period = Period.between(dateOfBirth, currentDate);
        return period.getYears();
        // return period.getYears() + " years " + period.getMonths() + " months " + period.getDays() + " days.";
    }

    public EmployeeDTO getEmployee(Integer employeeId) throws ApplicationException {
        Optional<Employee> optional = employeeRepository.findById(employeeId);
        Employee employee = optional.orElseThrow(() -> new ApplicationException("Employee does not exist"));

        return EmployeeDTO.builder().id(employee.getId()).name(employee.getName()).aadhar(employee.getAadhar()).age(employee.getAge()).dateOfBirth(employee.getDateOfBirth()).dept(employee.getDept()).city(employee.getCity()).build();
    }

    public List<EmployeeDTO> getAllEmployees() throws ApplicationException {

        List<Employee> employees = employeeRepository.findAll();

        if (employees.isEmpty()) {
            throw new ApplicationException("Employees does not exist");
        }

        List<EmployeeDTO> employeeDTOS = new ArrayList<>();
        employees.forEach(employee -> {
            EmployeeDTO employeeDTO = EmployeeDTO.builder()
                    .id(employee.getId())
                    .name(employee.getName())
                    .aadhar(employee.getAadhar())
                    .age(employee.getAge())
                    .dateOfBirth(employee.getDateOfBirth())
                    .dept(employee.getDept())
                    .city(employee.getCity()).build();

            employeeDTOS.add(employeeDTO);
        });

        return employeeDTOS;
    }

    public void updateEmployeeDepartment(UpdateEmployeeRequestDTO updateEmployeeRequestDTO) throws ApplicationException {
        Optional<Employee> employee = employeeRepository.findByAadhar(updateEmployeeRequestDTO.getAadhar());
        Employee employeeToUpdate = employee.orElseThrow(() -> new ApplicationException("Employee with this aadhar doesn't exist!"));
        employeeToUpdate.setDept(updateEmployeeRequestDTO.getDept());
        employeeRepository.save(employeeToUpdate);
    }

    public void deleteEmployee(Integer employeeId) throws ApplicationException {
        Optional<Employee> employee = employeeRepository.findById(employeeId);
        employee.orElseThrow(() -> new ApplicationException("Employee with this Id doesn't exist!"));
        employeeRepository.deleteById(employeeId);
    }
}
