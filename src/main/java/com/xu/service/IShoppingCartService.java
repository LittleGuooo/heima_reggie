package com.xu.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xu.entity.Category;
import com.xu.entity.ShoppingCart;

import java.util.List;

public interface IShoppingCartService extends IService<ShoppingCart> {
    /**
     * 添加一个新分类数据
     * @param shoppingCart
     * @return
     */
    boolean saveOne(ShoppingCart shoppingCart);

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

    List<ShoppingCart> listByUserId(Long userId);

    Boolean sub(ShoppingCart shoppingCart);

    Boolean clean();
}
