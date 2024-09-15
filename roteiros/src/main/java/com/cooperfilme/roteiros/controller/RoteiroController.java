package com.cooperfilme.roteiros.controller;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import com.cooperfilme.roteiros.model.User;
import com.cooperfilme.roteiros.service.RoteiroService;
import com.cooperfilme.roteiros.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roteiros")
public class RoteiroController {

    private final RoteiroService roteiroService;

    public RoteiroController(RoteiroService roteiroService, UserService userService) {
        this.roteiroService = roteiroService;
    }

    @PostMapping("/submit")
    public ResponseEntity<Roteiro> submitRoteiro(@Valid @RequestBody Roteiro roteiro) {
        return ResponseEntity.ok(roteiroService.submitRoteiro(roteiro));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ANALISTA', 'REVISOR', 'APROVADOR')")
    public ResponseEntity<Roteiro> updateRoteiroStatus(
            @PathVariable Long id,
            @Valid @RequestBody RoteiroStatus newStatus,
            @RequestParam(required = false) String justification,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roteiroService.updateRoteiroStatus(id, newStatus, user, justification));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALISTA', 'REVISOR', 'APROVADOR')")
    public ResponseEntity<List<Roteiro>> listRoteiros(
            @RequestParam(required = false) RoteiroStatus status,
            @RequestParam(required = false) String clientEmail,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        return ResponseEntity.ok(
                roteiroService.getRoteiroByStatusAndDateRangeAndClientEmail(status, startDate, endDate, clientEmail));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ANALISTA', 'REVISOR', 'APROVADOR')")
    public ResponseEntity<Roteiro> getRoteiroById(@PathVariable Long id) {
        Optional<Roteiro> roteiro = roteiroService.getRoteiroById(id);
        return roteiro.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/vote")
    @PreAuthorize("hasRole('APROVADOR')")
    public ResponseEntity<Roteiro> voteOnRoteiro(
            @PathVariable Long id,
            @RequestParam boolean approved,
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(roteiroService.voteOnRoteiro(id, user, approved));
    }

}
