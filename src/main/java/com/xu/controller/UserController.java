package com.xu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xu.common.Result;
import com.xu.common.ValidateCodeUtils;
import com.xu.entity.Category;
import com.xu.entity.User;
import com.xu.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.AnnotationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService userService;

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
            //假设调用Api发送验证成功
            Integer code = ValidateCodeUtils.generateValidateCode(6);
            log.info("code=" + code);

            //存入验证与手机信息
            httpServletRequest.getSession().setAttribute(phone, code);

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
        Integer realCode = (Integer) httpServletRequest.getSession().getAttribute(phone);
        // TODO 登录功能测试用后门，待删除
//        if (realCode != null && Objects.equals(realCode, code)) {
        if (true) {
            //调用service登录
            User user = userService.login(phone);

            //存入用户id
            httpServletRequest.getSession().setAttribute("user", user.getId());

            return Result.success(user);
        }

        return Result.error("登录失败！");
    }
}
