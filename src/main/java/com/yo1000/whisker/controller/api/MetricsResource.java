package com.yo1000.whisker.controller.api;

import com.yo1000.whisker.model.Metrics;
import com.yo1000.whisker.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by yoichi.kikuchi on 2016/01/15.
 */
@RestController
@RequestMapping("api/v1/metrics")
public class MetricsResource {
    private MetricsService metricsService;

    @Autowired
    public MetricsResource(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @RequestMapping(value = "refactoring-impact/bubble", method = RequestMethod.GET)
    public List<Metrics> getBubble() throws IOException {
        return this.getMetricsService().findBubble();
    }

    @RequestMapping(value = "refactoring-impact/bubble/{name}", method = RequestMethod.GET)
    public List<Metrics> getBubble(@PathVariable String name) throws IOException {
        return this.getMetricsService().findBubble(name);
    }


    @RequestMapping(value = "refactoring-impact/scatter", method = RequestMethod.GET)
    public List<Metrics> getScatter() throws IOException {
        return this.getMetricsService().findScatter();
    }

    @RequestMapping(value = "refactoring-impact/scatter/{name}", method = RequestMethod.GET)
    public List<Metrics> getScatter(@PathVariable String name) throws IOException {
        return this.getMetricsService().findScatter(name);
    }

    protected MetricsService getMetricsService() {
        return metricsService;
    }
}
