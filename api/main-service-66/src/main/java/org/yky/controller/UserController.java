package org.yky.controller;

import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.yky.common.BaseResponse;
import org.yky.common.ResultUtils;
import org.yky.constant.UserConstant;
import org.yky.pojo.User;
import org.yky.pojo.bo.ModifyUserBO;
import org.yky.pojo.vo.UserVO;
import org.yky.service.UserService;

import java.util.UUID;

@RestController
@RequestMapping("/userinfo")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/modify")
    public BaseResponse<UserVO> modify(@RequestBody ModifyUserBO userBO) {
        // 修改用户信息
        userService.modifyUserInfo(userBO);
        // 返回最新用户信息
        UserVO userVO = getUserInfo(userBO.getUserId(), true);
        return ResultUtils.success(userVO);
    }

    private UserVO getUserInfo(String userId, boolean needToken) {
        // 查询获得用户的最新信息
        User latestUser = userService.getById(userId);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(latestUser, userVO);
        if (needToken) {
            String uToken = UserConstant.TOKEN_WEB_PREFIX + UserConstant.SYMBOL_DOT + UUID.randomUUID();
            stringRedisTemplate.opsForValue().set(UserConstant.REDIS_USER_TOKEN + ":" + userId, uToken);
            userVO.setUserToken(uToken);
        }
        return userVO;
    }

    @PostMapping("/get")
    public BaseResponse<UserVO> get(@RequestParam("userId") String userId) {
        return ResultUtils.success(getUserInfo(userId, false));
    }
}
