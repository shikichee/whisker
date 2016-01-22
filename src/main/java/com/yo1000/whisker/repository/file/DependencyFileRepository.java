package com.yo1000.whisker.repository.file;

import com.yo1000.whisker.repository.DependencyRepository;
import com.yo1000.winchester.analyzer.ReferencedDependency;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by yoichi.kikuchi on 2016/01/22.
 */
@Repository
public class DependencyFileRepository implements DependencyRepository {
    private ReferencedDependency referencedDependency;

    public DependencyFileRepository() {
        this.referencedDependency = new ReferencedDependency();
    }

    @Override
    public Map<String, Integer> select(Path directory, List<String> extensions) throws IOException {
        return this.getReferencedDependency().analyze(directory, extensions);
    }

    public ReferencedDependency getReferencedDependency() {
        return referencedDependency;
    }
}
