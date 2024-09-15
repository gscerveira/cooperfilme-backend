package com.cooperfilme.roteiros.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import com.cooperfilme.roteiros.state.RoteiroState;
import com.cooperfilme.roteiros.state.RoteiroStateFactory;

public class Roteiro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoteiroStatus status;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private String clientEmail;

    @Column(nullable = false)
    private String clientPhone;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "roteiro_id")
    private Set<Vote> votes;


    // Construtores

    public Roteiro() {
        this.createdAt = LocalDateTime.now();
        this.status = RoteiroStatus.AGUARDANDO_ANALISE;
    }

    public Roteiro(String content, String clientName, String clientEmail, String clientPhone) {
        this();
        this.content = content;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.clientPhone = clientPhone;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public RoteiroStatus getStatus() {
        return status;
    }

    public void setStatus(RoteiroStatus status) {
        this.status = status;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    public void transitionTo(RoteiroStatus newStatus, User user, String justification) {
        RoteiroState currentState = RoteiroStateFactory.getState(this.status);
        currentState.transition(this, newStatus, user, justification);
    }


}
