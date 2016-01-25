package com.yo1000.whisker.service;

import com.yo1000.whisker.model.Metrics;
import com.yo1000.whisker.model.Repository;
import com.yo1000.whisker.repository.CommitRepository;
import com.yo1000.whisker.repository.ComplexityRepository;
import com.yo1000.whisker.repository.DependencyRepository;
import com.yo1000.whisker.repository.RepositoryRepository;
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
    private final ComplexityRepository complexityRepository;
    private final DependencyRepository dependencyRepository;
    private final String gitCommentRegex;
    private final String defaultExtractRegex;
    private final List<String> defaultExtensions;

    @Autowired
    public MetricsService(
            RepositoryRepository repositoryRepository,
            CommitRepository commitRepository,
            ComplexityRepository complexityRepository,
            DependencyRepository dependencyRepository,
            @Value("${application.metrics.git.comment-regex}") String gitCommentRegex,
            @Value("${application.metrics.default.extract-regex}") String defaultExtractRegex,
            @Value("#{T(java.util.Arrays).asList('${application.metrics.default.extensions}')}") List<String> defaultExtensions) {
        this.repositoryRepository = repositoryRepository;
        this.commitRepository = commitRepository;
        this.complexityRepository = complexityRepository;
        this.dependencyRepository = dependencyRepository;
        this.gitCommentRegex = gitCommentRegex;
        this.defaultExtractRegex = defaultExtractRegex;
        this.defaultExtensions = defaultExtensions;
    }

    public List<Metrics> findScatter() throws IOException {
        return this.getRepositoryRepository().select().parallelStream().map(repository -> {
            try {
                return this.findScatter(repository);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).collect(Collectors.toList());
    }

    public List<Metrics> findScatter(String name) throws IOException {
        return this.getRepositoryRepository().select(name).parallelStream().map(repository -> {
            try {
                return this.findScatter(repository);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).collect(Collectors.toList());
    }

    public List<Metrics> findBubble() throws IOException {
        return this.getRepositoryRepository().select().parallelStream().map(repository -> {
            try {
                return this.findBubble(repository);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).collect(Collectors.toList());
    }

    public List<Metrics> findBubble(String name) throws IOException {
        return this.getRepositoryRepository().select(name).parallelStream().map(repository -> {
            try {
                return this.findBubble(repository);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).collect(Collectors.toList());
    }

    protected Metrics findScatter(Repository repository) throws IOException {
        final Map<String, Integer> fixDiffsMap = this.getCommitRepository().selectDiffSummaryByLog(
                Paths.get(repository.getGit().getDirectory()), this.getGitCommentRegex());

        final Map<String, Integer> complexityMap = this.getComplexityRepository().select(
                Paths.get(repository.getSource().getDirectory()),
                repository.getExtensions() != null && !repository.getExtensions().isEmpty()
                        ? repository.getExtensions() : this.getDefaultExtensions());

        final Map<String, Integer> dependencyMap = this.getDependencyRepository().select(
                Paths.get(repository.getClassFile().getDirectory()), Arrays.asList(".class"));

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

        List<String> names = this.extractNames(fixDiffsMap.keySet(), gitFileExtractPattern);
        names.addAll(this.extractNames(complexityMap.keySet(), complexityFileExtractPattern));
        names.addAll(this.extractNames(dependencyMap.keySet(), dependencyFileExtractPattern));

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

        Map<String, Integer> zsMap = names.parallelStream()
                .map(s -> new AbstractMap.SimpleEntry<>(s, dependencyMap.entrySet().parallelStream()
                        .filter(entry -> entry.getKey().contains(s))
                        .findFirst()
                        .orElse(ELSE_ENTRY).getValue())
                )
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        Metrics metrics = new Metrics();
        metrics.setRepository(repository.getName());
        metrics.setXs(xsMap.entrySet().stream()
                .sorted((x, y) -> x.getKey().compareTo(y.getKey()))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList()));
        metrics.setYs(ysMap.entrySet().stream()
                .sorted((x, y) -> x.getKey().compareTo(y.getKey()))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList()));
        metrics.setZs(this.extractZs(names, xsMap, ysMap, zsMap));

        return metrics;
    }

    protected Metrics findBubble(Repository repository) throws IOException {
        final Map<String, Integer> fixDiffsMap = this.getCommitRepository().selectDiffSummaryByLog(
                Paths.get(repository.getGit().getDirectory()), this.getGitCommentRegex());

        final Map<String, Integer> complexityMap = this.getComplexityRepository().select(
                Paths.get(repository.getSource().getDirectory()),
                repository.getExtensions() != null && !repository.getExtensions().isEmpty()
                        ? repository.getExtensions() : this.getDefaultExtensions());

        final Map<String, Integer> dependencyMap = this.getDependencyRepository().select(
                Paths.get(repository.getClassFile().getDirectory()), Arrays.asList(".class"));

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

        List<String> names = this.extractNames(fixDiffsMap.keySet(), gitFileExtractPattern);
        names.addAll(this.extractNames(complexityMap.keySet(), complexityFileExtractPattern));
        names.addAll(this.extractNames(dependencyMap.keySet(), dependencyFileExtractPattern));

        names = names.parallelStream().parallel()
                .distinct()
                .filter(s -> s.matches(repository.getFilter()))
                .sorted()
                .collect(Collectors.toList());

        Map<String, Integer> xsMap = names.parallelStream()
                .map(s -> new AbstractMap.SimpleEntry<>(s, complexityMap.entrySet().parallelStream()
                        .filter(entry -> entry.getKey().contains(s))
                        .findFirst()
                        .orElse(ELSE_ENTRY).getValue())
                )
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        Map<String, Integer> ysMap = names.parallelStream()
                .map(s -> new AbstractMap.SimpleEntry<>(s, fixDiffsMap.entrySet().parallelStream()
                        .filter(entry -> entry.getKey().contains(s))
                        .findFirst()
                        .orElse(ELSE_ENTRY).getValue())
                )
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        Map<String, Integer> zsMap = names.parallelStream()
                .map(s -> new AbstractMap.SimpleEntry<>(s, dependencyMap.entrySet().parallelStream()
                        .filter(entry -> entry.getKey().contains(s))
                        .findFirst()
                        .orElse(ELSE_ENTRY).getValue())
                )
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        Metrics metrics = new Metrics();
        metrics.setRepository(repository.getName());
        metrics.setXs(xsMap.entrySet().stream()
                .sorted((x, y) -> x.getKey().compareTo(y.getKey()))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList()));
        metrics.setYs(ysMap.entrySet().stream()
                .sorted((x, y) -> x.getKey().compareTo(y.getKey()))
                .map(entry -> entry.getValue())
                .collect(Collectors.toList()));
        metrics.setZs(this.extractZs(names, xsMap, ysMap, zsMap));

        return metrics;
    }

    protected Map<String, List<Metrics.Z>> extractZs(List<String> names,
            Map<String, Integer> xsMap, Map<String, Integer> ysMap, Map<String, Integer> zsMap) {
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

    protected RepositoryRepository getRepositoryRepository() {
        return repositoryRepository;
    }

    protected CommitRepository getCommitRepository() {
        return commitRepository;
    }

    public ComplexityRepository getComplexityRepository() {
        return complexityRepository;
    }

    public DependencyRepository getDependencyRepository() {
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
