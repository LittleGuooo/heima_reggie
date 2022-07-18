package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.dto.SetmealDto;
import com.xu.entity.Setmeal;
import com.xu.entity.SetmealDish;

public interface ISetmealDishService extends IService<SetmealDish> {
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
     * @param setmeal
     * @return
     */
    boolean updateOne(Setmeal setmeal);

    /**
     * 删除一个分类
     * @param id
     * @return
     */
    boolean removeOne(Long id);
}
