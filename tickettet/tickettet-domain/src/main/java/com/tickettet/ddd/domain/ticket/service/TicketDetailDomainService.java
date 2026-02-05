package com.tickettet.ddd.domain.ticket.service;

import com.tickettet.ddd.domain.ticket.model.entity.TicketDetail;

import java.util.List;

public interface TicketDetailDomainService {
    TicketDetail getTicketDetailById(Long ticketId);

    /**
     * Get all ticket details
     */
    List<TicketDetail> getAllTicketDetails();
}
