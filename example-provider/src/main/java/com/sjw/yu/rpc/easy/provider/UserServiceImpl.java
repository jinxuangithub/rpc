package com.sjw.yu.rpc.easy.provider;

import com.sjw.shi.rpc.core.yupi.example.common.model.User;
import com.sjw.shi.rpc.core.yupi.example.common.service.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(User user) {
        System.out.println("用户名："+user.getName());
        return user;
    }
}
