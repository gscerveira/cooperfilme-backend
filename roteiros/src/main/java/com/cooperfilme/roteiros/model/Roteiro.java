package com.cooperfilme.roteiros.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.Set;

import com.cooperfilme.roteiros.state.RoteiroState;
import com.cooperfilme.roteiros.state.RoteiroStateFactory;

@Entity
public class Roteiro {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O conteúdo do roteiro não pode estar vazio")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoteiroStatus status;

    @NotBlank(message = "O nome do cliente é obrigatório")
    @Column(nullable = false)
    private String clientName;

    @NotBlank(message = "O email do cliente é obrigatório")
    @Email(message = "Por favor, forneça um endereço de email válido")
    @Column(nullable = false)
    private String clientEmail;

    @NotBlank(message = "O telefone do cliente é obrigatório")
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

    @Column(columnDefinition = "TEXT")
    private String reviewerComments;


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

    public LocalDateTime setCreatedAt(LocalDateTime createdAt) {
        return this.createdAt = createdAt;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }

    public String getReviewerComments() {
        return reviewerComments;
    }

    public void setReviewerComments(String reviewerComments) {
        this.reviewerComments = reviewerComments;
    }

    public void transitionTo(RoteiroStatus newStatus, User user, String justification) {
        RoteiroState currentState = RoteiroStateFactory.getState(this.status);
        currentState.transition(this, newStatus, user, justification);
    }

    public static Roteiro createNewSubmission(String content, String clientName, String clientEmail, String clientPhone) {
        Roteiro roteiro = new Roteiro();
        roteiro.setContent(content);
        roteiro.setClientName(clientName);
        roteiro.setClientEmail(clientEmail);
        roteiro.setClientPhone(clientPhone);
        roteiro.setStatus(RoteiroStatus.AGUARDANDO_ANALISE);
        roteiro.setCreatedAt(LocalDateTime.now());
        return roteiro;
    }


}
