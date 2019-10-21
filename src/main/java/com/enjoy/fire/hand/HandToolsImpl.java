package com.enjoy.fire.hand;

import com.enjoy.fire.annotation.EnjoyService;
import com.enjoy.fire.argumentResolver.ArgumentResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @Descirption
 * @Author FireMo
 * @Date 2019/10/11 20:55
 */
@EnjoyService("jamesHandTool")
public class HandToolsImpl implements HandToolsService{

    //返回方法里的所有参数
    @Override
    public Object[] hand(HttpServletRequest request, HttpServletResponse response, Method method, Map<String, Object> beans) {
        Class<?>[] paramClazzs = method.getParameterTypes();//拿到方法的参数
        Object[] args = new Object[paramClazzs.length];

        //拿到所有实现了ArgumentResolver这个接口的实例
        Map<String, Object> argResolvers = getInstanceType(beans, ArgumentResolver.class);

        int index = 0;
        int i = 0;
        for (Class<?> paramClazz : paramClazzs){
            //哪个参数对应了哪个解析类，用策略模式来找
            for (Map.Entry<String, Object> entry : argResolvers.entrySet()){
                ArgumentResolver ar = (ArgumentResolver) entry.getValue();
                if (ar.support(paramClazz, index, method)){
                    args[i++] = ar.argumentResolver(request,response,paramClazz,index,method);
                }
            }
            index++;
        }


        return args;
    }

    public Map<String, Object> getInstanceType(Map<String, Object> beans, Class<?> type){
        Map<String, Object> resultBeans = new HashMap<>();
        for (Map.Entry<String, Object> entry:beans.entrySet()){
            Class<?>[] infs = entry.getValue().getClass().getInterfaces();//是否实现接口
            if (infs != null && infs.length > 0){
                for (Class<?> inf : infs){
                    if (inf.isAssignableFrom(type)){
                        resultBeans.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        return resultBeans;
    }
}
