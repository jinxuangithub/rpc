package com.sjw.shi.rpc.core.yupi.example.common.service;

import com.sjw.shi.rpc.core.yupi.example.common.model.User;

public interface UserService {
    User getUser(User user);

    default short getUserAge() {
        return 10;
    }
}
