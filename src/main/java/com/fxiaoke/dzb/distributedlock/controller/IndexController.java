package com.fxiaoke.dzb.distributedlock.controller;

import com.fxiaoke.dzb.distributedlock.annotation.SubmitAgainAnnotation;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: dongzhb
 * @date: 2019/5/10
 * @Description:
 */
@RestController
public class IndexController {
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    @SubmitAgainAnnotation
    public String toIndex(HttpServletRequest request, Map<String, Object> map) {
        return "form";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    @SubmitAgainAnnotation
    public String add(HttpServletRequest request) {
        try {
            //模拟执行业务逻辑需要的时间
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "保存成功！";
    }
}
