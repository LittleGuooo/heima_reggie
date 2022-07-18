package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.common.Result;
import com.xu.entity.Employee;
import lombok.extern.slf4j.Slf4j;

public interface IEmployeeService extends IService<Employee> {
    /**
     * 验证员工登录
     * @param employee
     * @return
     */
    Result<Employee> verifyOne(Employee employee);

    /**
     * 添加一个新员工数据
     * @param employee
     * @return
     */
    boolean saveOne(Employee employee);

    /**
     * 返回一页员工数据
     * @param page
     * @param pageSize
     * @return
     */
    Page<Employee> getPage(int page, int pageSize, String name);

    /**
     * 修改一个员工数据
     * @param employee
     * @return
     */
    boolean updateOne(Employee employee);

}
