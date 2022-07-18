package com.xu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xu.entity.Dish;
import com.xu.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishFlavorMapper extends BaseMapper<DishFlavor> {
}
