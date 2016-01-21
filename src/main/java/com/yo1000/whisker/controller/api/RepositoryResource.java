package com.yo1000.whisker.controller.api;

import com.yo1000.whisker.model.Repository;
import com.yo1000.whisker.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yoichi.kikuchi on 2016/01/17.
 */
@RestController
@RequestMapping("api/v1/repositories")
public class RepositoryResource {
    private RepositoryService repositoryService;

    @Autowired
    public RepositoryResource(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public List<Repository> get() {
        return this.getRepositoryService().find();
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@RequestBody Repository repository,
                       @Value("${application.metrics.default.extract-regex}") String defaultRegex,
                       @Value("${application.metrics.default.extensions}") String defaultExtensions,
                       @Value("${application.metrics.default.filter}") String defaultFilter) {
        if (repository.getExtensions() == null || repository.getExtensions().isEmpty()) {
            repository.setExtensions(Arrays.asList(defaultExtensions.replaceAll("\\s", "").split(",")));
        }
        if (repository.getFilter() == null || repository.getFilter().isEmpty()) {
            repository.setFilter(defaultFilter);
        }
        if (repository.getGit().getExtractRegex() == null || repository.getGit().getExtractRegex().isEmpty()) {
            repository.getGit().setExtractRegex(defaultRegex);
        }
        if (repository.getSource().getExtractRegex() == null || repository.getSource().getExtractRegex().isEmpty()) {
            repository.getSource().setExtractRegex(defaultRegex);
        }
        if (repository.getClassFile().getExtractRegex() == null || repository.getClassFile().getExtractRegex().isEmpty()) {
            repository.getClassFile().setExtractRegex(defaultRegex);
        }
        this.getRepositoryService().create(repository);
    }

    @RequestMapping(value = "", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.OK)
    public void put(@RequestBody Repository repository,
                      @Value("${application.metrics.default.extract-regex}") String defaultRegex,
                      @Value("${application.metrics.default.extensions}") String defaultExtensions,
                      @Value("${application.metrics.default.filter}") String defaultFilter) {
        if (repository.getExtensions() == null || repository.getExtensions().isEmpty()) {
            repository.setExtensions(Arrays.asList(defaultExtensions.replaceAll("\\s", "").split(",")));
        }
        if (repository.getFilter() == null || repository.getFilter().isEmpty()) {
            repository.setFilter(defaultFilter);
        }
        if (repository.getGit().getExtractRegex() == null || repository.getGit().getExtractRegex().isEmpty()) {
            repository.getGit().setExtractRegex(defaultRegex);
        }
        if (repository.getSource().getExtractRegex() == null || repository.getSource().getExtractRegex().isEmpty()) {
            repository.getSource().setExtractRegex(defaultRegex);
        }
        if (repository.getClassFile().getExtractRegex() == null || repository.getClassFile().getExtractRegex().isEmpty()) {
            repository.getClassFile().setExtractRegex(defaultRegex);
        }
        this.getRepositoryService().modify(repository);
    }

    protected RepositoryService getRepositoryService() {
        return repositoryService;
    }
}
