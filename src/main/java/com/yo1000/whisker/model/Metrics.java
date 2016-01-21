package com.yo1000.whisker.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by yoichi.kikuchi on 2016/01/15.
 */
public class Metrics implements Serializable {
    private String repository;
    private Map<String, List<String>> names;
    private List<Integer> xs;
    private List<Integer> ys;

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public Map<String, List<String>> getNames() {
        return names;
    }

    public void setNames(Map<String, List<String>> names) {
        this.names = names;
    }

    public List<Integer> getXs() {
        return xs;
    }

    public void setXs(List<Integer> xs) {
        this.xs = xs;
    }

    public List<Integer> getYs() {
        return ys;
    }

    public void setYs(List<Integer> ys) {
        this.ys = ys;
    }
}
