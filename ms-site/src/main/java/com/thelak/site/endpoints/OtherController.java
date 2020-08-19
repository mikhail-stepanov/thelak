package com.thelak.site.endpoints;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class OtherController {
    @RequestMapping("{?:(?:(?!api|static|\\.).)*}/**")
    public String redirectApi() {
        return "/index.html";
    }
}