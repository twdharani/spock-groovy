package com.dharani.employeemanagementsystem.repository;

import com.dharani.employeemanagementsystem.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    boolean existsByAadhar(String aadhar);
    Optional<Employee> findByAadhar(String aadhar);
}