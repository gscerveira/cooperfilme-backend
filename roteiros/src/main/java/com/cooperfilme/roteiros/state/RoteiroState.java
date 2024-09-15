package com.cooperfilme.roteiros.state;

import com.cooperfilme.roteiros.model.Roteiro;
import com.cooperfilme.roteiros.model.RoteiroStatus;
import com.cooperfilme.roteiros.model.User;
import com.cooperfilme.roteiros.model.UserRole;

public interface RoteiroState {
    boolean canTransitionTo(RoteiroStatus newStatus, UserRole userRole);
    void transition(Roteiro roteiro, RoteiroStatus newStatus, User user, String justification);
}
