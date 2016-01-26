package com.yo1000.whisker.aspect;

import com.yo1000.whisker.model.Repository;
import com.yo1000.whisker.repository.CacheRepository;
import com.yo1000.whisker.repository.CommitRepository;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.file.Paths;

/**
 * Created by yoichi.kikuchi on 2016/01/26.
 */
@Aspect
@Component
public class MetricsCacheAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsCacheAdvice.class);

    private final CacheRepository cacheRepository;
    private final CommitRepository commitRepository;

    @Autowired
    public MetricsCacheAdvice(CacheRepository cacheRepository, CommitRepository commitRepository) {
        this.cacheRepository = cacheRepository;
        this.commitRepository = commitRepository;
    }

    @Around("execution(* com.yo1000.whisker.service.metrics.MetricsService+.find(..)) && args(repository)")
    public Object around(ProceedingJoinPoint joinPoint, Repository repository) throws Throwable {
        String key = new StringBuilder(joinPoint.getSignature().toString())
                .append(";").append(repository.getId())
                .append(";").append(this.getCommitRepository().selectLatestId(Paths.get(repository.getGit().getDirectory())))
                .toString();

        if (this.getCacheRepository().exists(key)) {
            LOGGER.debug("Cache hit. key=" + key);
            return this.getCacheRepository().select(key);
        }

        LOGGER.debug("Cache miss. key=" + key);
        Object o = joinPoint.proceed();
        this.getCacheRepository().insert(key, o);

        return o;
    }

    public CacheRepository getCacheRepository() {
        return cacheRepository;
    }

    public CommitRepository getCommitRepository() {
        return commitRepository;
    }
}
