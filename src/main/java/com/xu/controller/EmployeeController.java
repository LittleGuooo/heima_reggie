package com.xu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.Result;
import com.xu.entity.Employee;
import com.xu.service.IEmployeeService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/employee")
@Api(tags = "员工相关接口")
public class EmployeeController {
    @Autowired
    private IEmployeeService employeeService;

    /**
     * 登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public Result<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //楠岃瘉鐧诲綍淇℃伅
        Result<Employee> employeeResult = employeeService.verifyOne(employee);

        //鐧诲綍鎴愬姛锛屽皢鍛樺伐id瀛樺叆Session
        if (employeeResult.getCode() == 1) {
            request.getSession().setAttribute("employee", employeeResult.getData().getId());
        }

        //杩斿洖鐧诲綍缁撴灉
        return employeeResult;
    }

    /**
     * 注销
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public Result<Employee> logout(HttpServletRequest request) {
        //娉ㄩ攢鎴愬姛锛屾竻鐞哠ession涓憳宸�
        request.getSession().removeAttribute("employee");

        //杩斿洖娉ㄩ攢缁撴灉
        return Result.success(null);
    }

    /**
     * 新增
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PostMapping
    public Result<Employee> save(HttpServletRequest httpServletRequest, @RequestBody Employee employee) {
        employeeService.saveOne(employee);
        return Result.success(null);
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> get(int page, int pageSize, String name) {
        Page<Employee> page1 = employeeService.getPage(page, pageSize, name);
        return Result.success(page1);
    }

    /**
     * id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<Employee> get(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return Result.success(employee);
        }
        return Result.error("鏈煡鎵惧埌璇ュ憳宸ユ暟鎹�");
    }

    /**
     * 修改
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PutMapping
    public Result<Employee> update(HttpServletRequest httpServletRequest, @RequestBody Employee employee){
        employeeService.updateOne(employee);
        return Result.success(null);
    }
}
