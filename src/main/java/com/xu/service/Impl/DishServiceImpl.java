package com.xu.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.corba.se.impl.ior.JIDLObjectKeyTemplate;
import com.xu.dto.DishDto;
import com.xu.entity.*;
import com.xu.exception.BusinessException;
import com.xu.mapper.DishMapper;
import com.xu.service.ICategoryService;
import com.xu.service.IDishFlavorService;
import com.xu.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements IDishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IDishFlavorService dishFlavorService;

    @Override
    @Transactional
    public boolean saveOne(DishDto dishDto) {
        //添加菜品
        int insert1 = dishMapper.insert(dishDto);

        //添加菜品的风味
        List<DishFlavor> flavors = dishDto.getFlavors();
        Long id = dishDto.getId();
        flavors = flavors.stream().map((item) -> {
            item.setDishId(id);
            return item;
        }).collect(Collectors.toList());
        boolean saveBatch = dishFlavorService.saveBatch(flavors);

        return saveBatch && insert1 > 0;
    }

    @Override
    public Page<DishDto> getPage(int page, int pageSize, String name) {
        /**
         * 为了能显示categoryName，要对查询返回page结果进行改造。
         */

        //1.创建分页构造器
        Page<Dish> dishPage = new Page<>(page, pageSize);

        //创建DishDto返回结果
        Page<DishDto> dishDtoPage = new Page<>();

        //2.创建条件包装器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //3.添加查询条件
        lambdaQueryWrapper.like(!(StringUtils.isEmpty(name)), Dish::getName, name);

        //4.添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        //5.查询数据
        dishMapper.selectPage(dishPage, lambdaQueryWrapper);

        //拷贝除了records外的所有属性
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        //对返回的records进行改造
        List<Dish> records = dishPage.getRecords();
        List<DishDto> newRecords = records.stream().map((item) -> {
            DishDto dishDto = new DishDto();

            //利用categoryId查询出对应的categoryName，并赋值给DishDto对象
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //拷贝Dish对象的值到DishDto对象
            BeanUtils.copyProperties(item, dishDto);

            return dishDto;
        }).collect(Collectors.toList());

        //将改造好的records赋给dishDtoPage
        dishDtoPage.setRecords(newRecords);

        return dishDtoPage;
    }

    @Override
    @Transactional
    public boolean updateOne(DishDto dishDto) {
        //修改菜品信息
        boolean b = this.updateById(dishDto);

        //修改风味信息：
        //先删除原有
        Long id = dishDto.getId();
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        boolean remove = dishFlavorService.remove(dishFlavorLambdaQueryWrapper);
        //再重新添加
        List<DishFlavor> flavors = dishDto.getFlavors();
        List<DishFlavor> newFlavors = flavors.stream().map(flavor -> {
            //因为逻辑删除将产生id冲突，所以清除id
            flavor.setId(null);

            flavor.setDishId(id);
            return flavor;
        }).collect(Collectors.toList());
        boolean saveBatch = dishFlavorService.saveBatch(newFlavors);

        return remove && saveBatch && b;
    }

    @Override
    public DishDto getOne(Long id) {
        //查询Dish数据
        Dish dish = dishMapper.selectById(id);

        //查询对应Flavor数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
        lambdaQueryWrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavors = dishFlavorService.list(lambdaQueryWrapper);

        //组合成DishDto并返回
        DishDto dishDto = new DishDto();
        if (dish != null) {
            BeanUtils.copyProperties(dish, dishDto);
        }
        dishDto.setFlavors(dishFlavors);

        return dishDto;
    }

    @Override
    public List<Dish> getListByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, categoryId);

        return this.list(dishLambdaQueryWrapper);
    }

    @Override
    public boolean status(Integer status, Long[] ids) {
        //数组转集合
        List<Long> longs = Arrays.asList(ids);

        //先查询
        List<Dish> dishs = this.listByIds(longs);

        //再修改
        dishs = dishs.stream().map(dish -> {
            dish.setStatus(status);
            return dish;
        }).collect(Collectors.toList());
        boolean updateBatchById = this.updateBatchById(dishs);

        return true;
    }

    @Override
    public boolean removeOne(Long[] ids) {
        //数组转换集合
        List<Long> longs = Arrays.asList(ids);

        //查询菜品状态，确定是否可以删除
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(Dish::getId, longs);
        dishLambdaQueryWrapper.eq(Dish::getStatus, 1);
        long count = this.count(dishLambdaQueryWrapper);
        if (count > 0) {
            //不能删除则抛出异常
            throw new BusinessException("正在启售状态，无法删除！", 0);
        }

        //删除菜品
        boolean removeById = this.removeBatchByIds(longs);

        //删除菜品关联的风味
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.in(DishFlavor::getDishId, longs);
        boolean remove = dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        return true;
    }

    @Override
    public List<DishDto> getList(Dish dish) {
        //根据categoryId查询菜品
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,dish.getCategoryId());
        dishLambdaQueryWrapper.eq(Dish::getStatus,1);
        List<Dish> dishList = list(dishLambdaQueryWrapper);

        //查询菜品风味信息，并合并成DishDto
        List<DishDto> dishDtoList = dishList.stream().map(dish1 -> {
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish1,dishDto);

            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dish1.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavorList);

            return dishDto;
        }).collect(Collectors.toList());

        return dishDtoList;
    }

}
