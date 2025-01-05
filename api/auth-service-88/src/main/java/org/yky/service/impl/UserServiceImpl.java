package org.yky.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yky.constant.UserConstant;
import org.yky.enums.SexEnum;
import org.yky.mapper.UserMapper;
import org.yky.pojo.User;
import org.yky.service.UserService;
import org.yky.utils.LocalDateUtils;

import java.time.LocalDateTime;
import java.util.UUID;

/**
* @author yky
* @description 针对表【user】的数据库操作Service实现
* @createDate 2025-01-05 14:40:59
*/
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Resource
    private UserMapper userMapper;

    private static final String USER_FACE = "https://uploadfiles.nowcoder.com/images/20161205/3845004_1480922138730_E2E64FAC44C5C7245ED6583655784727";

    @Override
    public User queryMobileIfExist(String mobile) {
        return userMapper.selectOne(
                new QueryWrapper<User>()
                        .eq("mobile", mobile)
        );
    }

    @Transactional
    @Override
    public User createUser(String mobile) {
        User user = new User();
        user.setMobile(mobile);
        String uuid = UUID.randomUUID().toString();
        String[] uuidStr = uuid.split("-");
        String chatNum = "chat" + uuidStr[0] + uuidStr[1];
        user.setMychatNum(chatNum);
        user.setMychatNumImg(USER_FACE);
        user.setNickname(UserConstant.DEFAULT_NICK);
        user.setRealName("");
        user.setSex(SexEnum.SECRET.type);
        user.setFace(USER_FACE);
        user.setEmail("");
        user.setBirthday(LocalDateUtils
                .parseLocalDate(UserConstant.DEFAULT_BIRTH,
                                LocalDateUtils.DATE_PATTERN));
        user.setCountry("中国");
        user.setProvince("");
        user.setCity("");
        user.setDistrict("");
        user.setCreatedTime(LocalDateTime.now());
        user.setUpdatedTime(LocalDateTime.now());
        userMapper.insert(user);
        return user;
    }
}




