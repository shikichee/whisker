package com.yo1000.whisker.controller.page;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yoichi.kikuchi on 2016/01/14.
 */
@Controller
@RequestMapping("chart")
public class ChartPage {
    protected static final List SUPPORTED_CHARTS = Arrays.asList("bubble", "scatter");

    @RequestMapping(value = "refactoring-impact/{chart}", method = RequestMethod.GET)
    public String get(Model model,
                      @PathVariable String chart,
                      @Value("${application.chart.xaxis}") String xaxis,
                      @Value("${application.chart.yaxis}") String yaxis,
                      @Value("${application.chart.tooltip.xaxis}") String xaxisTooltip,
                      @Value("${application.chart.tooltip.yaxis}") String yaxisTooltip) {
        model.addAttribute("title", "Refactoring impact");
        model.addAttribute("chart", chart);
        model.addAttribute("xaxis", xaxis);
        model.addAttribute("yaxis", yaxis);
        model.addAttribute("xaxisTooltip", xaxisTooltip);
        model.addAttribute("yaxisTooltip", yaxisTooltip);

        model.addAttribute("metricsApi", "/metrics/refactoring-impact/" +
                (SUPPORTED_CHARTS.contains(chart) ? chart : SUPPORTED_CHARTS.get(0)));
        return "chart";
    }

    @RequestMapping(value = "refactoring-impact/{chart}/{name}", method = RequestMethod.GET)
    public String getName(Model model,
                          @PathVariable String chart,
                          @PathVariable String name,
                          @Value("${application.chart.xaxis}") String xaxis,
                          @Value("${application.chart.yaxis}") String yaxis,
                          @Value("${application.chart.tooltip.xaxis}") String xaxisTooltip,
                          @Value("${application.chart.tooltip.yaxis}") String yaxisTooltip) {
        model.addAttribute("title", "Refactoring impact - " + name);
        model.addAttribute("chart", chart);
        model.addAttribute("xaxis", xaxis);
        model.addAttribute("yaxis", yaxis);
        model.addAttribute("xaxisTooltip", xaxisTooltip);
        model.addAttribute("yaxisTooltip", yaxisTooltip);

        model.addAttribute("metricsApi", "/metrics/refactoring-impact/" +
                (SUPPORTED_CHARTS.contains(chart) ? chart : SUPPORTED_CHARTS.get(0)) + "/" + name);
        return "chart";
    }
}
