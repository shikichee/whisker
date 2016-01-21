package com.yo1000.whisker.controller.page;

import com.yo1000.whisker.service.RepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by yoichi.kikuchi on 2016/01/18.
 */
@Controller
@RequestMapping("repository")
public class RepositoryPage {
    private RepositoryService repositoryService;

    @Autowired
    public RepositoryPage(RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model,
                        @Value("${application.metrics.default.extract-regex}") String defaultRegex,
                        @Value("${application.metrics.default.extensions}") String defaultExtensions) {
        model.addAttribute("title", "Repository");
        model.addAttribute("defaultRegex", defaultRegex);
        model.addAttribute("defaultExtensions", defaultExtensions);
        model.addAttribute("repositories", this.getRepositoryService().find());
        return "repository";
    }

    protected RepositoryService getRepositoryService() {
        return repositoryService;
    }
}
