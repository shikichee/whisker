package com.yo1000.whisker.repository;

import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by yoichi.kikuchi on 2016/01/19.
 */
public interface CommitRepository {
    Map<String, Integer> selectDiffSummaryByLog(File gitDirectory) throws IOException;
    Map<String, Integer> selectDiffSummaryByLog(File gitDirectory, String commentFilterRegex) throws IOException;
}
