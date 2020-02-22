package com.doopp.gauss.core.service.impl;

import com.doopp.gauss.core.pojo.User;
import com.doopp.gauss.core.service.CoreService;
import org.springframework.stereotype.Service;

@Service("coreService")
public class CoreServiceImpl implements CoreService {

    @Override
    public User getUser() {
        User user = new User();
        user.setId(54612L);
        user.setName("liuyi");
        return user;
    }
}
