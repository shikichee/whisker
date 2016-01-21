package com.yo1000.whisker.controller.page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by yoichi.kikuchi on 2016/01/14.
 */
@Controller
@RequestMapping("scatter")
public class ScatterPage {
    @RequestMapping(value = "refactoring-impact", method = RequestMethod.GET)
    public String get(Model model) {
        model.addAttribute("title", "Refactoring impact");
        model.addAttribute("metricsApi", "/metrics/refactoring-impact");
        return "scatter";
    }

    @RequestMapping(value = "refactoring-impact/{name}", method = RequestMethod.GET)
    public String getName(@PathVariable String name, Model model) {
        model.addAttribute("title", "Refactoring impact - " + name);
        model.addAttribute("metricsApi", "/metrics/refactoring-impact/" + name);
        return "scatter";
    }
}
