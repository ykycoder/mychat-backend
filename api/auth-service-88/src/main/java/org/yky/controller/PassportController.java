package org.yky.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.yky.common.BaseResponse;
import org.yky.common.ResultUtils;
import org.yky.constant.UserConstant;
import org.yky.exception.BusinessException;
import org.yky.exception.ErrorCode;
import org.yky.exception.ThrowUtils;
import org.yky.pojo.User;
import org.yky.pojo.bo.RegisterLoginBO;
import org.yky.pojo.vo.UserVO;
import org.yky.service.UserService;
import org.yky.utils.NetUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/passport")
@Slf4j
public class PassportController {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private UserService userService;

    @PostMapping("/getSMSCode")
    public BaseResponse<String> getSMSCode(String mobile, HttpServletRequest request) {
        ThrowUtils.throwIf(StringUtils.isBlank(mobile), ErrorCode.PARAMS_ERROR, "手机号不能为空");
        String userIp = NetUtils.getIpAddress(request);
        stringRedisTemplate.opsForValue().setIfAbsent(UserConstant.MOBILE_SMSCODE + ":" + userIp, mobile, 60, TimeUnit.SECONDS);
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        log.info("验证码为：{}", code);
        stringRedisTemplate.opsForValue().set(UserConstant.MOBILE_SMSCODE + ":" + mobile, code, 10 * 60, TimeUnit.SECONDS);
        return ResultUtils.success(code);
    }

    @PostMapping("/register")
    public BaseResponse<UserVO> register(@RequestBody RegisterLoginBO registerLoginBO, HttpServletRequest request) {
        String mobile = registerLoginBO.getMobile();
        String code = registerLoginBO.getCode();
        // 1. 从redis中获得验证码进行校验
        String redisCode = stringRedisTemplate.opsForValue().get(UserConstant.MOBILE_SMSCODE + ":" + mobile);
        ThrowUtils.throwIf(StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code), ErrorCode.PARAMS_ERROR, "验证码错误");
        // 2. 根据mobile查询数据库，如果用户存在，则不能重复登录
        User user = userService.queryMobileIfExist(mobile);
        if (user == null) {
            user = userService.createUser(mobile);
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户已存在");
        }
        // 3. 用户注册成功后，删除redis中的短信验证码使其失效
        stringRedisTemplate.delete(UserConstant.MOBILE_SMSCODE + ":" + mobile);
        // 4. 设置用户分布式会话，保存用户的token令牌到redis中
        String uToken = UserConstant.TOKEN_WEB_PREFIX + UserConstant.SYMBOL_DOT + UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(UserConstant.REDIS_USER_TOKEN + ":" + user.getId(), uToken);
        // 5. 返回用户数据给前端
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setUserToken(uToken);
        return ResultUtils.success(userVO);
    }

    @PostMapping("/login")
    public BaseResponse<UserVO> login(@RequestBody RegisterLoginBO registerLoginBO, HttpServletRequest request) {
        String mobile = registerLoginBO.getMobile();
        String code = registerLoginBO.getCode();
        // 1. 从redis中获得验证码进行校验
        String redisCode = stringRedisTemplate.opsForValue().get(UserConstant.MOBILE_SMSCODE + ":" + mobile);
        ThrowUtils.throwIf(StringUtils.isBlank(redisCode) || !redisCode.equalsIgnoreCase(code), ErrorCode.PARAMS_ERROR, "验证码错误");
        // 2. 根据mobile查询数据库，如果用户存在，则不能重复登录
        User user = userService.queryMobileIfExist(mobile);
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在");
        }
        // 3. 用户登录成功后，删除redis中的短信验证码使其失效
        stringRedisTemplate.delete(UserConstant.MOBILE_SMSCODE + ":" + mobile);
        // 4. 设置用户分布式会话，保存用户的token令牌到redis中
        String uToken = UserConstant.TOKEN_WEB_PREFIX + UserConstant.SYMBOL_DOT + UUID.randomUUID();
        stringRedisTemplate.opsForValue().set(UserConstant.REDIS_USER_TOKEN + ":" + user.getId(), uToken);
        // 5. 返回用户数据给前端
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setUserToken(uToken);
        return ResultUtils.success(userVO);
    }

    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(@RequestParam String userId, HttpServletRequest request) {
        Boolean result = stringRedisTemplate.delete(UserConstant.REDIS_USER_TOKEN + ":" + userId);
        ThrowUtils.throwIf(!result, ErrorCode.SYSTEM_ERROR, "退出登录失败");
        return ResultUtils.success(true);
    }

    @GetMapping("/get/login")
    public BaseResponse<UserVO> getLoginUser(@RequestParam String userId) {
        User loginUser = userService.getLoginUser(userId);
        return ResultUtils.success(userService.getUserVO(loginUser));
    }
}
