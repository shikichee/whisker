package com.yo1000.whisker.service.metrics;

import com.yo1000.whisker.model.Metrics;
import com.yo1000.whisker.model.Repository;
import com.yo1000.whisker.repository.CommitRepository;
import com.yo1000.whisker.repository.ComplexityRepository;
import com.yo1000.whisker.repository.DependencyRepository;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by yoichi.kikuchi on 2016/01/26.
 */
public abstract class MetricsService {
    protected static final Map.Entry<String, Integer> ELSE_ENTRY = new AbstractMap.SimpleEntry<>("", 0);

    private final CommitRepository commitRepository;
    private final ComplexityRepository complexityRepository;
    private final DependencyRepository dependencyRepository;
    private final String gitCommentRegex;
    private final String defaultExtractRegex;
    private final List<String> defaultExtensions;

    public MetricsService(CommitRepository commitRepository,
            ComplexityRepository complexityRepository, DependencyRepository dependencyRepository,
            String gitCommentRegex, String defaultExtractRegex, List<String> defaultExtensions) {
        this.commitRepository = commitRepository;
        this.complexityRepository = complexityRepository;
        this.dependencyRepository = dependencyRepository;
        this.gitCommentRegex = gitCommentRegex;
        this.defaultExtractRegex = defaultExtractRegex;
        this.defaultExtensions = defaultExtensions;
    }

    public abstract Metrics find(Repository repository) throws IOException;

    protected Map<String, List<Metrics.Z>> extractZs(
            List<String> names, Map<String, Integer> xsMap, Map<String, Integer> ysMap, Map<String, Integer> zsMap) {
        return names.stream().collect(TreeMap<String, List<Metrics.Z>>::new,
                (map, s) -> {
                    if (!xsMap.containsKey(s) || !ysMap.containsKey(s)) {
                        return;
                    }
                    String key = "x" + xsMap.get(s) + "y" + ysMap.get(s);
                    List<Metrics.Z> zs = map.get(key);
                    if (zs == null) {
                        zs = new ArrayList<>();
                        map.put(key, zs);
                    }
                    zs.add(new Metrics.Z(s, zsMap.get(s)));
                },
                (map1, map2) -> {});
    }

    protected List<String> extractNames(Set<String> names, Pattern pattern) {
        return names.parallelStream()
                .map(s -> {
                    Matcher matcher = pattern.matcher(s);
                    return matcher.find() ? matcher.group(1) : "";
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    protected CommitRepository getCommitRepository() {
        return commitRepository;
    }

    protected ComplexityRepository getComplexityRepository() {
        return complexityRepository;
    }

    protected DependencyRepository getDependencyRepository() {
        return dependencyRepository;
    }

    protected String getGitCommentRegex() {
        return gitCommentRegex;
    }

    protected String getDefaultExtractRegex() {
        return defaultExtractRegex;
    }

    protected List<String> getDefaultExtensions() {
        return defaultExtensions;
    }
}
