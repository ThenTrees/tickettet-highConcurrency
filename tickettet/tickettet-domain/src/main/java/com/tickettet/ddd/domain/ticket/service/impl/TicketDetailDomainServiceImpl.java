package com.tickettet.ddd.domain.ticket.service.impl;

import com.tickettet.ddd.domain.ticket.model.entity.TicketDetail;
import com.tickettet.ddd.domain.ticket.repository.TicketRepository;
import com.tickettet.ddd.domain.ticket.service.TicketDetailDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketDetailDomainServiceImpl implements TicketDetailDomainService {

    private final TicketRepository ticketRepository;

    @Override
    public TicketDetail getTicketDetailById(Long ticketId) {
        log.info("Implement Domain: {}", ticketId);
        return ticketRepository.findById(ticketId).orElse(null);
    }

    @Override
    public List<TicketDetail> getAllTicketDetails() {
        log.info("Fetching all ticket details from domain service");
        return ticketRepository.findAll();
    }
}
