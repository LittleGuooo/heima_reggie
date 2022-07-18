package com.xu.filter;

import com.alibaba.fastjson.JSON;
import com.xu.common.BaseContext;
import com.xu.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter("/*")
public class LoginCheckFilter implements Filter {
    //AntPathMatcher工具类对下面uri的比较提供通配符解析功能
    private static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("当前线程：" + Thread.currentThread().getId() + " " + Thread.currentThread());

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        //1.判断本次请求是否需要检查
        String requestURI = httpServletRequest.getRequestURI();
        log.info("当前请求路径：" + requestURI);

        //可以放行的路径（动态资源的请求之外，都可以放行）
        String[] uris = new String[]{
               "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg"
        };
        if (doFilterHelper(requestURI,uris)) {
            //放行
            chain.doFilter(request, response);
            return;
        }

        //2-1.判断Employee是否已登录
        Long employeeId = (Long) httpServletRequest.getSession().getAttribute("employee");
        if (employeeId != null) {
            //设置threadLocal的值（员工id）
            BaseContext.setValue(employeeId);
            log.info("当前请求的employeeId：" + employeeId);

            //放行
            chain.doFilter(request, response);
            return;
        }

        //2-2.判断User是否已登录
        Long userId = (Long) httpServletRequest.getSession().getAttribute("user");
        if (userId != null) {
            //设置threadLocal的值（员工id）
            BaseContext.setValue(userId);
            log.info("当前请求的userId：" + userId);

            //放行
            chain.doFilter(request, response);
            return;
        }

        //3.对未通过检查请求进行拦截（返回相应Result由前端跳转）
        httpServletResponse.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));
    }

    private boolean doFilterHelper(String uri, String[] uris) {
        for (String s : uris) {
            if (ANT_PATH_MATCHER.match(s, uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
