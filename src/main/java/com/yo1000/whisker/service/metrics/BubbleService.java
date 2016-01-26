package com.yo1000.whisker.service.metrics;

import com.yo1000.whisker.model.Metrics;
import com.yo1000.whisker.model.Repository;
import com.yo1000.whisker.repository.CommitRepository;
import com.yo1000.whisker.repository.ComplexityRepository;
import com.yo1000.whisker.repository.DependencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by yoichi.kikuchi on 2016/01/15.
 */
@Service
public class BubbleService extends MetricsService {
    @Autowired
    public BubbleService(
            CommitRepository commitRepository,
            ComplexityRepository complexityRepository,
            DependencyRepository dependencyRepository,
            @Value("${application.metrics.git.comment-regex}") String gitCommentRegex,
            @Value("${application.metrics.default.extract-regex}") String defaultExtractRegex,
            @Value("#{T(java.util.Arrays).asList('${application.metrics.default.extensions}')}") List<String> defaultExtensions) {
        super(commitRepository, complexityRepository, dependencyRepository,
                gitCommentRegex, defaultExtractRegex, defaultExtensions);
    }

    @Override
    public Metrics find(Repository repository) throws IOException {
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
}
