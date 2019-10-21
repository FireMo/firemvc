package com.enjoy.fire.argumentResolver;

import com.enjoy.fire.annotation.EnjoyRequestParam;
import com.enjoy.fire.annotation.EnjoyService;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @Descirption
 * @Author FireMo
 * @Date 2019/10/12 9:46
 */
@EnjoyService("requestParamArgResolver")
public class RequestParamArgResolver implements ArgumentResolver {
    @Override
    public boolean support(Class<?> type, int index, Method method) {

        Annotation[][] annotations = method.getParameterAnnotations();
        Annotation[] paramAnnos = annotations[index];
        for (Annotation an:paramAnnos){
            if (EnjoyRequestParam.class.isAssignableFrom(an.getClass())){
                return true;
            }
        }

        return false;
    }

    @Override
    public Object argumentResolver(HttpServletRequest request, HttpServletResponse response, Class<?> type, int index, Method method) {
        Annotation[][] annotations = method.getParameterAnnotations();
        Annotation[] paramAnnos = annotations[index];
        for (Annotation an:paramAnnos){
            if (EnjoyRequestParam.class.isAssignableFrom(an.getClass())){
                EnjoyRequestParam er = (EnjoyRequestParam) an;
                String value = er.value();//name
                return request.getParameter(value);
            }
        }

        return null;
    }
}
