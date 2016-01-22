package com.yo1000.whisker.repository.file;

import com.yo1000.whisker.repository.ComplexityRepository;
import com.yo1000.winchester.analyzer.CyclomaticComplexity;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by yoichi.kikuchi on 2016/01/22.
 */
@Repository
public class ComplexityFileRepository implements ComplexityRepository {
    private CyclomaticComplexity cyclomaticComplexity;

    public ComplexityFileRepository() {
        this.cyclomaticComplexity = new CyclomaticComplexity();
    }

    @Override
    public Map<String, Integer> select(Path directory, List<String> extensions) throws IOException {
        return this.getCyclomaticComplexity().analyze(directory, extensions);
    }

    public CyclomaticComplexity getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }
}
