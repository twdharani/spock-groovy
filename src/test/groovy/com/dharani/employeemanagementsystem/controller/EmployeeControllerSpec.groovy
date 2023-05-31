package com.dharani.employeemanagementsystem.controller

import com.dharani.employeemanagementsystem.dto.EmployeeDTO
import com.dharani.employeemanagementsystem.service.EmployeeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean

import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import spock.lang.Specification
import org.springframework.http.HttpStatus
import java.time.LocalDate

import org.springframework.http.MediaType
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.mockito.Mockito.*


@WebMvcTest(EmployeeController)
@AutoConfigureMockMvc
@ContextConfiguration
class EmployeeControllerSpec extends Specification {

    @Autowired
    MockMvc mockMvc

    @MockBean
    EmployeeService employeeService

    def "addEmployee should return response with status 201"() {
        given:
        def employeeDTO = """{
        "id": 1,
        "name": "john",
        "aadhar": "7282 1234 5678",
        "dateOfBirth": "2000-07-21",
        "dept": "IT",
        "city": "cbe"
        } """

        when:
        when(employeeService.addEmployee(any(EmployeeDTO))).thenReturn(1)

        def result = mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeDTO)
        ).andReturn()

        then:
        def expectedResponse = '{"Status":"Success","Message":"Inserted successfully with employeeId  1"}'
        def actualResponse = result.getResponse().getContentAsString()

        result.response.status == HttpStatus.CREATED.value()
        actualResponse.trim() == expectedResponse.trim()
    }

    def "addEmployee should return response with status 500 when invalid name"() {
        given:
        def employeeDTO = """{
        "id": 1,
        "name": "john12",
        "aadhar": "7282 1234 5678",
        "dateOfBirth": "2000-07-21",
        "dept": "IT",
        "city": "cbe"
        } """

        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(employeeDTO)
        ).andReturn()

        then:
        result.response.status == HttpStatus.INTERNAL_SERVER_ERROR.value()
    }

    def "getEmployee should return response with status 200"() {
        given:
        EmployeeDTO employeeDTO = new EmployeeDTO(
                id: 1,
                name: "JOHN DOE",
                aadhar: "7282 1234 5678",
                dateOfBirth: LocalDate.of(2000, 7,21),
                dept: "IT",
                city: "NEW YORK"
        )

        when:
        when(employeeService.getEmployee(1)).thenReturn(employeeDTO)
        def result = mockMvc.perform(get("/employees/1"))
                .andReturn()

        then:
        result.response.status == HttpStatus.OK.value()
    }

    def "deleteEmployee should return response with status 200"() {
        when:
        def result = mockMvc.perform(MockMvcRequestBuilders.delete("/employees/1")).andReturn()

        then:
        def expectedResponse = '{"Status":"Success","Message":"Deleted successfully"}'
        def actualResponse = result.getResponse().getContentAsString()

        result.response.status == HttpStatus.OK.value()
        actualResponse.trim() == expectedResponse.trim()
    }

}
