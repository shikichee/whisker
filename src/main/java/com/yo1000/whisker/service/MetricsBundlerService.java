package com.yo1000.whisker.service;

import com.yo1000.whisker.model.Metrics;
import com.yo1000.whisker.repository.RepositoryRepository;
import com.yo1000.whisker.service.metrics.BubbleService;
import com.yo1000.whisker.service.metrics.MetricsService;
import com.yo1000.whisker.service.metrics.ScatterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yoichi.kikuchi on 2016/01/26.
 */
@Service
public class MetricsBundlerService {
    private final BubbleService bubbleService;
    private final ScatterService scatterService;
    private final RepositoryRepository repositoryRepository;

    @Autowired
    public MetricsBundlerService(BubbleService bubbleService, ScatterService scatterService, RepositoryRepository repositoryRepository) {
        this.bubbleService = bubbleService;
        this.scatterService = scatterService;
        this.repositoryRepository = repositoryRepository;
    }

    public List<Metrics> findBubble() throws IOException {
        return this.find(this.getBubbleService());
    }

    public List<Metrics> findBubble(String name) throws IOException {
        String test = null;
        if(test!=null){
            System.out.println("test");
        }
        
        return this.find(this.getBubbleService(), name);
    }

    public List<Metrics> findScatter() throws IOException {
        return this.find(this.getScatterService());
    }

    public List<Metrics> findScatter(String name) throws IOException {
        return this.find(this.getScatterService(), name);
    }

    protected List<Metrics> find(MetricsService service) throws IOException {
        return this.getRepositoryRepository().select().parallelStream().map(repository -> {
            try {
                return service.find(repository);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).collect(Collectors.toList());
    }

    protected List<Metrics> find(MetricsService service, String name) throws IOException {
        return this.getRepositoryRepository().select(name).parallelStream().map(repository -> {
            try {
                return service.find(repository);
            } catch (IOException e) {
                throw new UncheckedIOException("Encoding is invalid.");
            }
        }).collect(Collectors.toList());
    }

    protected BubbleService getBubbleService() {
        return bubbleService;
    }

    protected ScatterService getScatterService() {
        return scatterService;
    }

    protected RepositoryRepository getRepositoryRepository() {
        return repositoryRepository;
    }
}
