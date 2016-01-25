package com.yo1000.whisker.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by yoichi.kikuchi on 2016/01/15.
 */
public class Metrics implements Serializable {
    private String repository;
    private List<Integer> xs;
    private List<Integer> ys;
    private Map<String, List<Z>> zs;

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
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

    public Map<String, List<Z>> getZs() {
        return zs;
    }

    public void setZs(Map<String, List<Z>> zs) {
        this.zs = zs;
    }

    public static class Z {
        private String name;
        private Integer value;

        public Z() {}

        public Z(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }
    }
}
