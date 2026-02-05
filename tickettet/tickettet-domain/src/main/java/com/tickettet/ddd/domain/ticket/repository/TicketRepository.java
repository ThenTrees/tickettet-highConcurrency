package com.tickettet.ddd.domain.ticket.repository;

import com.tickettet.ddd.domain.ticket.model.entity.TicketDetail;

import java.util.List;
import java.util.Optional;

public interface TicketRepository {
    Optional<TicketDetail> findById(Long id);

    /**
     * Find all ticket details
     * @return List of all ticket details
     */
    List<TicketDetail> findAll();
}
