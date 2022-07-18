package com.xu.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.dto.SetmealDto;
import com.xu.entity.Category;
import com.xu.entity.DishFlavor;
import com.xu.entity.Setmeal;
import com.xu.entity.SetmealDish;
import com.xu.mapper.SetmealDishMapper;
import com.xu.mapper.SetmealMapper;
import com.xu.service.ICategoryService;
import com.xu.service.ISetmealDishService;
import com.xu.service.ISetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements ISetmealDishService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private ICategoryService categoryService;

    @Override
    public boolean saveOne(SetmealDto setmealDto) {
//        //添加套餐
//        boolean save = this.save(setmealDto);
//
//        //添加套餐菜品 TODO
//        List<DishFlavor> flavors = dishDto.getFlavors();
//        Long id = dishDto.getId();
//        flavors = flavors.stream().map((item) -> {
//            item.setDishId(id);
//            return item;
//        }).collect(Collectors.toList());
//        boolean saveBatch = dishFlavorService.saveBatch(flavors);
//
//        return saveBatch && save;
        return true;
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
        BeanUtils.copyProperties(setmealPage,setmealDtoPage);
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
    public boolean updateOne(Setmeal setmeal) {
        return 0 < setmealMapper.updateById(setmeal);
    }

    @Override
    public boolean removeOne(Long id) {
        return false;
    }
}
