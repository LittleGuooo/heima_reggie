package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.BaseContext;
import com.xu.common.Result;
import com.xu.entity.AddressBook;
import com.xu.entity.Category;
import com.xu.mapper.AddressBookMapper;
import com.xu.service.IAddressBookService;
import com.xu.service.ICategoryService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.beans.ParameterDescriptor;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/addressBook")
@Api(tags = "地址簿相关接口")
public class AddressBookController {
    @Autowired
    private IAddressBookService addressBookService;

    /**
     * 新增
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody AddressBook addressBook) {
        //获取UserId
        addressBook.setUserId(BaseContext.getValue());

        //新增地址
        addressBookService.saveOne(addressBook);

        return Result.success("添加成功！");
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
        List<AddressBook> addressBookList = addressBookService.getListByUserId(userId);

        return Result.success(addressBookList);
    }

    /**
     * 修改
     * 设置默认地址
     * @param addressBook
     * @return
     */
    @PutMapping("/default")
    public Result<String> setDefault(@RequestBody AddressBook addressBook) {
        //设置userId
        addressBook.setUserId(BaseContext.getValue());

        //设置默认地址
        boolean b = addressBookService.setDefault(addressBook);

        return Result.success(null);
    }

    /**
     * 查询
     * 查询默认地址
     * @return
     */
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        //查询默认地址
        AddressBook addressBook = addressBookService.getDefault();

        return Result.success(addressBook);
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
        Page<Category> page1 = addressBookService.getPage(page, pageSize, name);
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
        boolean b = addressBookService.removeOne(ids);
        if (b) {
            return Result.success(null);
        }

        return Result.error("删除数据失败！");
    }
}
