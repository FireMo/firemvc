package com.enjoy.fire.service.impl;

import com.enjoy.fire.annotation.EnjoyService;
import com.enjoy.fire.service.JamesService;

/**
 * @Descirption
 * @Author FireMo
 * @Date 2019/10/8 21:14
 */
@EnjoyService("JamesServiceImpl")
public class JamesServiceImpl implements JamesService {
    @Override
    public String query(String name, String age) {
        return "name ==" + name + " age ==" + age;
    }

    @Override
    public String insert(String param) {
        return "insert successful....";
    }

    @Override
    public String update(String param) {
        return "update successful......";
    }
}
