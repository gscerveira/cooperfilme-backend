package com.cooperfilme.roteiros.service;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import com.cooperfilme.roteiros.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
    

public interface RoteiroService {
    Roteiro submitRoteiro(Roteiro roteiro);
    Roteiro updateRoteiroStatus(Long roteiroId, RoteiroStatus newStatus, User user, String justification);
    List<Roteiro> getRoteiroByStatus(RoteiroStatus status);
    List<Roteiro> getRoteiroByClientEmail(String clientEmail);
    List<Roteiro> getRoteiroByDateRange(LocalDateTime start, LocalDateTime end);
    List<Roteiro> getRoteiroByStatusAndDateRangeAndClientEmail(RoteiroStatus status, String start, String end, String clientEmail);
    Optional<Roteiro> getRoteiroById(Long id);
    Roteiro voteOnRoteiro(Long roteiroId, User user, boolean approved);
}
