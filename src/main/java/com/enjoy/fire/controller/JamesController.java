package com.enjoy.fire.controller;

import com.enjoy.fire.annotation.EnjoyController;
import com.enjoy.fire.annotation.EnjoyQualifier;
import com.enjoy.fire.annotation.EnjoyRequestMapping;
import com.enjoy.fire.annotation.EnjoyRequestParam;
import com.enjoy.fire.service.JamesService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Descirption
 * @Author FireMo
 * @Date 2019/10/8 21:09
 */
@EnjoyController
@EnjoyRequestMapping("/james")
public class JamesController {

    @EnjoyQualifier("JamesServiceImpl")
    private JamesService jamesService;


    @EnjoyRequestMapping("/query")
    public void query(HttpServletRequest request, HttpServletResponse response,
                      @EnjoyRequestParam("name") String name,
                      @EnjoyRequestParam("age") String age){
        PrintWriter pw = null;
        try {
            pw = response.getWriter();
            String result = jamesService.query(name, age);
            pw.write(result);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
