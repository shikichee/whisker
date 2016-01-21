package com.yo1000.whisker.controller.api;

import com.yo1000.whisker.model.Metrics;
import com.yo1000.whisker.service.MetricsService;
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
    private MetricsService metricsService;

    @Autowired
    public MetricsResource(MetricsService metricsService) {
        this.metricsService = metricsService;
    }

    @RequestMapping(value = "refactoring-impact", method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8",
            consumes = "application/json")
    public List<Metrics> get() throws IOException {
        return this.getMetricsService().find();
    }

    @RequestMapping(value = "refactoring-impact/{name}", method = RequestMethod.GET,
            produces = "application/json;charset=UTF-8",
            consumes = "application/json")
    public List<Metrics> get(@PathVariable String name) throws IOException {
        return this.getMetricsService().find(name);
    }

    protected MetricsService getMetricsService() {
        return metricsService;
    }
}
