package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.BaseContext;
import com.xu.common.Result;
import com.xu.dto.OrdersDto;
import com.xu.entity.AddressBook;
import com.xu.entity.Category;
import com.xu.entity.Orders;
import com.xu.service.IAddressBookService;
import com.xu.service.IOrdersService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/order")
@Api(tags = "订单相关接口")
public class OrdersController {
    @Autowired
    private IOrdersService ordersService;


    /**
     * 新增
     * 提交新订单
     *
     * @param ordersDto
     * @return
     */
    @PostMapping("/submit")
    public Result<String> submit(@RequestBody OrdersDto ordersDto) {
        ordersService.submit(ordersDto);

        return Result.success("添加成功！");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public Result<Page<OrdersDto>> userPage(Integer page, Integer pageSize) {
        Page<OrdersDto> ordersDtoPage = ordersService.userPage(page, pageSize);

        return Result.success(ordersDtoPage);
    }

    /**
     * 列表查询
     * 返回当前用户的地址列表
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<AddressBook>> list() {
        //获取id
        Long userId = BaseContext.getValue();

        //根据id查询地址列表
//        List<AddressBook> addressBookList = ordersService.getListByUserId(userId);
//
//        return Result.success(addressBookList);
        return null;
    }

    /**
     * 修改
     * 设置默认地址
     *
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public Result<String> setDefault(@RequestBody AddressBook addressBook) {
        //设置userId
        addressBook.setUserId(BaseContext.getValue());

        //设置默认地址
//        boolean b = ordersService.setDefault(addressBook);

        return Result.success(null);
    }

    /**
     * 查询
     * 查询默认地址
     *
     * @return
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        //查询默认地址
//        AddressBook addressBook = ordersService.getDefault();
//
//        return Result.success(addressBook);
        return null;
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page> get(int page, int pageSize, String name) {
        Page<Category> page1 = ordersService.getPage(page, pageSize, name);
        return Result.success(page1);
    }

    /**
     * id删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<Category> delete(Long ids) {
        boolean b = ordersService.removeOne(ids);
        if (b) {
            return Result.success(null);
        }

        return Result.error("删除数据失败！");
    }
}
