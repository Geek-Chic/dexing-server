package com.company.project;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by evil on 10/13/17.
 */
public class CrossOriginInterceptor extends HandlerInterceptorAdapter {
    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                       Object handler) throws Exception {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Origin");
        response.setHeader("Content-Type","application/json;charset=UTF-8");
        return true;
    }
}
