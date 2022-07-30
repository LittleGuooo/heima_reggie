package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.Result;
import com.xu.entity.Category;
import com.xu.entity.Employee;
import com.xu.service.ICategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/category")
@Api(tags = "分类相关接口")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    /**
     * 新增
     * @param httpServletRequest
     * @param employee
     * @return
     */
    @PostMapping
    public Result<Category> save(HttpServletRequest httpServletRequest, @RequestBody Category employee) {
        categoryService.saveOne(employee);
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
        Page<Category> page1 = categoryService.getPage(page, pageSize, name);
        return Result.success(page1);
    }

    /**
     * 修改
     * @param httpServletRequest
     * @param category
     * @return
     */
    @PutMapping
    public Result<Category> update(HttpServletRequest httpServletRequest, @RequestBody Category category) {
        categoryService.updateOne(category);
        return Result.success(null);
    }

    /**
     * id删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<Category> delete(Long ids) {
        boolean b = categoryService.removeOne(ids);
        if (b) {
            return Result.success(null);
        }

        return Result.error("删除数据失败！");
    }


    /**
     * 列表查询
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type) {
        log.info(String.valueOf(type));
        List<Category> listByTypeId = categoryService.getListByTypeId(type);
        if (listByTypeId != null) {
            return Result.success(listByTypeId);
        }
        return Result.error("查询数据失败！");
    }
}
