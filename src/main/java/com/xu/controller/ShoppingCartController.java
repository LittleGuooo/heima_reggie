package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.BaseContext;
import com.xu.common.Result;
import com.xu.entity.Category;
import com.xu.entity.ShoppingCart;
import com.xu.mapper.ShoppingCartMapper;
import com.xu.service.ICategoryService;
import com.xu.service.IShoppingCartService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/shoppingCart")
@Api(tags = "购物车相关接口")
public class ShoppingCartController {
    @Autowired
    private IShoppingCartService shoppingCartService;

    /**
     * 列表查询
     * 查询对应用户的订单
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.listByUserId(BaseContext.getValue());

        return Result.success(list);
    }

    /**
     * 新增
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart) {
        //设置userId
        shoppingCart.setUserId(BaseContext.getValue());

        //新增购物车数据
        shoppingCartService.saveOne(shoppingCart);

        return Result.success(shoppingCart);
    }

    /**
     * 新增
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public Result<String> sub(@RequestBody ShoppingCart shoppingCart) {
        //设置userId
        shoppingCart.setUserId(BaseContext.getValue());

        //删除购物车数据
        Boolean sub = shoppingCartService.sub(shoppingCart);

        return Result.success("删除成功！");
    }

    /**
     * 清空购物车
     *
     * @return
     */
    @DeleteMapping("/clean")
    public Result<String> clean() {
        Boolean clean = shoppingCartService.clean();
        return Result.success("清除成功！");
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
        Page<Category> page1 = shoppingCartService.getPage(page, pageSize, name);
        return Result.success(page1);
    }

    /**
     * 修改
     *
     * @param httpServletRequest
     * @param category
     * @return
     */
    @PutMapping
    public Result<Category> update(HttpServletRequest httpServletRequest, @RequestBody Category category) {
        shoppingCartService.updateOne(category);
        return Result.success(null);
    }

    /**
     * id删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<Category> delete(Long ids) {
        boolean b = shoppingCartService.removeOne(ids);
        if (b) {
            return Result.success(null);
        }

        return Result.error("删除数据失败！");
    }
}
