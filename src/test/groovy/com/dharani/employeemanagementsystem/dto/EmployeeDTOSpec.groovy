package com.dharani.employeemanagementsystem.dto

import spock.lang.Shared
import spock.lang.Specification
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.Errors
import org.springframework.validation.FieldError
import org.springframework.validation.Validator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean

import java.time.LocalDate
import java.time.format.DateTimeFormatter;

class EmployeeDTOSpec extends Specification {

    public static final String IT_DEPARTMENT = "IT"
    public static final String NEW_YORK = "New York"
    public static final int EMPLOYEE_ONE = 1
    public static final String VALID_AADHAR = "7282 1234 5678"
    public static final String VALID_NAME = "John Doe"

    def dateString = "21/07/2000"
    def formatter = DateTimeFormatter.ofPattern('dd/MM/yyyy')
    def localDate = LocalDate.parse(dateString, formatter)

    @Shared
    Validator validator

    def setupSpec() {
        validator = new LocalValidatorFactoryBean()
        validator.afterPropertiesSet()
    }

    def "Should not give errors when valid EmployeeDTO"() {
        given:
        EmployeeDTO employeeDTO = new EmployeeDTO(
                id: EMPLOYEE_ONE,
                name: VALID_NAME,
                aadhar: VALID_AADHAR,
                dateOfBirth: localDate,
                dept: IT_DEPARTMENT,
                city: NEW_YORK
        )

        when:
        Errors errors = validateEmployeeDTO(employeeDTO)

        then:
        !errors.hasErrors()
    }

    def "Should give name error when EmployeeDTO with invalid name"() {
        given:
        EmployeeDTO employeeDTO = new EmployeeDTO(
                id: EMPLOYEE_ONE,
                name: "John123 Doe",
                aadhar: VALID_AADHAR,
                dateOfBirth: localDate,
                dept: IT_DEPARTMENT,
                city: NEW_YORK
        )

        when:
        Errors errors = validateEmployeeDTO(employeeDTO)

        then:
        errors.hasErrors()
        FieldError nameError = errors.getFieldError("name")
        nameError != null
        nameError.defaultMessage == "Name should be alphabets only"
    }

    def "Should give aadhar error when EmployeeDTO with invalid aadhar"() {
        given:
        EmployeeDTO employeeDTO = new EmployeeDTO(
                id: EMPLOYEE_ONE,
                name: VALID_NAME,
                aadhar: "123456789",
                dateOfBirth: localDate,
                dept: IT_DEPARTMENT,
                city: NEW_YORK
        )

        when:
        Errors errors = validateEmployeeDTO(employeeDTO)

        then:
        errors.hasErrors()
        FieldError aadharError = errors.getFieldError("aadhar")
        aadharError != null
        aadharError.defaultMessage == "Invalid Aadhaar number"
    }

    def "Should give dateOfBirth error when EmployeeDTO with future date of birth"() {
        given:
        def dateOfBirth = LocalDate.now().plusDays(1)

        EmployeeDTO employeeDTO = new EmployeeDTO(
                id: EMPLOYEE_ONE,
                name: VALID_NAME,
                aadhar: VALID_AADHAR,
                dateOfBirth:dateOfBirth ,
                dept: IT_DEPARTMENT,
                city: NEW_YORK
        )

        when:
        Errors errors = validateEmployeeDTO(employeeDTO)

        then:
        errors.hasErrors()
        FieldError dateOfBirthError = errors.getFieldError("dateOfBirth")
        dateOfBirthError != null
        dateOfBirthError.defaultMessage == "Date of Birth should not be of future"
    }

    private Errors validateEmployeeDTO(EmployeeDTO employeeDTO) {
        Errors errors = new BeanPropertyBindingResult(employeeDTO, "employeeDTO")
        validator.validate(employeeDTO, errors)
        errors
    }
}

