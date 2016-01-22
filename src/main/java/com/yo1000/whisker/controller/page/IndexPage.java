package com.yo1000.whisker.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by yoichi.kikuchi on 2016/01/14.
 */
@Controller
@RequestMapping("")
public class IndexPage {
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String get() {
        return "redirect:/repository";
    }
}
