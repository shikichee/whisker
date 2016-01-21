package com.yo1000.whisker.repository;

import com.yo1000.whisker.model.Repository;

import java.util.List;

/**
 * Created by yoichi.kikuchi on 2016/01/19.
 */
public interface RepositoryRepository {
    List<Repository> select();
    List<Repository> select(String name);
    void insert(Repository repository);
    void update(Repository repository);
}
