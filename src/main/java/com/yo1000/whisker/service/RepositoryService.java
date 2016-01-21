package com.yo1000.whisker.service;

import com.yo1000.whisker.component.Identifier;
import com.yo1000.whisker.model.Repository;
import com.yo1000.whisker.repository.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yoichi.kikuchi on 2016/01/19.
 */
@Service
public class RepositoryService {
    private final RepositoryRepository repositoryRepository;
    private final Identifier identifier;

    @Autowired
    public RepositoryService(RepositoryRepository repositoryRepository, Identifier identifier) {
        this.repositoryRepository = repositoryRepository;
        this.identifier = identifier;
    }

    public List<Repository> find() {
        return this.getRepositoryRepository().select();
    }

    public void create(Repository repository) {
        repository.setId(this.getIdentifier().generate());
        repository.setModifier("UNKNOWN"); // TODO: Require the user management feature.
        this.getRepositoryRepository().insert(repository);
    }

    public void modify(Repository repository) {
        if (repository.getId() == null || repository.getId().isEmpty()) {
            throw new IllegalArgumentException("Repository#getId() " + repository.getId());
        }
        repository.setModifier("UNKNOWN"); // TODO: Require the user management feature.
        this.getRepositoryRepository().update(repository);
    }

    protected RepositoryRepository getRepositoryRepository() {
        return repositoryRepository;
    }

    protected Identifier getIdentifier() {
        return identifier;
    }
}
