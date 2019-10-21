package com.enjoy.fire.argumentResolver;

import com.enjoy.fire.annotation.EnjoyService;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Descirption
 * @Author FireMo
 * @Date 2019/10/12 9:46
 */
@EnjoyService("httpServletRequestArguResolver")
public class HttpServletRequestArguResolver implements ArgumentResolver {
    @Override
    public boolean support(Class<?> type, int index, Method method) {
        return ServletRequest.class.isAssignableFrom(type);
    }

    @Override
    public Object argumentResolver(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index, Method method) {
        return request;
    }
}
