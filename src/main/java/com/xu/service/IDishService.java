package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.dto.DishDto;
import com.xu.entity.Category;
import com.xu.entity.Dish;

import java.util.List;

public interface IDishService extends IService<Dish> {
    /**
     * 添加一个新菜品数据
     * @param dishDto
     * @return
     */
    boolean saveOne(DishDto dishDto);

    /**
     * 返回一页分类数据
     * @param page
     * @param pageSize
     * @return
     */
    Page<DishDto> getPage(int page, int pageSize, String name);

    /**
     * 修改一个分类数据
     * @param dishDto
     * @return
     */
    boolean updateOne(DishDto dishDto);

    /**
     * 删除一个分类
     * @param id
     * @return
     */
    DishDto getOne(Long id);

    List<Dish> getListByCategoryId(Long categoryId);

    boolean status(Integer status, Long[] ids);

    boolean removeOne(Long[] ids);

    List<DishDto> getList(Dish dish);
}
