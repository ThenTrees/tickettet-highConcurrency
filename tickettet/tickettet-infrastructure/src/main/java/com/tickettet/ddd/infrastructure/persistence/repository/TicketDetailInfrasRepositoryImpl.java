package com.tickettet.ddd.infrastructure.persistence.repository;

import com.tickettet.ddd.domain.ticket.model.entity.TicketDetail;
import com.tickettet.ddd.domain.ticket.repository.TicketRepository;
import com.tickettet.ddd.infrastructure.persistence.mapper.TicketDetailJPAMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TicketDetailInfrasRepositoryImpl implements TicketRepository {
    //Call JPA Mapper

    private final TicketDetailJPAMapper ticketDetailJPAMapper;

    @Override
    public Optional<TicketDetail> findById(Long id){
        log.info("Implement Infrastructure : {}", id);
        return ticketDetailJPAMapper.findById(id);
    }

    @Override
    public List<TicketDetail> findAll() {
        log.info("Fetching all ticket details");
        return ticketDetailJPAMapper.findAll();
    }
}
