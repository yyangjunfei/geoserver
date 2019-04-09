package com.leoco.getworkstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class BaseController {
    @RequestMapping(value = "")
    public void base(HttpServletResponse response) throws IOException {
        response.sendRedirect("/leoco/getwork");
    }
}