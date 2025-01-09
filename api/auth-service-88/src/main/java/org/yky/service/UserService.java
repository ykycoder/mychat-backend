package org.yky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yky.pojo.User;
import org.yky.pojo.vo.UserVO;

import jakarta.servlet.http.HttpServletRequest;

/**
* @author yky
* @description 针对表【user】的数据库操作Service
* @createDate 2025-01-05 14:41:00
*/
public interface UserService extends IService<User> {

    /**
     * 判断用户是否存在
     *
     * @param mobile
     * @return
     */
    public User queryMobileIfExist(String mobile);

    /**
     * 创建用户信息
     *
     * @param mobile
     * @return
     */
    public User createUser(String mobile);

    /**
     * 获取当前登录用户
     *
     * @param userId
     * @return
     */
    User getLoginUser(String userId);

    /**
     * 获得脱敏后的用户登录信息
     *
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

}
