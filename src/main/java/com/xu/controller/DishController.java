package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.Result;
import com.xu.dto.DishDto;
import com.xu.entity.Dish;
import com.xu.service.IDishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private IDishService dishService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public Result<Page<DishDto>> page(int page, int pageSize, String name) {
        Page<DishDto> page1 = dishService.getPage(page, pageSize, name);
        return Result.success(page1);
    }

    /**
     * 新增
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public Result<String> save(@RequestBody DishDto dishDto) {
        //添加菜品
        boolean save = dishService.saveOne(dishDto);
        if (!save) {
            Result.error("修改失败！");
        }

        //数据发生变化，清理缓存
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        Boolean delete = redisTemplate.delete(key);

        return Result.success("添加成功！");
    }

    /**
     * id查询
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<DishDto> get(@PathVariable Long id) {
        DishDto dishDto = dishService.getOne(id);
        return Result.success(dishDto);
    }

    /**
     * 修改
     *
     * @param dishDto
     * @return
     */
    @PutMapping
    public Result<String> update(@RequestBody DishDto dishDto) {
        boolean b = dishService.updateOne(dishDto);
        if (!b) {
            Result.error("修改失败！");
        }

        //数据发生变化，清理缓存
        String key = "dish_" + dishDto.getCategoryId() + "_" + dishDto.getStatus();
        Boolean delete = redisTemplate.delete(key);

        return Result.success("修改成功！");
    }

    /**
     * 列表查询
     *
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public Result<List<DishDto>> list(Dish dish) {
        //优化：先查看redis缓存
        String key = "dish_" + dish.getCategoryId() + "_" + dish.getStatus();
        List<DishDto> dishDtos = (List<DishDto>) redisTemplate.opsForValue().get(key);

        //如果存在则直接返回数据
        if (dishDtos != null) {
            log.info("redis中有缓存");
            return Result.success(dishDtos);
        }

        //如果不存在则查询数据并存入redis，再返回数据
        dishDtos = dishService.getList(dish);
        redisTemplate.opsForValue().set(key, dishDtos,60, TimeUnit.MINUTES);
        log.info("redis中无缓存");

        return Result.success(dishDtos);
    }

    /**
     * 状态修改
     *
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public Result<String> status(@PathVariable Integer status, Long[] ids) {
        boolean status1 = dishService.status(status, ids);
        return Result.success("成功！");
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @DeleteMapping
    public Result<String> delete(Long[] ids) {
        //判断ids是否为空
        if (ids.length == 0) {
            return Result.error("");
        }

        boolean b = dishService.removeOne(ids);
        if (b) {
            return Result.success(null);
        }

        return Result.error("删除数据失败！");
    }
}
