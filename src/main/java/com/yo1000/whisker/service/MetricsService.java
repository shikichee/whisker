package com.yo1000.whisker.service;

import com.yo1000.whisker.model.Metrics;
import com.yo1000.whisker.model.Repository;
import com.yo1000.whisker.repository.CommitRepository;
import com.yo1000.whisker.repository.RepositoryRepository;
import com.yo1000.winchester.analyzer.CyclomaticComplexity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by yoichi.kikuchi on 2016/01/15.
 */
@Service
public class MetricsService {
    protected static final Map.Entry<String, Integer> ELSE_ENTRY = new AbstractMap.SimpleEntry<>("", 0);

    private final RepositoryRepository repositoryRepository;
    private final CommitRepository commitRepository;
    private final String gitCommentRegex;
    private final String defaultExtractRegex;
    private final List<String> defaultExtensions;

    @Autowired
    public MetricsService(
            RepositoryRepository repositoryRepository,
            CommitRepository commitRepository,
            @Value("${application.metrics.git.comment-regex}") String gitCommentRegex,
            @Value("${application.metrics.default.extract-regex}") String defaultExtractRegex,
            @Value("#{T(java.util.Arrays).asList('${application.metrics.default.extensions}')}") List<String> defaultExtensions) {
        this.repositoryRepository = repositoryRepository;
        this.commitRepository = commitRepository;
        this.gitCommentRegex = gitCommentRegex;
        this.defaultExtractRegex = defaultExtractRegex;
        this.defaultExtensions = defaultExtensions;
    }

    public List<Metrics> find() throws IOException {
        return this.getRepositoryRepository().select().parallelStream().map(repository -> {
            try {
                return this.find(repository);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).collect(Collectors.toList());
    }

    public List<Metrics> find(String name) throws IOException {
        return this.getRepositoryRepository().select(name).parallelStream().map(repository -> {
            try {
                return this.find(repository);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).collect(Collectors.toList());
    }

    protected Metrics find(Repository repository) throws IOException {
        final Map<String, Integer> fixDiffsMap = this.getCommitRepository().selectDiffSummaryByLog(Paths.get(
                repository.getGit().getDirectory()).toFile(), this.getGitCommentRegex());

        final Map<String, Integer> complexityMap = new CyclomaticComplexity().analyze(Paths.get(
                repository.getSource().getDirectory()),
                repository.getExtensions() != null && !repository.getExtensions().isEmpty()
                        ? repository.getExtensions() : this.getDefaultExtensions());

        final Map<String, Integer> dependencyMap = new CyclomaticComplexity().analyze(Paths.get(
                repository.getClassFile().getDirectory()), Arrays.asList(".class"));

        final Pattern defaultFileExtractPattern = Pattern.compile(this.getDefaultExtractRegex());
        final Pattern gitFileExtractPattern =
                repository.getGit().getExtractRegex() != null && !repository.getGit().getExtractRegex().isEmpty()
                        ? Pattern.compile(repository.getGit().getExtractRegex()) : defaultFileExtractPattern;
        final Pattern complexityFileExtractPattern =
                repository.getClassFile().getExtractRegex() != null && !repository.getClassFile().getExtractRegex().isEmpty()
                        ? Pattern.compile(repository.getClassFile().getExtractRegex()) : defaultFileExtractPattern;
        final Pattern dependencyFileExtractPattern =
                repository.getSource().getExtractRegex() != null && !repository.getSource().getExtractRegex().isEmpty()
                        ? Pattern.compile(repository.getSource().getExtractRegex()) : defaultFileExtractPattern;

        List<String> names = fixDiffsMap.keySet().parallelStream()
                .map(s -> {
                    Matcher matcher = gitFileExtractPattern.matcher(s);
                    return matcher.find() ? matcher.group(1) : "";
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        names.addAll(complexityMap.keySet().parallelStream()
                .map(s -> {
                    Matcher matcher = complexityFileExtractPattern.matcher(s);
                    return matcher.find() ? matcher.group(1) : "";
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList()));

        names.addAll(dependencyMap.keySet().parallelStream()
                .map(s -> {
                    Matcher matcher = dependencyFileExtractPattern.matcher(s);
                    return matcher.find() ? matcher.group(1) : "";
                })
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList()));

        names = names.parallelStream().parallel()
                .distinct()
                .filter(s -> s.matches(repository.getFilter()))
                .sorted()
                .collect(Collectors.toList());

        Map<String, Integer> normalizedComplexityMap = names.parallelStream()
                .map(s -> new AbstractMap.SimpleEntry<>(s, complexityMap.entrySet().parallelStream()
                        .filter(entry -> entry.getKey().contains(s))
                        .findFirst()
                        .orElse(ELSE_ENTRY).getValue())
                )
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        Map<String, Integer> normalizedDependencyMap = names.parallelStream()
                .map(s -> new AbstractMap.SimpleEntry<>(s, dependencyMap.entrySet().parallelStream()
                        .filter(entry -> entry.getKey().contains(s))
                        .findFirst()
                        .orElse(ELSE_ENTRY).getValue())
                )
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        Map<String, Integer> xsMap = normalizedDependencyMap.entrySet().parallelStream()
                .map(dependencyEntry -> {
                    String key = dependencyEntry.getKey();
                    Integer dependencyValue = dependencyEntry.getValue();
                    Integer complexityValue = normalizedComplexityMap.get(key);

                    return new AbstractMap.SimpleEntry<>(key,
                            (dependencyValue > 0 ? dependencyValue : 1) * (complexityValue > 0 ? complexityValue : 1));
                })
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        Map<String, Integer> ysMap = names.parallelStream()
                .map(s -> new AbstractMap.SimpleEntry<>(s, fixDiffsMap.entrySet().parallelStream()
                        .filter(entry -> entry.getKey().contains(s))
                        .findFirst()
                        .orElse(ELSE_ENTRY).getValue())
                )
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        Metrics metrics = new Metrics();
        metrics.setRepository(repository.getName());
        metrics.setNames(names.stream()
                .collect(TreeMap<String, List<String>>::new,
                        (map, s) -> {
                            String key = "x" + xsMap.get(s) + "y" + ysMap.get(s);
                            if (!map.containsKey(key)) {
                                map.put(key, new ArrayList<>());
                            }
                            map.get(key).add(s);
                        },
                        (map1, map2) -> {}));
        metrics.setXs(xsMap.entrySet().stream()
                .sorted((x, y) -> x.getKey().compareTo(y.getKey()))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList()));
        metrics.setYs(ysMap.entrySet().stream()
                .sorted((x, y) -> x.getKey().compareTo(y.getKey()))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList()));

        return metrics;
    }

    protected RepositoryRepository getRepositoryRepository() {
        return repositoryRepository;
    }

    protected CommitRepository getCommitRepository() {
        return commitRepository;
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
