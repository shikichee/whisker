package com.yo1000.whisker.controller.api;

import com.yo1000.whisker.model.Metrics;
import com.yo1000.whisker.service.MetricsBundlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Created by yoichi.kikuchi on 2016/01/15.
 */
@RestController
@RequestMapping("api/v1/metrics")
public class MetricsResource {
    private final MetricsBundlerService metricsBundlerService;

    @Autowired
    public MetricsResource(MetricsBundlerService metricsBundlerService) {
        this.metricsBundlerService = metricsBundlerService;
    }

    @RequestMapping(value = "refactoring-impact/bubble", method = RequestMethod.GET)
    public List<Metrics> getBubble() throws IOException {
        return this.getMetricsBundlerService().findBubble();
    }

    @RequestMapping(value = "refactoring-impact/bubble/{name}", method = RequestMethod.GET)
    public List<Metrics> getBubble(@PathVariable String name) throws IOException {
        return this.getMetricsBundlerService().findBubble(name);
    }

    @RequestMapping(value = "refactoring-impact/scatter", method = RequestMethod.GET)
    public List<Metrics> getScatter() throws IOException {
        return this.getMetricsBundlerService().findScatter();
    }

    @RequestMapping(value = "refactoring-impact/scatter/{name}", method = RequestMethod.GET)
    public List<Metrics> getScatter(@PathVariable String name) throws IOException {
        return this.getMetricsBundlerService().findScatter(name);
    }

    public MetricsBundlerService getMetricsBundlerService() {
        return metricsBundlerService;
    }
}
