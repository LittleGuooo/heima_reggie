package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.dto.OrdersDto;
import com.xu.entity.Category;
import com.xu.entity.Orders;

import java.util.List;

public interface IOrdersService extends IService<Orders> {
    /**
     * 添加一个新分类数据
     * @param category
     * @return
     */
    boolean saveOne(Category category);

    /**
     * 返回一页分类数据
     * @param page
     * @param pageSize
     * @return
     */
    Page<Category> getPage(int page, int pageSize, String name);

    /**
     * 修改一个分类数据
     * @param category
     * @return
     */
    boolean updateOne(Category category);

    /**
     * 删除一个分类
     * @param id
     * @return
     */
    boolean removeOne(Long id);

    List<Category> getListByTypeId(Integer typeId);

    Boolean submit(Orders orders);

    Page<OrdersDto> userPage(Integer page, Integer pageSize);
}
