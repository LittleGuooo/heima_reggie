package com.xu.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.entity.Category;
import com.xu.entity.Dish;
import com.xu.entity.Setmeal;
import com.xu.entity.User;
import com.xu.exception.BusinessException;
import com.xu.mapper.CategoryMapper;
import com.xu.mapper.UserMapper;
import com.xu.service.ICategoryService;
import com.xu.service.IDishService;
import com.xu.service.ISetmealService;
import com.xu.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.jws.soap.SOAPBinding;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private IDishService dishService;
    @Autowired
    private ISetmealService setmealService;
    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String phone) {
        //判断是否为老客户
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.like(User::getPhone,phone);
        User user = this.getOne(userLambdaQueryWrapper);
        if (user != null){
            return user;
        }

        //创建新用户，并设置信息
        User user1 = new User();
        user1.setStatus(1);
        user1.setPhone(phone);

        //插入新User
        this.save(user1);

        return user1;
    }

    @Override
    public boolean saveOne(Category category) {
        int insert = categoryMapper.insert(category);
        return insert > 0;
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
    public List<Category> getListByTypeId(int typeId) {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(Category::getType, typeId);

        return categoryMapper.selectList(categoryLambdaQueryWrapper);
    }
}
