package com.xu.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.entity.Dish;
import com.xu.entity.DishFlavor;
import com.xu.mapper.DishFlavorMapper;
import com.xu.mapper.DishMapper;
import com.xu.service.IDishFlavorService;
import com.xu.service.IDishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements IDishFlavorService {
    @Autowired
    private DishMapper dishMapper;

//    @Override
//    public boolean saveOne(Dish dish) {
//        int insert = dishMapper.insert(dish);
//        return insert > 0;
//    }
//
//        @Override
//    public Page<Dish> getPage(int page, int pageSize, String name) {
//        //1.创建分页构造器
//        Page<Dish> dishPage = new Page<>(page, pageSize);
//
//        //2.创建条件包装器
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//
//        //3.添加查询条件
//        lambdaQueryWrapper.like(!(StringUtils.isEmpty(name)), Dish::getName, name);
//
//        //4.添加排序条件
//        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);
//
//        //5.查询数据
//        dishMapper.selectPage(dishPage, lambdaQueryWrapper);
//
//        return dishPage;
//    }
//
//    @Override
//    public boolean updateOne(Dish dish) {
//        return 0 < dishMapper.updateById(dish);
//    }
//
//    @Override
//    public boolean removeOne(Long id) {
//        return false;
//    }
}
