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
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import br.senai.collabtrack.R;
import br.senai.collabtrack.adapter.MonitorMonitoradoSwitchAdapter;
import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.domain.util.MonitorMonitoradoUtil;
import br.senai.collabtrack.util.DateUtil;

public class FiltrarLocalizacoesDialogFragment extends DialogFragment {

    private TextView textViewMonitorMonitorados;
    private RecyclerView recyclerViewMonitorMonitorados;

    private MaterialBetterSpinner periodoSpinner;
    private ArrayAdapter<String> periodoAdapter;

    private TextView textViewPontos;
    private SeekBar seekBarPontos;

    private MonitorMonitoradoSwitchAdapter monitorMonitoradoSwitchAdapter;
    private List<MonitorMonitorado> monitorMonitorados;

    private List<Long> monitorados = new ArrayList<>();

    private static int DEFAULT_PONTOS_MULTIPLIER = 25;
    private static final String HOJE = "Hoje";
    private static final String ULTIMOS_3_DIAS = "Últimos 3 dias";
    private static final String ULTIMA_SEMANA = "Última semana";
    private static final String ULTIMOS_15_DIAS = "Últimos 15 dias";
    private static final String DESDE_O_COMECO = "Desde o começo";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Dados passados pelo bundle
        monitorMonitorados = MonitorMonitoradoUtil.writeToMonitorMonitoradoList(getArguments().getParcelableArrayList("monitorMonitorados"));
        if (monitorMonitorados != null && monitorMonitorados.size() > 0) {

            for (MonitorMonitorado monitorMonitorado : monitorMonitorados) {
                monitorados.add(monitorMonitorado.getMonitorado().getId());
            }

            monitorMonitoradoSwitchAdapter = new MonitorMonitoradoSwitchAdapter(getContext(), monitorMonitorados, new MonitorMonitoradoSwitchAdapter.MonitorOnCheckedChangeListener() {
                @Override
                public void onCheckedChangeMonitor(boolean isChecked, int idx) {
                    if(isChecked) {
                        monitorados.add(monitorMonitorados.get(idx).getMonitorado().getId());
                    } else {

                        for (int i = 0; i < monitorados.size(); i++) {
                            if(monitorados.get(i) == monitorMonitorados.get(idx).getMonitorado().getId()) {
                                monitorados.remove(i);
                            }
                        }

                    }
                }
            });

        }

        // Adapter de período
        periodoAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_dropdown_item_1line,
                new String[]{HOJE, ULTIMOS_3_DIAS, ULTIMA_SEMANA, ULTIMOS_15_DIAS, DESDE_O_COMECO}
        );

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // View
        View view = inflater.inflate(R.layout.fragment_filtrar_localizacoes_dialog, null);

        // Textview
        textViewMonitorMonitorados = (TextView) view.findViewById(R.id.text_view_monitor_monitorados);

        // RecyclerView
        recyclerViewMonitorMonitorados = (RecyclerView) view.findViewById(R.id.recycler_view_monitor_monitorados);

        // Spinner
        periodoSpinner = (MaterialBetterSpinner) view.findViewById(R.id.spinner_periodo);
        periodoSpinner.setAdapter(periodoAdapter);
        periodoSpinner.setText(HOJE);

        // SeekBar
        seekBarPontos = (SeekBar) view.findViewById(R.id.seek_bar_pontos);
        textViewPontos = (TextView) view.findViewById(R.id.text_view_pontos_valor);
        textViewPontos.setText((DEFAULT_PONTOS_MULTIPLIER * 2) + " " + getActivity().getString(R.string.pontos));
        seekBarPontos.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(progress < 1){
                    seekBarPontos.setProgress(1);
                }

                int pontos = seekBarPontos.getProgress() * DEFAULT_PONTOS_MULTIPLIER;
                textViewPontos.setText(pontos + " " + getActivity().getString(R.string.pontos));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(monitorMonitorados != null && monitorMonitorados.size() > 0) {
            recyclerViewMonitorMonitorados.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerViewMonitorMonitorados.setItemAnimator(new DefaultItemAnimator());
            recyclerViewMonitorMonitorados.setHasFixedSize(true);
            recyclerViewMonitorMonitorados.setAdapter(monitorMonitoradoSwitchAdapter);
        } else {
            textViewMonitorMonitorados.setVisibility(View.INVISIBLE);
            recyclerViewMonitorMonitorados.setVisibility(View.INVISIBLE);
        }

        builder.setView(view)
                .setPositiveButton(R.string.filtrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialogResult();
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        FiltrarLocalizacoesDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();

    }

    private int getPeriodo() {

        int periodo = 1;

        switch (periodoSpinner.getText().toString()) {
            case HOJE:
                periodo = DateUtil.PERIODO_HOJE;
                break;

            case ULTIMOS_3_DIAS:
                periodo = DateUtil.PERIODO_ULTIMOS_3_DIAS;
                break;
            case ULTIMA_SEMANA:
                periodo = DateUtil.PERIODO_ULTIMA_SEMANA;
                break;
            case ULTIMOS_15_DIAS:
                periodo = DateUtil.PERIODO_ULTIMOS_15_DIAS;
                break;
            case DESDE_O_COMECO:
                periodo = DateUtil.PERIODO_DESDE_O_COMECO;
                break;
        }

        return periodo;

    }

    private long[] getMonitorados() {

        long[] monitoradosArray = new long[monitorMonitorados.size()];
        ListIterator iterator = monitorados.listIterator();

        while (iterator.hasNext()){
            int index = iterator.nextIndex();
            monitoradosArray[index] = monitorados.get(index);
            iterator.next();
        }

        return monitoradosArray;
    }

    private void dialogResult() {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();

        bundle.putLongArray("monitorados", getMonitorados());
        bundle.putInt("periodo", getPeriodo());
        bundle.putInt("pontos", seekBarPontos.getProgress() * DEFAULT_PONTOS_MULTIPLIER);

        intent.putExtras(bundle);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);

    }

}
