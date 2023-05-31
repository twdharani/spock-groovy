package com.dharani.employeemanagementsystem.service

import com.dharani.employeemanagementsystem.dto.EmployeeDTO
import com.dharani.employeemanagementsystem.dto.UpdateEmployeeRequestDTO
import com.dharani.employeemanagementsystem.entity.Employee
import com.dharani.employeemanagementsystem.repository.EmployeeRepository
import com.dharani.employeemanagementsystem.exception.ApplicationException
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EmployeeServiceSpec extends Specification {

    public static final String JOHN_DOE = "John Doe"
    public static final int EMPLOYEE_ONE = 1
    public static final String AADHAR = "7282 1234 5678"
    public static final String IT_DEPARTMENT = "IT"
    public static final String NEW_YORK = "New York"

    def dateString = '21/07/2000'
    def formatter = DateTimeFormatter.ofPattern('dd/MM/yyyy')
    def localDate = LocalDate.parse(dateString, formatter)

    @Subject
    EmployeeService employeeService

    @Subject
    UpdateEmployeeRequestDTO updateEmployeeRequestDTO;

    @Shared
    EmployeeRepository employeeRepository

    def setup() {
        employeeRepository = Mock(EmployeeRepository)
        employeeService = new EmployeeService()
        employeeService.employeeRepository = employeeRepository
    }

    def "Should addEmployee when employee with aadhar doesn't exist"() {
        given:
        EmployeeDTO employeeDTO = new EmployeeDTO(
                id: EMPLOYEE_ONE,
                name: JOHN_DOE,
                aadhar: AADHAR,
                dateOfBirth: localDate,
                dept: IT_DEPARTMENT,
                city: NEW_YORK
        )

        and:
        employeeRepository.existsByAadhar(_ as String) >> false
        employeeRepository.save(_ as Employee) >> { Employee savedEmployee ->
            savedEmployee.id = EMPLOYEE_ONE
            savedEmployee
        }

        when:
        Integer result = employeeService.addEmployee(employeeDTO)

        then:
        result == EMPLOYEE_ONE
    }

    def "Should throw ApplicationException when employee with aadhar already exists"() {
        given:
        EmployeeDTO employeeDTO = new EmployeeDTO(
                id: EMPLOYEE_ONE,
                name: JOHN_DOE,
                aadhar: AADHAR,
                dateOfBirth: localDate,
                dept: IT_DEPARTMENT,
                city: NEW_YORK
        )

        and:
        employeeRepository.existsByAadhar(_ as String) >> true

        when:
        employeeService.addEmployee(employeeDTO)

        then:
        thrown(ApplicationException)
    }

    def "Should getEmployee when employee exists"() {
        given:
        Integer employeeId = EMPLOYEE_ONE
        Employee employee = new Employee(
                id: employeeId,
                name: JOHN_DOE,
                aadhar: AADHAR,
                age: 33,
                dateOfBirth: localDate,
                dept: IT_DEPARTMENT,
                city: NEW_YORK
        )

        and:
        employeeRepository.findById(_ as Integer) >> Optional.of(employee)

        when:
        EmployeeDTO result = employeeService.getEmployee(employeeId)

        then:
        result.id == employeeId
        result.name == JOHN_DOE
        result.aadhar == AADHAR
        result.age == 33
        result.dateOfBirth == localDate
        result.dept == IT_DEPARTMENT
        result.city == NEW_YORK
    }

    def "Should deleteEmployee when employee exists"() {
        given:
        Integer employeeId = 1
        Employee employee = new Employee()
        employeeRepository.findById(employeeId) >> Optional.of(employee)

        when:
        employeeService.deleteEmployee(employeeId)

        then:
        1 * employeeRepository.deleteById(employeeId)
    }

    def "Should throw ApplicationException when employee doesn't exists"() {
        given:
        Integer employeeId = 1
        employeeRepository.findById(employeeId) >> Optional.empty()

        when:
        employeeService.deleteEmployee(employeeId)

        then:
        thrown(ApplicationException)
    }

}
