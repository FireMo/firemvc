package com.enjoy.fire.servlet;

import com.enjoy.fire.annotation.EnjoyController;
import com.enjoy.fire.annotation.EnjoyQualifier;
import com.enjoy.fire.annotation.EnjoyRequestMapping;
import com.enjoy.fire.annotation.EnjoyService;
import com.enjoy.fire.controller.JamesController;
import com.enjoy.fire.hand.HandToolsService;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Descirption
 * @Author FireMo
 * @Date 2019/10/8 21:22
 */
public class DispatcherServlet extends HttpServlet {
    List<String> classNames = new ArrayList<String>();
    Map<String, Object> beans = new HashMap<String, Object>();
    Map<String, Object> handMap = new HashMap<String, Object>();

    public DispatcherServlet() {
    }

    @Override
    public void init(ServletConfig config){
        //tomcat启动时，创建spring容器
        //1.扫描哪些类需要被实例化 class（包，以及包以下的class）
        doScanPackage("com.enjoy");
        for (String cname : classNames){
            System.out.println(cname);
        }
        //2.classNames包含了所有bean的全类名路径
        doInstance();

        //3.依赖注入，把service反射创建的对象依赖注入到controller
        iocDi();

        //4.建立一个URL与method的映射关系
        jamesHandMapper();

        for (Map.Entry<String, Object> entry:handMap.entrySet()){
            System.out.println(entry.getKey() + ":" + entry.getValue());
        }

    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    //请求入口
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //拿到 firemvc/james/query
        String uri = req.getRequestURI();
        String context = req.getContextPath();//拿到firemvc
        String path = uri.replaceAll(context, "");//拿到 james/query
        Method method = (Method) handMap.get(path);//拿到映射到的方法

        JamesController instance = (JamesController) beans.get("/" + path.split("/")[1]);

        HandToolsService hand = (HandToolsService) beans.get("jamesHandTool");
        //处理器,策略模式
        Object[] args = hand.hand(req,resp,method,beans);

        try {
            method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }

    //扫描class类,
    private void doScanPackage(String basePackage){
        //扫描 编译好的项目 路径下的所有类
        URL url = this.getClass().getClassLoader().getResource("/" + basePackage.replaceAll("\\.","/"));
        String fileStr = url.getFile();
        File file = new File(fileStr);
        String[] files = file.list();
        for (String path:files){
            File filePath = new File(fileStr + path);//找到磁盘路径+com.enjoy
            //如果是路径，递归
            if (filePath.isDirectory()){
                doScanPackage(basePackage + "." + path);
            } else {
                //如果不是路径，则属于类名,将所有类名加到数组中
                //格式：com/enjoy/xxxService.class
                classNames.add(basePackage + "." + filePath.getName());
            }
        }

    }

    //实例化所有类,并注入到容器中
    private void doInstance(){
        if (classNames.size() <= 0){
            System.out.println("doScanFailed.........");
            return;
        }
        //遍历扫描到的class全类名路径，通过反射进行创建对象
        for (String className : classNames){
            //将com/enjoy/xxxService.class 变成 com/enjoy/xxxService
            String cn = className.replaceAll(".class", "");

            try {
                Class<?> clazz = Class.forName(cn);//拿到class类,进行加载
                //判断是否有注解
                if (clazz.isAnnotationPresent(EnjoyController.class)){
                    EnjoyController  controller = clazz.getAnnotation(EnjoyController.class);
                    Object instance = clazz.newInstance();//拿到实例化的bean
                    EnjoyRequestMapping requestMapping = clazz.getAnnotation(EnjoyRequestMapping.class);
                    String key = requestMapping.value();
                    beans.put(key, instance);
                    //IOC容器，是一个map<>， map.put(key, value); value就是 instance
                }else if (clazz.isAnnotationPresent(EnjoyService.class)){
                    EnjoyService service = clazz.getAnnotation(EnjoyService.class);
                    Object instance = clazz.newInstance();
                    beans.put(service.value(), instance);
                }else {
                    continue;
                }

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }

        }



    }

    private void iocDi(){
        if (beans.entrySet().size() < 1){
            System.out.println("类没有被实例化");
            return;
        }
        //把service注入到controller中
        for (Map.Entry<String, Object> entry : beans.entrySet()){
            Object instance = entry.getValue();
            //获取类声明了哪些注解
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(EnjoyController.class)){
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields){
                    if (field.isAnnotationPresent(EnjoyQualifier.class)){
                        EnjoyQualifier qualifier = field.getAnnotation(EnjoyQualifier.class);
                        String value = qualifier.value();
                        field.setAccessible(true);//当controller中EnjoyQualifier下的访问修饰符为private或空的时候,放开权限
                        try {
                            field.set(instance, beans.get(value));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                    }else {
                        continue;
                    }
                }
            }else {
                continue;
            }
        }


    }

    //建立映射关系
    private void jamesHandMapper(){
        if (beans.entrySet().size() < 1){
            System.out.println("类没有被实例化");
            return;
        }

        for (Map.Entry<String, Object> entry : beans.entrySet()){
            Object instance = entry.getValue();
            Class<?> clazz = instance.getClass();
            if (clazz.isAnnotationPresent(EnjoyController.class)){
                EnjoyRequestMapping requestMapping = clazz.getAnnotation(EnjoyRequestMapping.class);
                String classPath = requestMapping.value();
                Method[] methods = clazz.getMethods();
                for (Method method : methods){
                    if (method.isAnnotationPresent(EnjoyRequestMapping.class)){
                        EnjoyRequestMapping mapping = method.getAnnotation(EnjoyRequestMapping.class);
                        String methodUrl = mapping.value();
                        handMap.put(classPath+methodUrl, method);
                    }else {
                        continue;
                    }
                }
            }else {
                continue;
            }
        }

    }


}
