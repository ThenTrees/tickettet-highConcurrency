package com.tickettet.ddd.application.service.ticket.cache;

import com.google.common.cache.CacheBuilder;
import com.tickettet.ddd.domain.ticket.model.entity.TicketDetail;
import com.tickettet.ddd.domain.ticket.service.TicketDetailDomainService;
import com.tickettet.ddd.infrastructure.cache.redis.RedisInfrasService;
import com.tickettet.ddd.infrastructure.distributed.redisson.RedisDistributedLocker;
import com.tickettet.ddd.infrastructure.distributed.redisson.RedisDistributedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.google.common.cache.Cache;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketDetailCacheService {
    private final RedisDistributedService redisDistributedService;
    private final RedisInfrasService redisInfrasService;
    private final TicketDetailDomainService ticketDetailDomainService;
//     private static final Logger log = LoggerFactory.getLogger(TicketDetailCacheService.class);
    private final static Cache<Long, TicketDetail> ticketDetailLockCache = CacheBuilder.newBuilder()
        .initialCapacity(10)
        .concurrencyLevel(12)
        .expireAfterWrite(100, TimeUnit.MINUTES)
        .build();

    public TicketDetail getTicketDefaultCacheNormal(Long id, Long version) {
        // 1. Get ticket item by redis
        TicketDetail ticketDetail = redisInfrasService.getObject(genEventItemKey(id), TicketDetail.class);
        // 2. YES -> Hit Cache
        if (ticketDetail != null) {
            log.info("FROM CACHE {}, {}, {}", id, version, ticketDetail);
            return ticketDetail;
        }
        // 3. If NO -> Missing Cache

        // 4. Get data from DBS
        ticketDetail = ticketDetailDomainService.getTicketDetailById(id);
        log.info("FROM DBS {}, {}, {}", id, version, ticketDetail);

        // 5. Check ticketItem
        if (ticketDetail != null) {
            // Nói sau khi code xong: Code nay co van de -> Gia su ticketItem lay ra tu dbs null thi sao, query mãi

            // 6. SET CACHE
            redisInfrasService.setObject(genEventItemKey(id), ticketDetail);
        }
        return ticketDetail;
    }

    /**
     * 1. CACHE
     * 2. LOCK
     * 3. CACHE
     * 4. GET FROM DB
     * 5. SET CACHE
     */
    public TicketDetail getTicketDefaultCacheVip(Long id, Long version) {
        TicketDetail ticketDetail = redisInfrasService.getObject(genEventItemKey(id), TicketDetail.class);
        // show log: cache item
        log.info("CACHE {}, {}, {}", id, version, ticketDetail);
        // YES
        if (ticketDetail != null) {
            log.info("FROM CACHE EXIST {}",ticketDetail);
            return ticketDetail;
        }
        log.info("CACHE NO EXIST, START GET DB AND SET CACHE->, {}, {} ", id, version);
        // tao log process voi KEY
        RedisDistributedLocker locker = redisDistributedService.getDistributedLock("PRO_LOCK_ITEM"+id);
        try {
            // 1 - Tạo lock
            boolean isLock = locker.tryLock(1, 5, TimeUnit.SECONDS);
            // nếu quá 1s mà chưa lấy được khóa => return false;
            // leaseTime là tgian tự động giả phóng => nếu luồng đã lấy được khóa thành công thì sau 5s khóa sẽ tự động bị xóa kể cả chưa unlock()
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            if (!isLock) {
                log.info("LOCK WAIT ITEM PLEASE....{}", version);
                return ticketDetail;
            }
            // stub ...
            // GET CACHE
            ticketDetail = redisInfrasService.getObject(genEventItemKey(id), TicketDetail.class);
            // 2. YES
            if (ticketDetail != null) {
                log.info("FROM CACHE NGON A {}, {}, {}", id, version, ticketDetail);
                return ticketDetail;
            }
            // 3. Van khong co thi truy van db
            ticketDetail = ticketDetailDomainService.getTicketDetailById(id);
            log.info("FROM DBS ->>>> {}, {}", ticketDetail, version);
            if (ticketDetail == null) {
                log.info("TICKET NOT EXITS....{}", version);

                redisInfrasService.setObject(genEventItemKey(id), ticketDetail);
                return ticketDetail;
            }
            // neu co thi set redis
            redisInfrasService.setObject(genEventItemKey(id), ticketDetail); // TTL
            // set luon local
            return ticketDetail;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            // Lưu ý: Cho dù thành công hay không cũng phải unLock, bằng mọi giá.
            locker.unlock();
        }
    }

    private TicketDetail getTicketDetailLocalCache(Long id) {
        try {
            return ticketDetailLockCache.getIfPresent(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String genEventItemKey(Long itemId) {
        return "PRO_TICKET:ITEM:" + itemId;
    }
}
