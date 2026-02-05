package com.tickettet.ddd.application.mapper;

import com.tickettet.ddd.application.model.TicketDetailDTO;
import com.tickettet.ddd.domain.ticket.model.entity.TicketDetail;
import org.springframework.beans.BeanUtils;

public class TicketMapper {
     public static TicketDetailDTO mapToTicketDetailDTO(TicketDetail ticketDetail) {
         if (ticketDetail == null) return null;
         TicketDetailDTO ticketDetailDTO = new TicketDetailDTO();
         BeanUtils.copyProperties(ticketDetail, ticketDetailDTO);
         return ticketDetailDTO;
     }
}
