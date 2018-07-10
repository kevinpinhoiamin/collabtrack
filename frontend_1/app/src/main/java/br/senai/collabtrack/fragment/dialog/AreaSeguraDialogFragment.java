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
import br.senai.collabtrack.domain.AreaSegura;
import br.senai.collabtrack.domain.util.AreaSeguraUtil;

/**
 * Created by kevin on 26/05/17.
 */

public class AreaSeguraDialogFragment extends DialogFragment {

    private String[] options;
    private AreaSegura areaSegura;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();

        areaSegura = AreaSeguraUtil.writeToAraSegura(bundle.getParcelable("areaSegura"));

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        options = new String[]{getString(R.string.editar),
                getString(R.string.excluir),
                getString(R.string.visualizar_no_mapa)
        };

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

    private void dialogResult(int action){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        switch (action){
            case 0: // Editar
                bundle.putParcelable("areaSeguraEditar", AreaSeguraUtil.writeToParcel(areaSegura));
                break;
            case 1: // Excluir
                bundle.putParcelable("areaSeguraRemovida", AreaSeguraUtil.writeToParcel(areaSegura));
                break;
            case 2: // Visualizar no mapa
                bundle.putParcelable("areaSeguraMostrar", AreaSeguraUtil.writeToParcel(areaSegura));
                break;
        }

        intent.putExtras(bundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

}
