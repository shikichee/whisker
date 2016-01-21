package com.yo1000.whisker.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Created by yoichi.kikuchi on 2016/01/18.
 */
public class Environment implements Serializable {
    @NotNull
    String directory;
    String extractRegex;

    public Environment() {}

    public Environment(String directory) {
        this.directory = directory;
    }

    public Environment(String directory, String extractRegex) {
        this.directory = directory;
        this.extractRegex = extractRegex;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public String getExtractRegex() {
        return extractRegex;
    }

    public void setExtractRegex(String extractRegex) {
        this.extractRegex = extractRegex;
    }
}
