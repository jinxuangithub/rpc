package com.sjw.yu.rpc.easy.consumer;
import com.sjw.shi.rpc.core.proxy.MockServiceProxyFactory;
import com.sjw.shi.rpc.core.yupi.example.common.model.User;
import com.sjw.shi.rpc.core.yupi.example.common.service.UserService;

public class consumer {
    public static void main(String[] args) {
        UserService userService = MockServiceProxyFactory.createProxy(UserService.class);
        User user = new User();
        user.setName("sjw");
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println(" user is null");
            // 处理返回结果为null的情况);
        }
        short s=userService.getUserAge();

        System.out.println(s);
    }
}
