package com.cooperfilme.roteiros.service;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import com.cooperfilme.roteiros.model.User;
import com.cooperfilme.roteiros.model.UserRole;
import com.cooperfilme.roteiros.model.Vote;
import com.cooperfilme.roteiros.repository.RoteiroRepository;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;

@Service
public class RoteiroServiceImpl implements RoteiroService {

    private final RoteiroRepository roteiroRepository;

    public RoteiroServiceImpl(RoteiroRepository roteiroRepository) {
        this.roteiroRepository = roteiroRepository;
    }

    @Override
    public Roteiro submitRoteiro(Roteiro roteiro) {
        roteiro.setStatus(RoteiroStatus.AGUARDANDO_ANALISE);
        return roteiroRepository.save(roteiro);
    }

    @Override
    public Roteiro updateRoteiroStatus(Long roteiroId, RoteiroStatus newStatus, User user, String justification) {
        Roteiro roteiro = roteiroRepository.findById(roteiroId)
                .orElseThrow(() -> new EntityNotFoundException("Roteiro não encontrado"));

        roteiro.transitionTo(newStatus, user, justification);
        return roteiroRepository.save(roteiro);
    }

    @Override
    public List<Roteiro> getRoteiroByStatus(RoteiroStatus status) {
        return roteiroRepository.findByStatus(status);
    }

    @Override
    public List<Roteiro> getRoteiroByClientEmail(String clientEmail) {
        return roteiroRepository.findByClientEmail(clientEmail);
    }

    @Override
    public List<Roteiro> getRoteiroByDateRange(LocalDateTime start, LocalDateTime end) {
        return roteiroRepository.findByCreatedAtBetween(start, end);
    }

    @Override
    public List<Roteiro> getRoteiroByStatusAndDateRangeAndClientEmail(RoteiroStatus status, String start, String end, String clientEmail) {
        return roteiroRepository.findByStatusAndCreatedAtBetweenAndClientEmail(status, start, end, clientEmail);
    }

    @Override
    public Optional<Roteiro> getRoteiroById(Long id) {
        return roteiroRepository.findById(id);
    }

    @Override
    public Roteiro voteOnRoteiro(Long roteiroId, User user, boolean approved) {
        Roteiro roteiro = roteiroRepository.findById(roteiroId)
                .orElseThrow(() -> new EntityNotFoundException("Roteiro não encontrado"));

        if (roteiro.getStatus() != RoteiroStatus.AGUARDANDO_APROVACAO && roteiro.getStatus() != RoteiroStatus.EM_APROVACAO) {
            throw new IllegalStateException("A votação só é permitida para roteiros nos estados AGUARDANDO_APROVACAO ou EM_APROVACAO");
        }

        if (user.getRole() != UserRole.APROVADOR) {
            throw new IllegalStateException("Apenas usuários com cargo de APROVADOR podem votar em roteiros");
        }

        if (roteiro.getVotes() == null) {
            roteiro.setVotes(new HashSet<>());
        }

        roteiro.getVotes().removeIf(vote -> vote.getUser().equals(user));
        roteiro.getVotes().add(new Vote(user, approved));

        if (!approved) {
            roteiro.setStatus(RoteiroStatus.RECUSADO);
            return roteiroRepository.save(roteiro);
        }

        if (roteiro.getVotes().size() == 3) {
            long approvedVotes = roteiro.getVotes().stream().filter(Vote::isApproved).count();
            if (approvedVotes == 3) {
                roteiro.setStatus(RoteiroStatus.APROVADO);
            } else {
                roteiro.setStatus(RoteiroStatus.RECUSADO);
            }
        }

        return roteiroRepository.save(roteiro);
    }
}
