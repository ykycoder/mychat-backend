package org.yky.controller.interceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.yky.constant.UserConstant;
import org.yky.exception.BusinessException;
import org.yky.exception.ErrorCode;
import org.yky.utils.NetUtils;

@Slf4j
public class SMSInterceptor implements HandlerInterceptor {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIp = NetUtils.getIpAddress(request);
        String value = stringRedisTemplate.opsForValue().get(UserConstant.MOBILE_SMSCODE + ":" + userIp);
        if (value != null) {
            log.error("短信发送频率过高，请稍后再试");
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "短信发送频率过高，请稍后再试");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
