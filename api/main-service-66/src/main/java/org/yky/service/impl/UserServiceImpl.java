package org.yky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yky.constant.UserConstant;
import org.yky.enums.SexEnum;
import org.yky.exception.BusinessException;
import org.yky.exception.ErrorCode;
import org.yky.exception.ThrowUtils;
import org.yky.mapper.UserMapper;
import org.yky.pojo.User;
import org.yky.pojo.bo.ModifyUserBO;
import org.yky.service.UserService;
import org.yky.utils.LocalDateUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
* @author yky
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-01-05 14:40:59
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Transactional
    @Override
    public void modifyUserInfo(ModifyUserBO userBO) {
        User pendingUser = new User();

        String mychatNum = userBO.getMychatNum();
        String userId = userBO.getUserId();

        ThrowUtils.throwIf(StringUtils.isBlank(userId), ErrorCode.OPERATION_ERROR, "用户信息修改失败");
        if (StringUtils.isNotBlank(mychatNum)) {
            String isExist = stringRedisTemplate.opsForValue().get(UserConstant.REDIS_USER_ALREADY_UPDATE_MYCHAT_NUM + ":" + userId);
            if (StringUtils.isNotBlank(isExist)) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "聊天号已被修改，请等待1年后再修改！");
            }
        }
        pendingUser.setId(userId);
        pendingUser.setUpdatedTime(LocalDateTime.now());
        BeanUtils.copyProperties(userBO, pendingUser);
        userMapper.updateById(pendingUser);

        // 如果用户修改了mychatNum，则只能修改一次，放入redis中进行判断
        if (StringUtils.isNotBlank(mychatNum)) {
            stringRedisTemplate.opsForValue().set(UserConstant.REDIS_USER_ALREADY_UPDATE_MYCHAT_NUM + ":" + userId, userId, 365, TimeUnit.DAYS);
        }
    }

    @Override
    public User getById(String userId) {
        return userMapper.selectById(userId);
    }
}




