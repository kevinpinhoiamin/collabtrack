package br.senai.collabtrack.fragment.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.AreaSegura;
import br.senai.collabtrack.domain.util.AreaSeguraUtil;


public class AreaSeguraDialogExcluirFragment extends DialogFragment {

    private AreaSegura areaSegura;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        areaSegura = AreaSeguraUtil.writeToAraSegura(getArguments().getParcelable("areaSegura"));
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
                .setMessage(getString(R.string.excluir_area_segura));
        return builder.create();
    }

    private void dialogResult(){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putParcelable("areaSegura", AreaSeguraUtil.writeToParcel(areaSegura));
        intent.putExtras(bundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
    }

}
