package br.senai.collabtrack.fragment.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.R;
import br.senai.collabtrack.service.MensagemService;

/**
 * Created by lucas on 11/04/2018.
 */

public class MensagemDialogExcluirFragment extends DialogFragment {

    private Bundle bundle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle = getArguments();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.acoes)
                .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogResult();
                    }
                })
                .setMessage(getString(R.string.excluir_mensagens));
        return builder.create();
    }

    private void dialogResult(){
        long idMonitorado = bundle.getInt("idMonitorado");
        MensagemService mensagemService = new MensagemService(CollabtrackApplication.getContext());
        mensagemService.deletarMensamgens(idMonitorado);

        Intent it = new Intent("REFRESH_CHAT");
        LocalBroadcastManager.getInstance(CollabtrackApplication.getContext()).sendBroadcast(it);
    }
}
