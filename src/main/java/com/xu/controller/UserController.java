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
        if (Objects.equals(realCode, code)) {
            //调用service登录
            User user = userService.login(phone);

            //存入用户id
            httpServletRequest.getSession().setAttribute("user", user.getId());

            return Result.success(user);
        }

        return Result.error("登录失败！");
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
        Page<Category> page1 = userService.getPage(page, pageSize, name);
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
        userService.updateOne(category);
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
        boolean b = userService.removeOne(ids);
        if (b) {
            return Result.success(null);
        }

        return Result.error("删除数据失败！");
    }


    /**
     * 列表查询
     *
     * @param type
     * @return
     */
    @GetMapping("/list")
    public Result<List<Category>> list(int type) {
        log.info(String.valueOf(type));
        List<Category> listByTypeId = userService.getListByTypeId(type);
        if (listByTypeId != null) {
            return Result.success(listByTypeId);
        }
        return Result.error("查询数据失败！");
    }
}
