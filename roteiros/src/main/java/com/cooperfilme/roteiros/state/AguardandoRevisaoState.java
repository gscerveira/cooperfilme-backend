package com.cooperfilme.roteiros.state;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import com.cooperfilme.roteiros.model.User;
import com.cooperfilme.roteiros.model.UserRole;

public class AguardandoRevisaoState implements RoteiroState {
    @Override
    public boolean canTransitionTo(RoteiroStatus newStatus, UserRole userRole) {
        return newStatus == RoteiroStatus.EM_REVISAO && userRole == UserRole.REVISOR;
    }

    @Override
    public void transition(Roteiro roteiro, RoteiroStatus newStatus, User user, String justification) {
        if (!canTransitionTo(newStatus, user.getRole())) {
            throw new IllegalStateException("Transição inválida de AGUARDANDO_REVISAO para " + newStatus);
        }
        roteiro.setStatus(newStatus);
        roteiro.setAssignedTo(user);
        roteiro.setJustification(justification);
    }

}
