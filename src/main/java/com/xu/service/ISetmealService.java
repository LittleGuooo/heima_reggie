package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.dto.DishDto;
import com.xu.dto.SetmealDto;
import com.xu.entity.Dish;
import com.xu.entity.Setmeal;

public interface ISetmealService extends IService<Setmeal> {
    /**
     * 添加一个新菜品数据
     * @param setmealDto
     * @return
     */
    boolean saveOne(SetmealDto setmealDto);

    /**
     * 返回一页分类数据
     * @param page
     * @param pageSize
     * @return
     */
    Page<SetmealDto> getPage(int page, int pageSize, String name);

    /**
     * 修改一个分类数据
     * @param setmealDto
     * @return
     */
    boolean updateOne(SetmealDto setmealDto);

    /**
     * 删除一个分类
     * @param ids
     * @return
     */
    boolean removeOne(Long[] ids);

    boolean status(Integer status, Long[] ids);

    SetmealDto getOne(Long id);
}
