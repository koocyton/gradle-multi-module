package com.doopp.gauss.web_server.api.service.impl;

import com.doopp.gauss.core.pojo.User;
import com.doopp.gauss.core.service.CoreService;
import com.doopp.gauss.web_server.api.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("webService")
public class WebServiceImpl implements WebService {

    @Autowired
    private CoreService coreService;

    @Override
    public User getUser() {
        return coreService.getUser();
    }
}
