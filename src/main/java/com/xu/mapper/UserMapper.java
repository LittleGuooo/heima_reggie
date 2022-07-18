package com.xu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xu.entity.Category;
import com.xu.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
