package br.senai.collabtrack.fragment.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.Status;
import br.senai.collabtrack.domain.util.StatusUtil;

/**
 * Created by kevin on 9/4/17.
 */

public class StatusDialogFragment extends DialogFragment{

    private String[] options;
    private Status status;
    private boolean emMonitoramento;
    private boolean temLocalizacao;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        status = StatusUtil.writeToStatus(getArguments().getParcelable("status"));
        emMonitoramento = getArguments().getBoolean("emMonitoramento");
        temLocalizacao = getArguments().getBoolean("temLocalizacao");

        if(status.isPrincipal() && temLocalizacao){
            options = new String[]{getString(R.string.editar), getString(R.string.visualizar_no_mapa), getString(R.string.limpar_localizacoes), getString(emMonitoramento ? R.string.finalizar_monitoramento : R.string.iniciar_monitoramento)};
        }else if(status.isPrincipal() && !temLocalizacao){
            options = new String[]{getString(R.string.editar), getString(R.string.visualizar_no_mapa), getString(emMonitoramento ? R.string.finalizar_monitoramento : R.string.iniciar_monitoramento)};
        }else if(!status.isAtivo()){
            options = new String[]{getString(R.string.ativar)};
        }else if(temLocalizacao){
            options = new String[]{getString(R.string.editar), getString(R.string.inativar), getString(R.string.visualizar_no_mapa), getString(R.string.limpar_localizacoes), getString(emMonitoramento ? R.string.finalizar_monitoramento : R.string.iniciar_monitoramento)};
        }else{
            options = new String[]{getString(R.string.editar), getString(R.string.inativar), getString(emMonitoramento ? R.string.finalizar_monitoramento : R.string.iniciar_monitoramento)};
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.acoes)
                .setNegativeButton(R.string.fechar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogResult(which);
                    }
                });
        return builder.create();
    }

    private void dialogResult(int action) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        if(status.isPrincipal() && temLocalizacao){

            switch (action){
                case 0:
                    bundle.putParcelable("statusEditar", StatusUtil.writeToParcel(status));
                    break;
                case 1:
                    bundle.putParcelable("statusMostrar", StatusUtil.writeToParcel(status));
                    break;
                case 2:
                    bundle.putParcelable("statusLimparLocalizacao", StatusUtil.writeToParcel(status));
                    break;
                case 3:
                    bundle.putParcelable(emMonitoramento ? "statusFinalizarMonitoramento" : "statusIniciarMonitoramento", StatusUtil.writeToParcel(status));
                    break;
            }

        }else if(status.isPrincipal() && !temLocalizacao){

            switch (action){
                case 0:
                    bundle.putParcelable("statusEditar", StatusUtil.writeToParcel(status));
                    break;
                case 1:
                    bundle.putParcelable("statusMostrar", StatusUtil.writeToParcel(status));
                    break;
                case 2:
                    bundle.putParcelable(emMonitoramento ? "statusFinalizarMonitoramento" : "statusIniciarMonitoramento", StatusUtil.writeToParcel(status));
                    break;
            }

        }else if(!status.isAtivo()){

            bundle.putParcelable("statusAtivar", StatusUtil.writeToParcel(status));

        }else if(temLocalizacao){

            switch (action){
                case 0:
                    bundle.putParcelable("statusEditar", StatusUtil.writeToParcel(status));
                    break;
                case 1:
                    bundle.putParcelable("statusInativar", StatusUtil.writeToParcel(status));
                    break;
                case 2:
                    bundle.putParcelable("statusMostrar", StatusUtil.writeToParcel(status));
                    break;
                case 3:
                    bundle.putParcelable("statusLimparLocalizacao", StatusUtil.writeToParcel(status));
                    break;
                case 4:
                    bundle.putParcelable(emMonitoramento ? "statusFinalizarMonitoramento" : "statusIniciarMonitoramento", StatusUtil.writeToParcel(status));
                    break;
            }

        }else{

            switch (action){
                case 0:
                    bundle.putParcelable("statusEditar", StatusUtil.writeToParcel(status));
                    break;
                case 1:
                    bundle.putParcelable("statusInativar", StatusUtil.writeToParcel(status));
                    break;
                case 2:
                    bundle.putParcelable(emMonitoramento ? "statusFinalizarMonitoramento" : "statusIniciarMonitoramento", StatusUtil.writeToParcel(status));
                    break;
            }

        }

        intent.putExtras(bundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

}
