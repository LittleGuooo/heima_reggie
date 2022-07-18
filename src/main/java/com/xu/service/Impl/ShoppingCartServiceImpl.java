package com.xu.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.common.BaseContext;
import com.xu.entity.Category;
import com.xu.entity.Dish;
import com.xu.entity.Setmeal;
import com.xu.entity.ShoppingCart;
import com.xu.exception.BusinessException;
import com.xu.mapper.CategoryMapper;
import com.xu.mapper.ShoppingCartMapper;
import com.xu.service.ICategoryService;
import com.xu.service.IDishService;
import com.xu.service.ISetmealService;
import com.xu.service.IShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private IDishService dishService;
    @Autowired
    private ISetmealService setmealService;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Override
    public boolean saveOne(ShoppingCart shoppingCart) {
        //查询是否已存在该菜品或套餐的购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        Long dishId = shoppingCart.getDishId();
        shoppingCartLambdaQueryWrapper.eq(dishId != null, ShoppingCart::getDishId,dishId);
        Long setmealId = shoppingCart.getSetmealId();
        shoppingCartLambdaQueryWrapper.eq(setmealId != null, ShoppingCart::getSetmealId,setmealId);
        ShoppingCart shoppingCart1 = this.getOne(shoppingCartLambdaQueryWrapper);

        //如果已存在，则将number加一
        if (shoppingCart1 != null){
            Integer number = shoppingCart1.getNumber();
            shoppingCart1.setNumber(number + 1);
            return this.updateById(shoppingCart1);
        }

        //如果不存在，则存入新数据
        return this.save(shoppingCart);
    }

    @Override
    public Boolean sub(ShoppingCart shoppingCart) {
        //查询当前用户购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        Long dishId = shoppingCart.getDishId();
        shoppingCartLambdaQueryWrapper.eq(dishId != null,ShoppingCart::getDishId,dishId);
        Long setmealId = shoppingCart.getSetmealId();
        shoppingCartLambdaQueryWrapper.eq(setmealId != null, ShoppingCart::getSetmealId,setmealId);
        ShoppingCart shoppingCart1 = this.getOne(shoppingCartLambdaQueryWrapper);

        //判断number是否大于一，如果大于则number减一，如果等于一则删除数据
        if (shoppingCart1.getNumber() > 1){
            Integer number = shoppingCart1.getNumber();
            shoppingCart1.setNumber(number - 1);
            return this.updateById(shoppingCart1);
        }

        return this.remove(shoppingCartLambdaQueryWrapper);
    }

    @Override
    public Boolean clean() {
        //获得用户id
        Long userId = BaseContext.getValue();

        //删除该用户所有购物车信息
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);

        return this.remove(shoppingCartLambdaQueryWrapper);
    }

    @Override
    public Page<Category> getPage(int page, int pageSize, String name) {
        //1.创建分页构造器
        Page<Category> categoryPage = new Page<>(page, pageSize);

        //2.创建条件包装器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //3.添加查询条件
        lambdaQueryWrapper.like(!(StringUtils.isEmpty(name)), Category::getName, name);

        //4.添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getCreateTime);

        //5.查询数据
        categoryMapper.selectPage(categoryPage, lambdaQueryWrapper);

        return categoryPage;
    }

    @Override
    public boolean updateOne(Category category) {
        return 0 < categoryMapper.updateById(category);
    }

    @Override
    public boolean removeOne(Long id) {
        //删除前判断是否关联菜品
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(Dish::getCategoryId, id);
        long exists = dishService.count(lambdaQueryWrapper);
        if (exists > 0) {
            //存在关联抛出业务异常
            throw new BusinessException("当前分类与菜品存在关联！", 0);
        }

        //删除前判断是否关联套餐
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper1 = new LambdaQueryWrapper();
        lambdaQueryWrapper1.eq(Setmeal::getCategoryId, id);
        long exists1 = setmealService.count(lambdaQueryWrapper1);
        if (exists1 > 0) {
            //存在关联抛出业务异常
            throw new BusinessException("当前分类与套餐存在关联！", 0);
        }

        //根据id删除分类数据
        return 0 < categoryMapper.deleteById(id);
    }

    @Override
    public List<Category> getListByTypeId(Integer typeId) {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(typeId != null, Category::getType, typeId);

        return categoryMapper.selectList(categoryLambdaQueryWrapper);
    }

    @Override
    public List<ShoppingCart> listByUserId(Long userId) {
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        return this.list(shoppingCartLambdaQueryWrapper);
    }
}
