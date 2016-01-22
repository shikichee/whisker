package com.yo1000.whisker.repository;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

/**
 * Created by yoichi.kikuchi on 2016/01/19.
 */
public interface CommitRepository {
    Map<String, Integer> selectDiffSummaryByLog(Path gitDirectory) throws IOException;
    Map<String, Integer> selectDiffSummaryByLog(Path gitDirectory, String commentFilterRegex) throws IOException;
}
