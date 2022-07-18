package com.xu.mapper;

import com.xu.common.BaseContext;
import com.xu.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmployeeMapperTest {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Test
    public void Test1() {
        Employee employee = new Employee();
        employee.setId(1546786155106709506L);
        employee.setName("小白");
        BaseContext.setValue(1L);
        int i = employeeMapper.updateById(employee);
    }
}