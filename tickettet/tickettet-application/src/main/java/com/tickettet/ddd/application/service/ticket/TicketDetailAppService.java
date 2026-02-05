package com.tickettet.ddd.application.service.ticket;

import com.tickettet.ddd.application.model.TicketDetailDTO;

public interface TicketDetailAppService {
    TicketDetailDTO getTicketDetailById(Long ticketId, Long version);
    // order ticket
    boolean orderTicketByUser(Long ticketId);
}
