package com.yo1000.whisker.repository.jgit;

import com.yo1000.whisker.repository.CommitRepository;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

/**
 * Created by yoichi.kikuchi on 2016/01/17.
 */
@Repository
public class CommitJgitRepository implements CommitRepository {
    @Override
    public Map<String, Integer> selectDiffSummaryByLog(File gitDirectory) throws IOException {
        return this.selectDiffSummaryByLog(gitDirectory, ".*");
    }

    @Override
    public Map<String, Integer> selectDiffSummaryByLog(File gitDirectory, String commentFilterRegex) throws IOException {
        org.eclipse.jgit.lib.Repository repository = newRepository(gitDirectory);
        Git git = new Git(repository);

        DiffFormatter diffFormatter = new DiffFormatter(System.out);
        diffFormatter.setRepository(repository);

        try {
            return StreamSupport.stream(git.log().all().call().spliterator(), false)
                    .filter(revCommit -> revCommit.getParentCount() == 1 && revCommit.getFullMessage().matches(commentFilterRegex)) // ignore merge
                    .flatMap(revCommit -> {
                        try {
                            return diffFormatter.scan(revCommit.getTree(), revCommit.getParent(0).getTree()).parallelStream()
                                    .map(diffEntry -> {
                                        try {
                                            return new AbstractMap.SimpleEntry<>(diffEntry.getNewPath(),
                                                    diffFormatter.toFileHeader(diffEntry).toEditList().parallelStream()
                                                            .mapToInt(edit -> Math.abs(edit.getLengthA() - edit.getLengthB()))
                                                            .sum());
                                        } catch (IOException e) {
                                            throw new UncheckedIOException(e);
                                        }
                                    });
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    })
                    .collect(ConcurrentHashMap<String, Integer>::new, (map, entry) -> {
                        Integer value = map.get(entry.getKey());
                        if (value == null) {
                            value = 0;
                        }
                        map.put(entry.getKey(), value + entry.getValue());
                    }, (map1, map2) -> {});
        } catch (GitAPIException e) {
            throw new IllegalStateException(e);
        }
    }

    protected static org.eclipse.jgit.lib.Repository newRepository(File gitDirectory) throws IOException {
        return new FileRepositoryBuilder()
                .setGitDir(gitDirectory)
                .readEnvironment()
                .findGitDir()
                .build();
    }

}
