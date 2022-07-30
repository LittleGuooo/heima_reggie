package com.xu.controller;

import com.xu.common.Result;
import com.xu.common.ValidateCodeUtils;
import com.xu.entity.User;
import com.xu.service.IUserService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/user")
@Api(tags = "用户相关接口")
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送验证码
     *
     * @param httpServletRequest
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public Result<String> sendMsg(HttpServletRequest httpServletRequest, @RequestBody User user) {
        String phone = user.getPhone();

        if (phone != null) {
            //假设调用短信Api发送验证成功
            Integer code = ValidateCodeUtils.generateValidateCode(6);
            log.info("code=" + code);

            //存入验证与手机信息
            //原先：验证码存入session，有效期30分钟。
//            httpServletRequest.getSession().setAttribute(phone, code);
            //优化：验证码存入redis缓存。
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return Result.success("发送验证码成功！");
        }

        return Result.error("发送验证码失败！");
    }


    /**
     * 登录
     *
     * @param httpServletRequest
     * @param map
     * @return
     */
    @PostMapping("/login")
    public Result<User> login(HttpServletRequest httpServletRequest, @RequestBody Map map) {
        //比对验证码
        String phone = (String) map.get("phone");
        Integer code = Integer.parseInt((String) map.get("code"));
        //原先
//        Integer realCode = (Integer) httpServletRequest.getSession().getAttribute(phone);
        //优化：从redis缓存中取出code
        Integer realCode = (Integer) redisTemplate.opsForValue().get(phone);
        log.info(realCode.toString());

        if (realCode.equals(code)) {
            //验证码验证通过，删除验证码
            Boolean delete = redisTemplate.delete(phone);

            //调用service登录
            User user = userService.login(phone);

            //存入用户id
            httpServletRequest.getSession().setAttribute("user", user.getId());

            return Result.success(user);
        }

        return Result.error("登录失败！");
    }
}
