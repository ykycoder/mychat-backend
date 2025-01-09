package org.yky.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yky.pojo.User;
import org.yky.pojo.bo.ModifyUserBO;

/**
* @author yky
* @description 针对表【user】的数据库操作Service
* @createDate 2025-01-05 14:41:00
*/
public interface UserService extends IService<User> {

    /**
     * 修改用户基本信息
     *
     * @param userBO
     */
    public void modifyUserInfo(ModifyUserBO userBO);

    /**
     * 获得用户信息
     *
     * @param userId
     * @return
     */
    public User getById(String userId);

}
