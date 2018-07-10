package br.senai.collabtrack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.Monitor;

/**
 * Created by kevin on 10/11/17.
 */

public class MonitorSwitchAdapter extends RecyclerView.Adapter<MonitorSwitchAdapter.MonitoresViewHolder>{

    private final Context context;
    private final List<Monitor> monitorList;
    private MonitorOnCheckedChangeListener monitorOnCheckedChangeListener;
    private boolean enabled;

    public MonitorSwitchAdapter(Context context, List<Monitor> monitorList, MonitorOnCheckedChangeListener monitorOnCheckedChangeListener, boolean enabled){
        this.context = context;
        this.monitorList = monitorList;
        this.monitorOnCheckedChangeListener = monitorOnCheckedChangeListener;
        this.enabled = enabled;
    }

    @Override
    public int getItemCount() {
        return monitorList != null ? monitorList.size() : 0;
    }

    @Override
    public MonitorSwitchAdapter.MonitoresViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_switch_monitor, viewGroup, false);
        MonitoresViewHolder holder = new MonitoresViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MonitoresViewHolder holder, final int position) {
        Monitor monitor = monitorList.get(position);
        holder.nome.setText(monitor.getNome());

        if (monitor.isDesabilitado()){
            holder.switch_monitor.setEnabled(false);
            holder.borda.setVisibility(View.VISIBLE);
        } else {
            holder.switch_monitor.setEnabled(this.enabled);
            holder.switch_monitor.setChecked(monitor.isMarcado());
            holder.borda.setVisibility(View.GONE);
        }

        if(monitorOnCheckedChangeListener != null){
            holder.switch_monitor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    monitorOnCheckedChangeListener.onCheckedChangeMonitor(isChecked, position);
                }
            });
        }
    }

    public interface MonitorOnCheckedChangeListener{
        void onCheckedChangeMonitor(boolean isChecked, int idx);
    }

    public static class MonitoresViewHolder extends RecyclerView.ViewHolder{
        public TextView nome;
        public Switch switch_monitor;
        public View borda;

        public MonitoresViewHolder(View view){
            super(view);
            nome = (TextView) view.findViewById(R.id.nome_monitor);
            switch_monitor = (Switch) view.findViewById(R.id.switch_monitor);
            borda = view.findViewById(R.id.borda_monitor_desabilitado);
        }
    }

    public void desabilitarMonitor(long idMonitor){

        if (monitorList != null && monitorList.size() > 0) {
            List<Integer> indexes = new ArrayList<>();
            ListIterator iterator = monitorList.listIterator();
            while (iterator.hasNext()) {

                // Buscando a posição do item posterior
                int position = iterator.nextIndex();
                // Trocando a posição na lista
                Monitor monitor = (Monitor) iterator.next();

                // Desabilita o monitor com o código passado
                if (monitor.getId() == idMonitor && !monitor.isDesabilitado()) {
                    monitor.setDesabilitado(true);
                    indexes.add(position);
                } else if (monitor.getId() != idMonitor && monitor.isDesabilitado()) {
                    // Habilita o monitor
                    monitor.setDesabilitado(false);
                    indexes.add(position);
                }

            }

            if (indexes.size() > 0) {
                for (Integer index : indexes) {
                    notifyItemChanged(index);
                }
            }
        }

    }

    public void habilitarMonitores(List<Monitor> monitores){

        if (monitores != null && monitores.size() > 0) {
            // Iterator
            ListIterator iteratorParam = monitores.listIterator();
            int index = -1;
            while (iteratorParam.hasNext()) {

                // Trocando a posição na lista
                Monitor monitorParam = (Monitor) iteratorParam.next();

                // Iterator
                ListIterator iteratorAdapter = monitorList.listIterator();

                while (iteratorAdapter.hasNext()) {

                    // Buscando a posição do item posterior
                    int positionAdapter = iteratorAdapter.nextIndex();

                    // Trocando a posição na lista
                    Monitor monitorAdapter = (Monitor) iteratorAdapter.next();

                    // Habilita se possuir o ID correto
                    if (monitorParam.getId() == monitorAdapter.getId()) {
                        monitorAdapter.setDesabilitado(false);
                        monitorAdapter.setMarcado(true);
                        index = positionAdapter;
                    }

                }
            }

            if (index != -1) {
                notifyItemChanged(index);
            }
        }
    }

}
