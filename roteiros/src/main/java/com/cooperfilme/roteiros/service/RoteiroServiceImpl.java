package com.cooperfilme.roteiros.service;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import com.cooperfilme.roteiros.model.User;
import com.cooperfilme.roteiros.model.UserRole;
import com.cooperfilme.roteiros.repository.UserRepository;
import com.cooperfilme.roteiros.model.Vote;
import com.cooperfilme.roteiros.repository.RoteiroRepository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;

@Service
public class RoteiroServiceImpl implements RoteiroService {

    private final RoteiroRepository roteiroRepository;
    private final UserRepository userRepository;

    public RoteiroServiceImpl(RoteiroRepository roteiroRepository, UserRepository userRepository) {
        this.roteiroRepository = roteiroRepository;
        this.userRepository = userRepository;
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

        if (roteiro.getStatus() == newStatus) {
            return roteiro;
        }

        if (user.getId() == null) {
            user = userRepository.findByEmail(user.getEmail())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        } else {
            user = userRepository.findById(user.getId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
        }

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
    public List<Roteiro> getRoteiroByStatusAndDateRangeAndClientEmail(RoteiroStatus status, LocalDateTime start,
            LocalDateTime end, String clientEmail) {
        if (status == null && start == null && end == null && (clientEmail == null || clientEmail.isEmpty())) {
            return roteiroRepository.findAll();
        }

        Specification<Roteiro> spec = Specification.where(null);

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        if (start != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), start));
        }

        if (end != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt"), end));
        }

        if (clientEmail != null && !clientEmail.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("clientEmail"), clientEmail));
        }

        return roteiroRepository.findAll(spec);
    }

    @Override
    public Optional<Roteiro> getRoteiroById(Long id) {
        return roteiroRepository.findById(id);
    }

    @Override
    public Roteiro voteOnRoteiro(Long roteiroId, User user, boolean approved) {
        Roteiro roteiro = roteiroRepository.findById(roteiroId)
                .orElseThrow(() -> new EntityNotFoundException("Roteiro não encontrado"));

        if (roteiro.getStatus() != RoteiroStatus.AGUARDANDO_APROVACAO
                && roteiro.getStatus() != RoteiroStatus.EM_APROVACAO) {
            throw new IllegalStateException(
                    "A votação só é permitida para roteiros nos estados AGUARDANDO_APROVACAO ou EM_APROVACAO");
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

    @Override
    public Roteiro updateRoteiroWithReviewerComments(Long roteiroId, RoteiroStatus newStatus, String reviewerComments,
            User user) {
        Roteiro roteiro = roteiroRepository.findById(roteiroId)
                .orElseThrow(() -> new EntityNotFoundException("Roteiro não encontrado"));

        if (user.getRole() != UserRole.REVISOR) {
            throw new IllegalStateException("Apenas revisores podem adicionar comentários a roteiros em revisão");
        }

        if (roteiro.getStatus() != RoteiroStatus.EM_REVISAO && newStatus != RoteiroStatus.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException("Comentários só podem ser adicionados a roteiros em revisão ou ao enviar para aprovação");
        }

        roteiro.setReviewerComments(reviewerComments);
        roteiro.setStatus(newStatus);
        roteiro.setAssignedTo(null); // Clear the assigned user as it's moving to a new stage
        return roteiroRepository.save(roteiro);
    }
}
