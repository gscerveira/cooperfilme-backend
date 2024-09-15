package com.cooperfilme.roteiros.controller;

import com.cooperfilme.roteiros.dto.RoteiroSubmissionRequest;
import com.cooperfilme.roteiros.dto.RoteiroStatusUpdateRequest;
import com.cooperfilme.roteiros.dto.RoteiroResponse;
import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.User;
import com.cooperfilme.roteiros.service.RoteiroService;
import com.cooperfilme.roteiros.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/roteiros")
public class RoteiroController {

    private final RoteiroService roteiroService;
    private final UserService userService;

    public RoteiroController(RoteiroService roteiroService, UserService userService) {
        this.roteiroService = roteiroService;
        this.userService = userService;
    }

    @PostMapping("/submit")
    public ResponseEntity<RoteiroResponse> submitRoteiro(@Valid @RequestBody RoteiroSubmissionRequest request) {
        Roteiro roteiro = roteiroService.submitRoteiro(request.toRoteiro());
        return ResponseEntity.ok(new RoteiroResponse.fromRoteiro(roteiro));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ANALISTA', 'REVISOR', 'APROVADOR')")
    public ResponseEntity<RoteiroResponse> updateRoteiroStatus(
            @PathVariable Long id,
            @Valid @RequestBody RoteiroStatusUpdateRequest request,
            @AuthenticationPrincipal User user) {
        Roteiro updatedRoteiro = roteiroService.updateRoteiroStatus(id, request.getNewStatus(), user, request.getJustification());
        return ResponseEntity.ok(RoteiroResponse.fromRoteiro(updatedRoteiro));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ANALISTA', 'REVISOR', 'APROVADOR')")
    public ResponseEntity<List<RoteiroResponse>> listRoteiros(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String clientEmail,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        List<Roteiro> roteiros = roteiroService.listRoteiros(status, clientEmail, startDate, endDate);
        List<RoteiroResponse> response = roteiros.stream()
                .map(RoteiroResponse::fromRoteiro)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/vote")
    @PreAuthorize("hasRole('APROVADOR')")
    public ResponseEntity<RoteiroResponse> voteOnRoteiro(
            @PathVariable Long id,
            @RequestParam boolean approved,
            @AuthenticationPrincipal User user) {
        Roteiro votedRoteiro = roteiroService.voteOnRoteiro(id, user, approved);
        return ResponseEntity.ok(RoteiroResponse.fromRoteiro(votedRoteiro));
    }

}
