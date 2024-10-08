package com.cooperfilme.roteiros.controller;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import com.cooperfilme.roteiros.model.User;
import com.cooperfilme.roteiros.model.UserRole;
import com.cooperfilme.roteiros.service.RoteiroService;
import com.cooperfilme.roteiros.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/roteiros")
public class RoteiroController {

    private static final Logger logger = LoggerFactory.getLogger(RoteiroController.class);
    private final RoteiroService roteiroService;

    public RoteiroController(RoteiroService roteiroService, UserService userService) {
        this.roteiroService = roteiroService;
    }

    @PostMapping("/submit")
    public ResponseEntity<Roteiro> submitRoteiro(@Valid @RequestBody Roteiro roteiro) {
        return ResponseEntity.ok(roteiroService.submitRoteiro(roteiro));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ANALISTA', 'REVISOR', 'APROVADOR')")
    public ResponseEntity<Roteiro> updateRoteiroStatus(
            @PathVariable Long id,
            @Valid @RequestBody RoteiroStatus newStatus,
            @RequestParam(required = false) String justification,
            @RequestParam(required = false) String reviewerComments,
            @AuthenticationPrincipal User user) {
        Roteiro updatedRoteiro = roteiroService.updateRoteiroStatus(id, newStatus, user, justification);
        
        if (user.getRole() == UserRole.REVISOR && newStatus == RoteiroStatus.AGUARDANDO_APROVACAO && reviewerComments != null) {
            updatedRoteiro = roteiroService.updateRoteiroWithReviewerComments(id, newStatus, reviewerComments, user);
        } else {
            updatedRoteiro = roteiroService.updateRoteiroStatus(id, newStatus, user, justification);
        }
        return ResponseEntity.ok(updatedRoteiro);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ANALISTA', 'REVISOR', 'APROVADOR')")
    public ResponseEntity<?> listRoteiros(
            @RequestParam(required = false) RoteiroStatus status,
            @RequestParam(required = false) String clientEmail,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start = null;
        LocalDateTime end = null;

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        try {
            if (startDate != null && !startDate.isEmpty()) {
                start = LocalDateTime.parse(startDate, formatter);
            }

            if (endDate != null && !endDate.isEmpty()) {
                end = LocalDateTime.parse(endDate, formatter);
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid date format. Please use ISO_DATE_TIME format (e.g., '2024-03-21T12:00:00')");
        }

        List<Roteiro> roteiros = roteiroService.getRoteiroByStatusAndDateRangeAndClientEmail(status, start, end,
                clientEmail);
        return ResponseEntity.ok(roteiros);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ANALISTA', 'REVISOR', 'APROVADOR')")
    public ResponseEntity<Roteiro> getRoteiroById(@PathVariable Long id) {
        Optional<Roteiro> roteiro = roteiroService.getRoteiroById(id);
        return roteiro.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/vote")
    @PreAuthorize("hasAuthority('APROVADOR')")
    public ResponseEntity<Roteiro> voteOnRoteiro(
            @PathVariable Long id,
            @RequestParam boolean approved,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roteiroService.voteOnRoteiro(id, user, approved));
    }

    @GetMapping("/status/{id}")
    public ResponseEntity<?> getRoteiroStatus(@PathVariable Long id) {
        Optional<Roteiro> roteiro = roteiroService.getRoteiroById(id);
        if (roteiro.isPresent()) {
            return ResponseEntity.ok(Map.of("status", roteiro.get().getStatus()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/reviewer-comments")
    @PreAuthorize("hasAuthority('REVISOR')")
    public ResponseEntity<Roteiro> addReviewerComments(
            @PathVariable Long id,
            @Valid @RequestBody RoteiroStatus newStatus,
            @RequestBody String reviewerComments,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roteiroService.updateRoteiroWithReviewerComments(id, newStatus, reviewerComments, user));
    }

}
