package com.tickettet.ddd.infrastructure.distributed.redisson.impl;

import com.tickettet.ddd.infrastructure.distributed.redisson.RedisDistributedLocker;
import com.tickettet.ddd.infrastructure.distributed.redisson.RedisDistributedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RedisDistributedLockerImpl implements RedisDistributedService {

    private final RedissonClient redissonClient;

    @Override
    public RedisDistributedLocker getDistributedLock(String lockKey) {
        RLock rLock= redissonClient.getLock(lockKey);
        return new RedisDistributedLocker() {
            @Override
            public boolean tryLock(long waitTime, long leaseTime, TimeUnit unit) throws InterruptedException {
                boolean isLockSuccess = rLock.tryLock(waitTime, leaseTime, unit);
                log.info("{} get lock result: {}", lockKey, isLockSuccess);
                return isLockSuccess;
            }

            @Override
            public void lock(long time, TimeUnit unit) {
                rLock.lock(time, unit);
            }

            @Override
            public void unlock() {
                if (isLocked() && isHeldByCurrentThread()) {
                    rLock.unlock();
                }
            }

            @Override
            public boolean isLocked() {
                return rLock.isLocked();
            }

            @Override
            public boolean isHeldByThread(long threadId) {
                return rLock.isHeldByThread(threadId);
            }

            @Override
            public boolean isHeldByCurrentThread() {
                return rLock.isHeldByCurrentThread();
            }
        };
    }
}
