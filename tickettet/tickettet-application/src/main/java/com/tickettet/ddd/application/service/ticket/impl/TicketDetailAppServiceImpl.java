package com.tickettet.ddd.application.service.ticket.impl;

import com.tickettet.ddd.application.mapper.TicketMapper;
import com.tickettet.ddd.application.model.TicketDetailDTO;
import com.tickettet.ddd.application.model.cache.TicketDetailCache;
import com.tickettet.ddd.application.service.ticket.TicketDetailAppService;
import com.tickettet.ddd.application.service.ticket.cache.TicketDetailCacheService;
import com.tickettet.ddd.application.service.ticket.cache.TicketDetailCacheServiceRefactor;
import com.tickettet.ddd.domain.ticket.service.TicketDetailDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketDetailAppServiceImpl implements TicketDetailAppService {
    // CALL Service Domain Module
    private final TicketDetailDomainService ticketDetailDomainService;

    // CALL CACHE
    private final TicketDetailCacheService ticketDetailCacheService;

    // CALL CACHE SERVICE REFACTOR
    private final TicketDetailCacheServiceRefactor ticketDetailCacheServiceRefactor;

    @Override
    public TicketDetailDTO getTicketDetailById(Long ticketId, Long version) {
        log.info("Implement Application : {}, {}: ", ticketId, version);
        TicketDetailCache ticketDetailCache = ticketDetailCacheServiceRefactor.getTicketDetail(ticketId, version);
        // map top dto
        return TicketMapper.mapToTicketDetailDTO(ticketDetailCache.getTicketDetail());
    }

    @Override
    public boolean orderTicketByUser(Long ticketId) {
        return ticketDetailCacheServiceRefactor.orderTicketByUser(ticketId);
    }

}
