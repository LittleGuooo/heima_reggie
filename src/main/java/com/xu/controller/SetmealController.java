package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.Result;
import com.xu.dto.DishDto;
import com.xu.dto.SetmealDto;
import com.xu.entity.Category;
import com.xu.service.ISetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private ISetmealService setmealService;

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<SetmealDto>> page(int page, int pageSize, String name){
        Page<SetmealDto> page1 = setmealService.getPage(page, pageSize, name);
        return Result.success(page1);
    }

    /**
     * 新增
     * @param setmealDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody SetmealDto setmealDto) {
        //添加菜品
        boolean save = setmealService.saveOne(setmealDto);
        if (!save) {
            Result.error("修改失败！");
        }
        return Result.success("添加成功！");
    }

    /**
     * 删除
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long[] ids) {
        //判断ids是否为空
        if (ids.length == 0){
            return Result.error("");
        }

        boolean b = setmealService.removeOne(ids);
        if (b) {
            return Result.success(null);
        }

        return Result.error("删除数据失败！");
    }

    /**
     * 状态修改
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, Long[] ids) {
        boolean status1 = setmealService.status(status, ids);
        return Result.success("成功！");
    }

    /**
     * 修改
     * @param setmealDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody SetmealDto setmealDto) {
        boolean b = setmealService.updateOne(setmealDto);
        if (!b) {
            Result.error("修改失败！");
        }

        return Result.success("修改成功！");
    }

    /**
     * id查询
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getOne(id);
        return Result.success(setmealDto);
    }
}
