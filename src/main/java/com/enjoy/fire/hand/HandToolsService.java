package com.enjoy.fire.hand;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Descirption
 * @Author FireMo
 * @Date 2019/10/11 20:55
 */
public interface HandToolsService {
    public Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method,
                         Map<String, Object> beans);
}
