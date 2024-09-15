package com.cooperfilme.roteiros.state;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import com.cooperfilme.roteiros.model.User;
import com.cooperfilme.roteiros.model.UserRole;

public class AguardandoAprovacaoState implements RoteiroState {
    @Override
    public boolean canTransitionTo(RoteiroStatus newStatus, UserRole userRole) {
        return (newStatus == RoteiroStatus.EM_APROVACAO || newStatus == RoteiroStatus.RECUSADO)
        && userRole == UserRole.APROVADOR;
    }

    @Override
    public void transition(Roteiro roteiro, RoteiroStatus newStatus, User user, String justification) {
        if (!canTransitionTo(newStatus, user.getRole())) {
            throw new IllegalStateException("Transição inválida de AGUARDANDO_APROVACAO para " + newStatus);
        }
        roteiro.setStatus(newStatus);
        roteiro.setJustification(justification);
    }

}
