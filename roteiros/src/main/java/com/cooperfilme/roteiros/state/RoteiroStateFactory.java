package com.cooperfilme.roteiros.state;

import com.cooperfilme.roteiros.model.RoteiroStatus;

import java.util.HashMap;
import java.util.Map;

public class RoteiroStateFactory {
    private static final Map<RoteiroStatus, RoteiroState> states = new HashMap<>();

    static {
        states.put(RoteiroStatus.AGUARDANDO_ANALISE, new AguardandoAnaliseState());
        states.put(RoteiroStatus.EM_APROVACAO, new EmAprovacaoState());
        states.put(RoteiroStatus.APROVADO, new AprovadoState());
        states.put(RoteiroStatus.RECUSADO, new RecusadoState());
        states.put(RoteiroStatus.EM_REVISAO, new AguardandoRevisaoState());
        states.put(RoteiroStatus.AGUARDANDO_REVISAO, new EmRevisaoState());
        states.put(RoteiroStatus.EM_ANALISE, new EmAnaliseState());
        states.put(RoteiroStatus.AGUARDANDO_APROVACAO, new AguardandoAprovacaoState());
    }

    public static RoteiroState getState(RoteiroStatus status) {
        return states.get(status);
    }

}
