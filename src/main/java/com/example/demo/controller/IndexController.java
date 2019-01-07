package com.example.demo.controller;

import com.example.demo.limiting.Limiting;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("index")
    @Limiting(value = "index")
    public String index() throws InterruptedException {
        Thread.sleep(10000);
        return "index";
    }

    @GetMapping("limiting")
    public String limiting() {
        return "limiting";
    }

}
