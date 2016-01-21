package com.yo1000.whisker.repository.jdbc;

import com.yo1000.whisker.model.Environment;
import com.yo1000.whisker.repository.RepositoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by yoichi.kikuchi on 2016/01/19.
 */
@Repository
public class RepositoryJdbcRepository implements RepositoryRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public RepositoryJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<com.yo1000.whisker.model.Repository> select() {
        return this.getJdbcTemplate().query("SELECT " +
                "    REPO_ID    , " +
                "    REPO_NAME  , " +
                "    GIT_DIR    , " +
                "    GIT_REGX   , " +
                "    SRC_DIR    , " +
                "    SRC_REGX   , " +
                "    CLS_DIR    , " +
                "    CLS_REGX   , " +
                "    EXTENSIONS , " +
                "    FILTER , " +
                "    MODIFIER   , " +
                "    MODIFIED " +
                "FROM " +
                "    REPOSITORY ",
                (resultSet, i) -> {
                    return new com.yo1000.whisker.model.Repository(
                            resultSet.getString("REPO_ID"),
                            resultSet.getString("REPO_NAME"),
                            new Environment(resultSet.getString("GIT_DIR"), resultSet.getString("GIT_REGX")),
                            new Environment(resultSet.getString("SRC_DIR"), resultSet.getString("SRC_REGX")),
                            new Environment(resultSet.getString("CLS_DIR"), resultSet.getString("CLS_REGX")),
                            Arrays.asList(resultSet.getString("EXTENSIONS").replaceAll("\\s", "").split(",")),
                            resultSet.getString("FILTER"),
                            resultSet.getString("MODIFIER"),
                            resultSet.getDate("MODIFIED"));
                });
    }

    @Override
    public List<com.yo1000.whisker.model.Repository> select(String name) {
        return this.getJdbcTemplate().query("SELECT " +
                        "    REPO_ID    , " +
                        "    REPO_NAME  , " +
                        "    GIT_DIR    , " +
                        "    GIT_REGX   , " +
                        "    SRC_DIR    , " +
                        "    SRC_REGX   , " +
                        "    CLS_DIR    , " +
                        "    CLS_REGX   , " +
                        "    EXTENSIONS , " +
                        "    FILTER     , " +
                        "    MODIFIER   , " +
                        "    MODIFIED " +
                        "FROM " +
                        "    REPOSITORY " +
                        "WHERE " +
                        "    REPO_NAME = :repoName",
                new HashMap<String, Object>() {{ this.put("repoName", name); }},
                (resultSet, i) -> {
                    return new com.yo1000.whisker.model.Repository(
                            resultSet.getString("REPO_ID"),
                            resultSet.getString("REPO_NAME"),
                            new Environment(resultSet.getString("GIT_DIR"), resultSet.getString("GIT_REGX")),
                            new Environment(resultSet.getString("SRC_DIR"), resultSet.getString("SRC_REGX")),
                            new Environment(resultSet.getString("CLS_DIR"), resultSet.getString("CLS_REGX")),
                            Arrays.asList(resultSet.getString("EXTENSIONS").replaceAll("\\s", "").split(",")),
                            resultSet.getString("FILTER"),
                            resultSet.getString("MODIFIER"),
                            resultSet.getDate("MODIFIED"));
                });
    }

    @Override
    public void insert(com.yo1000.whisker.model.Repository repository) {
        this.getJdbcTemplate().update("INSERT INTO " +
                "REPOSITORY(" +
                "    REPO_ID, REPO_NAME, " +
                "    GIT_DIR, GIT_REGX , " +
                "    CLS_DIR, CLS_REGX , " +
                "    SRC_DIR, SRC_REGX , " +
                "    EXTENSIONS, " +
                "    FILTER, " +
                "    MODIFIER, " +
                "    MODIFIED " +
                ") VALUES (" +
                "    :repoId, :repoName, " +
                "    :gitDir, :gitRegx , " +
                "    :clsDir, :clsRegx , " +
                "    :srcDir, :srcRegx , " +
                "    :extensions, " +
                "    :filter, " +
                "    :modifier, " +
                "    CURRENT_TIMESTAMP() " +
                ")",
                new HashMap<String, Object>() {
                    {
                        this.put("repoId", repository.getId());
                        this.put("repoName", repository.getName());
                        this.put("extensions", repository.getExtensions().stream().reduce((s1, s2) -> s1 + "," + s2).get());
                        this.put("filter", repository.getFilter());
                        this.put("gitDir", repository.getGit().getDirectory());
                        this.put("gitRegx", repository.getGit().getExtractRegex());
                        this.put("srcDir", repository.getSource().getDirectory());
                        this.put("srcRegx", repository.getSource().getExtractRegex());
                        this.put("clsDir", repository.getClassFile().getDirectory());
                        this.put("clsRegx", repository.getClassFile().getExtractRegex());
                        this.put("modifier", repository.getModifier());
                    }
                });
    }

    @Override
    public void update(com.yo1000.whisker.model.Repository repository) {
        this.getJdbcTemplate().update("UPDATE " +
                "    REPOSITORY " +
                " SET " +
                "    REPO_NAME  = :repoName     , " +
                "    GIT_DIR    = :gitDir       , " +
                "    GIT_REGX   = :gitRegx      , " +
                "    CLS_DIR    = :clsDir       , " +
                "    CLS_REGX   = :clsRegx      , " +
                "    SRC_DIR    = :srcDir       , " +
                "    SRC_REGX   = :srcRegx      , " +
                "    EXTENSIONS = :extensions   , " +
                "    FILTER     = :filter       , " +
                "    MODIFIER   = :modifier     , " +
                "    MODIFIED   = CURRENT_TIMESTAMP()" +
                " WHERE " +
                "    REPO_ID    = :repoId ",
                new HashMap<String, Object>() {
                    {
                        this.put("repoId", repository.getId());
                        this.put("repoName", repository.getName());
                        this.put("extensions", repository.getExtensions().stream().reduce((s1, s2) -> s1 + "," + s2).get());
                        this.put("filter", repository.getFilter());
                        this.put("gitDir", repository.getGit().getDirectory());
                        this.put("gitRegx", repository.getGit().getExtractRegex());
                        this.put("srcDir", repository.getSource().getDirectory());
                        this.put("srcRegx", repository.getSource().getExtractRegex());
                        this.put("clsDir", repository.getClassFile().getDirectory());
                        this.put("clsRegx", repository.getClassFile().getExtractRegex());
                        this.put("modifier", repository.getModifier());
                    }
                });
    }

    protected NamedParameterJdbcTemplate getJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }
}
