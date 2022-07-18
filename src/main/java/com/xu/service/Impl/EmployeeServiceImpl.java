package com.xu.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.common.Result;
import com.xu.entity.Employee;
import com.xu.mapper.EmployeeMapper;
import com.xu.service.IEmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements IEmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Result<Employee> verifyOne(Employee employee) {
        //1.检索员工是否存在
        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername, username);
        Employee employee1 = employeeMapper.selectOne(lambdaQueryWrapper);
        if (employee1 == null) {
            return Result.error("员工不存在！");
        }

        //2.对比密码
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!(employee1.getPassword().equals(password))) {
            return Result.error("密码不匹配！");
        }

        //3.检查员工状态
        if (employee1.getStatus() == 0) {
            return Result.error("员工处于禁止状态！");
        }

        return Result.success(employee1);
    }

    @Override
    public boolean saveOne(Employee employee) {
        //1.设置默认密码为123456
        String defaultPassword = "123456";
        String md5Password = DigestUtils.md5DigestAsHex(defaultPassword.getBytes());
        employee.setPassword(md5Password);

        //2.添加到数据库
        int insert = employeeMapper.insert(employee);

        return insert > 0;
    }

    @Override
    public Page<Employee> getPage(int page, int pageSize, String name) {
        //1.创建分页构造器
        Page<Employee> employeePage = new Page<>(page, pageSize);

        //2.创建条件包装器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //3.添加查询条件
        lambdaQueryWrapper.like(!(StringUtils.isEmpty(name)), Employee::getName, name);

        //4.添加排序条件
        lambdaQueryWrapper.orderByAsc(Employee::getCreateTime);

        //5.查询数据
        employeeMapper.selectPage(employeePage, lambdaQueryWrapper);

        return employeePage;
    }

    @Override
    public boolean updateOne(Employee employee) {
        return 0 < employeeMapper.updateById(employee);
    }
}
