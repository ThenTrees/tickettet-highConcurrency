package com.tickettet.ddd.application.service.ticket.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.tickettet.ddd.application.model.cache.TicketDetailCache;
import com.tickettet.ddd.domain.ticket.model.entity.TicketDetail;
import com.tickettet.ddd.domain.ticket.service.TicketDetailDomainService;
import com.tickettet.ddd.infrastructure.cache.redis.RedisInfrasService;
import com.tickettet.ddd.infrastructure.distributed.redisson.RedisDistributedLocker;
import com.tickettet.ddd.infrastructure.distributed.redisson.RedisDistributedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketDetailCacheServiceRefactor {
    private final RedisDistributedService redisDistributedService;
    private final RedisInfrasService redisInfrasService;
    private final TicketDetailDomainService ticketDetailDomainService;

    // use gauva de cache local
    private final static Cache<Long, TicketDetailCache> ticketDetailLocalCache = CacheBuilder.newBuilder()
            .initialCapacity(10)
            .concurrencyLevel(12)
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public boolean orderTicketByUser(Long ticketId) {
        ticketDetailLocalCache.invalidate(ticketId);
        redisInfrasService.delete(genEventItemKey(ticketId));
        return  true;

    }

    /**
     * GET ticket item by id in cache
     */
    public TicketDetailCache getTicketDetail(Long ticketId, Long version) {
        // 1. Get data from local cache
        TicketDetailCache ticketDetailCache = getTicketDetailLocalCache(ticketId);

        if (ticketDetailCache != null) {
            // User: version, Cache: version
            // 1.version = null
            if (version == null) {
                log.info("01: GET TICKET FROM LOCAL CACHE: versionUser: {}, versionLocalCache: {}", version, ticketDetailCache.getVersion());
                return ticketDetailCache;
            }

            if (version.equals(ticketDetailCache.getVersion())) {
                log.info("02: GET TICKET FROM LOCAL CACHE: versionUser: {}, versionLocal: {}", version, ticketDetailCache.getTicketDetail());
                return ticketDetailCache;
            }

            if (version < ticketDetailCache.getVersion()) {
                log.info("03: GET TICKET FROM LOCAL CACHE: versionUser:{}, versionLocal: {}", version, ticketDetailCache.getVersion());
                return ticketDetailCache;
            }

            if (version > ticketDetailCache.getVersion()){
                return getTicketDetailDistributedCache(ticketId);
            }
        }
        return getTicketDetailDistributedCache(ticketId);
    }

    /**
     * GET ticket from distributed cache
     */
    public TicketDetailCache getTicketDetailDistributedCache(Long ticketId) {
        // 1 - GET DATA
        TicketDetailCache ticketDetailCache = redisInfrasService.getObject(genEventItemKey(ticketId), TicketDetailCache.class);
        if (ticketDetailCache == null) {
            // CALL DB
            log.info("GET TICKET FROM DISTRIBUTED LOCK");
            ticketDetailCache = getTicketDetailFromDatabase(ticketId);
        }
        // 2 - PUT DATA INTO LOCAL CACHE
        ticketDetailLocalCache.put(ticketId, ticketDetailCache);
        log.info("GET TICKET FROM DISTRIBUTED CACHE");
        return ticketDetailCache;
    }

    /**
     * get ticket from db
     */
    public TicketDetailCache getTicketDetailFromDatabase(Long ticketId) {
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock(genEventItemKeyLock(ticketId));
        try {
            // 1 - tao lock
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            // Lưu ý: dù có lấy lock thành công hay không vẫn phải unlock bằng mọi giá
            if (!isLock) {
                return null;
            }

            // GET CACHE
            TicketDetailCache ticketDetailCache = redisInfrasService.getObject(genEventItemKey(ticketId), TicketDetailCache.class);
            // 2. YES
            if (ticketDetailCache != null) {
                return ticketDetailCache;
            }

            TicketDetail ticketDetail = ticketDetailDomainService.getTicketDetailById(ticketId);
            ticketDetailCache = new TicketDetailCache().withClone(ticketDetail).withVersion(System.currentTimeMillis());
            // SET data to distributed cache
            redisInfrasService.setObject(genEventItemKey(ticketId), ticketDetailCache);
            return ticketDetailCache;
        } catch (Exception e) {
            throw  new RuntimeException(e);
        } finally {
            locker.unlock();
        }
    }

    /**
     * get ticket from local cache
     */
    public TicketDetailCache getTicketDetailLocalCache(Long ticketId) {
        // get cache from GUAVA
        // get cache from Caffeine
        return ticketDetailLocalCache.getIfPresent(ticketId);
    }

    private String genEventItemKey(Long ticketId) {
        return "PRO_TICKET:ITEM:" + ticketId;
    }

    private String genEventItemKeyLock(Long ticketId) {
        return "PRO_LOCK_KEY_ITEM" + ticketId;
    }
}
