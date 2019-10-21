package com.enjoy.fire.argumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @Descirption
 * @Author FireMo
 * @Date 2019/10/12 9:19
 */
public interface ArgumentResolver {
    //判断是否为当前需要解析的类
    public boolean support(Class<?> type, int index, Method method);

    //解析参数
    public Object argumentResolver(HttpServletRequest request, HttpServletResponse response,Class<?> type, int index, Method method);
}
