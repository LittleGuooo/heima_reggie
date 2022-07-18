package com.xu.service;

import com.xu.common.CommonTest;
import com.xu.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest
class IEmployeeServiceTest {
    @Autowired
    private IEmployeeService iEmployeeService;

    @Test
    void verifyOne() {

    }

    @Test
    void saveOne() {
        List<String> randomEmployee = CommonTest.TestRandomEmployee();
        Employee employee = new Employee();
        employee.setName(randomEmployee.get(0));
        employee.setUsername(randomEmployee.get(1));
        employee.setPhone(randomEmployee.get(2));
        employee.setIdNumber(randomEmployee.get(3));
        employee.setSex(randomEmployee.get(4));
        employee.setPassword(randomEmployee.get(5));

        iEmployeeService.save(employee);
    }

    @Test
    void getPage() {
    }

    @Test
    void updateOne() {
        Employee employee = new Employee();
        employee.setId(1545492246544052226L);
        employee.setPhone("13908445763");

        boolean b = iEmployeeService.updateOne(employee);
        System.out.println(b);
    }
}