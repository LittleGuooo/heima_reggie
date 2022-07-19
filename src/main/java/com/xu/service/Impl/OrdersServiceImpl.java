package com.xu.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xu.common.BaseContext;
import com.xu.dto.OrdersDto;
import com.xu.entity.*;
import com.xu.exception.BusinessException;
import com.xu.mapper.CategoryMapper;
import com.xu.mapper.OrdersMapper;
import com.xu.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements IOrdersService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private IDishService dishService;
    @Autowired
    private ISetmealService setmealService;
    @Autowired
    private IShoppingCartService shoppingCartService;
    @Autowired
    private IOrderDetailService orderDetailService;
    @Autowired
    private IAddressBookService addressBookService;

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
    public List<Category> getListByTypeId(Integer typeId) {
        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(typeId != null, Category::getType, typeId);

        return categoryMapper.selectList(categoryLambdaQueryWrapper);
    }

    @Override
    @Transactional
    public Boolean submit(Orders orders) {
        Long userId = BaseContext.getValue();
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);

        //首先判断购物车数据是否为空
        if (shoppingCartList.isEmpty()) {
            throw new BusinessException("购物车为空，不能下单！", 0);
        }

        //新增order数据
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(IdWorker.getId()));
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setOrderTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(0));
        boolean save = this.save(orders);

        //新增orderDetail数据
        List<OrderDetail> orderDetailList = shoppingCartList.stream().map(shoppingCart -> {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart, orderDetail);
            orderDetail.setOrderId(orders.getId());

            return orderDetail;
        }).collect(Collectors.toList());
        boolean saveBatch = orderDetailService.saveBatch(orderDetailList);

        //清除购物车数据
        boolean remove = shoppingCartService.remove(shoppingCartLambdaQueryWrapper);

        return true;
    }

    @Override
    public Page<OrdersDto> userPage(Integer page, Integer pageSize) {
        //1.创建分页构造器
        Page<Orders> ordersPage = new Page<>(page, pageSize);

        //2.创建条件包装器
        LambdaQueryWrapper<Orders> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        //3.添加查询条件
        lambdaQueryWrapper.like(Orders::getUserId, BaseContext.getValue());

        //4.添加排序条件
        lambdaQueryWrapper.orderByAsc(Orders::getOrderTime);

        //5.查询Orders数据
        this.page(ordersPage, lambdaQueryWrapper);

        //6.对每个Order数据查询orderDetail数据和AddressBook数据，并合并成OrderDto
        List<Orders> ordersList = ordersPage.getRecords();
        List<OrdersDto> ordersDtoList = ordersList.stream().map(orders -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(orders, ordersDto);

            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId, orders.getId());
            List<OrderDetail> orderDetailList = orderDetailService.list(orderDetailLambdaQueryWrapper);
            ordersDto.setOrderDetails(orderDetailList);

            AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
            BeanUtils.copyProperties(addressBook, ordersDto);

            return ordersDto;
        }).collect(Collectors.toList());

        Page<OrdersDto> ordersDtoPage = new Page<>();
        BeanUtils.copyProperties(ordersPage, ordersDtoPage);
        ordersDtoPage.setRecords(ordersDtoList);

        return ordersDtoPage;
    }
}
