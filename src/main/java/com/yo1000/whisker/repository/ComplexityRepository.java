package com.yo1000.whisker.repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Created by yoichi.kikuchi on 2016/01/22.
 */
public interface ComplexityRepository {
    Map<String, Integer> select(Path directory, List<String> extensions) throws IOException;
}
