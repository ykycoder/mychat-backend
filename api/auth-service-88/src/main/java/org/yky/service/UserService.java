package org.yky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yky.pojo.User;

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

}
