package com.doopp.gauss.web_server.api.controller;

import com.doopp.gauss.core.message.StandardResponse;
import com.doopp.gauss.core.pojo.User;
import com.doopp.gauss.web_server.api.service.WebService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(value = "/api")
public class TestController {

    @Autowired
    private WebService webService;

    @ResponseBody
    @GetMapping("/user")
    public StandardResponse<User> makeTaskDataListByPublish() {
        return new StandardResponse<>(webService.getUser());
    }
}
