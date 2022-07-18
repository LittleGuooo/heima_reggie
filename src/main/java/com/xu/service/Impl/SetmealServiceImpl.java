package com.xu.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.dto.DishDto;
import com.xu.dto.SetmealDto;
import com.xu.entity.*;
import com.xu.exception.BusinessException;
import com.xu.mapper.DishMapper;
import com.xu.mapper.SetmealMapper;
import com.xu.service.ICategoryService;
import com.xu.service.IDishService;
import com.xu.service.ISetmealDishService;
import com.xu.service.ISetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements ISetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ISetmealDishService setmealDishService;

    @Override
    public boolean saveOne(SetmealDto setmealDto) {
        //添加套餐
        boolean save = this.save(setmealDto);

        //添加套餐菜品
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //设置SetmealId
        Long setmealDtoId = setmealDto.getId();
        setmealDishes = setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDtoId);
            return item;
        }).collect(Collectors.toList());
        boolean saveBatch = setmealDishService.saveBatch(setmealDishes);

        return saveBatch && save;
    }

    @Override
    public Page<SetmealDto> getPage(int page, int pageSize, String name) {
        //1.创建分页构造器
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);

        //2.创建条件包装器
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //3.添加查询条件
        lambdaQueryWrapper.like(!(StringUtils.isEmpty(name)), Setmeal::getName, name);

        //4.添加排序条件
        lambdaQueryWrapper.orderByAsc(Setmeal::getCreateTime);

        //5.查询数据
        setmealMapper.selectPage(setmealPage, lambdaQueryWrapper);

        //6.改造返回结果
        Page<SetmealDto> setmealDtoPage = new Page<>();
        //复制字段值，除records之外
        BeanUtils.copyProperties(setmealPage, setmealDtoPage);
        //改造records
        List<Setmeal> records = setmealPage.getRecords();
        List<SetmealDto> newRecords = records.stream().map((record) -> {
            SetmealDto setmealDto = new SetmealDto();
            //复制字段值
            BeanUtils.copyProperties(record, setmealDto);
            //设置categoryName字段
            Long categoryId = record.getCategoryId();
            Category category = categoryService.getById(categoryId);
            setmealDto.setCategoryName(category.getName());
            //返回新record
            return setmealDto;
        }).collect(Collectors.toList());
        //设置新records
        setmealDtoPage.setRecords(newRecords);

        return setmealDtoPage;
    }

    @Override
    public boolean updateOne(SetmealDto setmealDto) {
        //修改套餐信息
        boolean b = this.updateById(setmealDto);

        //修改菜品信息：
        //先删除原有
        Long id = setmealDto.getId();
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        boolean remove = setmealDishService.remove(setmealDishLambdaQueryWrapper);
        //再重新添加
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        List<SetmealDish> newFlavors = setmealDishes.stream().map(setmealDishe -> {
            setmealDishe.setSetmealId(id);
            return setmealDishe;
        }).collect(Collectors.toList());
        boolean saveBatch = setmealDishService.saveBatch(newFlavors);

        return remove && saveBatch && b;
    }

    @Override
    public boolean removeOne(Long[] ids) {
        //数组转换成集合
        List<Long> longs = Arrays.asList(ids);

        //查询套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.in(Setmeal::getId, longs);
        setmealLambdaQueryWrapper.eq(Setmeal::getStatus, 1);
        long count = this.count(setmealLambdaQueryWrapper);
        if (count > 0) {
            //不能删除则抛出异常
            throw new BusinessException("套餐正在启售状态，无法删除！", 0);
        }

        //删除套餐
        int deleteBatchIds = setmealMapper.deleteBatchIds(longs);

        //删除套餐关联的菜品
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealDishLambdaQueryWrapper.in(SetmealDish::getSetmealId, longs);
        boolean remove = setmealDishService.remove(setmealDishLambdaQueryWrapper);

        return true;
    }

    @Override
    public boolean status(Integer status, Long[] ids) {
        //先查询
        List<Long> longs = Arrays.asList(ids);
        List<Setmeal> setmeals = setmealMapper.selectBatchIds(longs);

        //再修改
        List<Setmeal> setmeals1 = setmeals.stream().map(setmeal -> {
            setmeal.setStatus(status);
            return setmeal;
        }).collect(Collectors.toList());
        boolean updateById = this.updateBatchById(setmeals1);

        return true;
    }

    @Override
    public SetmealDto getOne(Long id) {
        //查询套餐数据
        Setmeal setmeal = setmealMapper.selectById(id);

        //查询对应菜品数据
        LambdaQueryWrapper<SetmealDish> setmealDishLambdaQueryWrapper = new LambdaQueryWrapper();
        setmealDishLambdaQueryWrapper.eq(SetmealDish::getSetmealId, id);
        List<SetmealDish> setmealDishes = setmealDishService.list(setmealDishLambdaQueryWrapper);

        //组合成DishDto并返回
        SetmealDto setmealDto = new SetmealDto();
        if (setmeal != null) {
            BeanUtils.copyProperties(setmeal, setmealDto);
        }
        setmealDto.setSetmealDishes(setmealDishes);

        return setmealDto;
    }
}
