package com.yo1000.whisker.model;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by yoichi.kikuchi on 2016/01/17.
 */
public class Repository implements Serializable {
    private String id;
    @NotNull
    private String name;
    @NotNull
    private Environment git;
    @NotNull
    private Environment source;
    @NotNull
    private Environment classFile;
    private List<String> extensions;
    private String filter;
    private String modifier;
    private Date modified;

    public Repository() {}

    public Repository(String id, String name, Environment git, Environment source, Environment classFile,
            List<String> extensions, String filter, String modifier, Date modified) {
        this.id = id;
        this.name = name;
        this.git = git;
        this.source = source;
        this.classFile = classFile;
        this.extensions = extensions;
        this.filter = filter;
        this.modifier = modifier;
        this.modified = modified;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Environment getGit() {
        return git;
    }

    public void setGit(Environment git) {
        this.git = git;
    }

    public Environment getClassFile() {
        return classFile;
    }

    public void setClassFile(Environment classFile) {
        this.classFile = classFile;
    }

    public Environment getSource() {
        return source;
    }

    public void setSource(Environment source) {
        this.source = source;
    }

    public List<String> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }
}
