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
        String reposKey = new StringBuilder(joinPoint.getSignature().toString())
                .append(";").append(repository.getId())
                .toString();

        Object cachedCommitId = this.getCacheRepository().select(reposKey);
        String latestCommitId = this.getCommitRepository().selectLatestId(Paths.get(repository.getGit().getDirectory()));

        String cacheKey = new StringBuilder(reposKey)
                .append(";").append(latestCommitId)
                .toString();

        if (this.getCacheRepository().exists(cacheKey) && latestCommitId.equals(cachedCommitId)) {
            LOGGER.debug("Cache hit. key=" + cacheKey);
            return this.getCacheRepository().select(cacheKey);
        }

        LOGGER.debug("Cache miss. key=" + cacheKey);

        String deleteCacheKey = new StringBuilder(reposKey).append(";").append(cachedCommitId).toString();

        if (this.getCacheRepository().exists(deleteCacheKey)) {
            this.getCacheRepository().delete(reposKey);
            this.getCacheRepository().delete(deleteCacheKey);
        }

        Object o = joinPoint.proceed();
        this.getCacheRepository().insert(reposKey, latestCommitId);
        this.getCacheRepository().insert(cacheKey, o);

        return o;
    }

    public CacheRepository getCacheRepository() {
        return cacheRepository;
    }

    public CommitRepository getCommitRepository() {
        return commitRepository;
    }
}
