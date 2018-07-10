package br.senai.collabtrack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.MonitorMonitorado;

/**
 * Created by kevin on 3/6/18.
 */

public class MonitorMonitoradoSwitchAdapter extends RecyclerView.Adapter<MonitorMonitoradoSwitchAdapter.MonitoradosViewHolder> {

    private final Context context;
    private final List<MonitorMonitorado> monitoradoMonitoradoList;
    private final MonitorMonitoradoSwitchAdapter.MonitorOnCheckedChangeListener monitorOnCheckedChangeListener;

    public MonitorMonitoradoSwitchAdapter(Context context, List<MonitorMonitorado> monitoradoMonitoradoList, MonitorMonitoradoSwitchAdapter.MonitorOnCheckedChangeListener monitorOnCheckedChangeListener){
        this.context = context;
        this.monitoradoMonitoradoList = monitoradoMonitoradoList;
        this.monitorOnCheckedChangeListener = monitorOnCheckedChangeListener;
    }

    @Override
    public int getItemCount() {
        return monitoradoMonitoradoList != null ? monitoradoMonitoradoList.size() : 0;
    }

    @Override
    public MonitorMonitoradoSwitchAdapter.MonitoradosViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_monitor_monitorado_switch, viewGroup, false);
        MonitorMonitoradoSwitchAdapter.MonitoradosViewHolder holder = new MonitorMonitoradoSwitchAdapter.MonitoradosViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MonitorMonitoradoSwitchAdapter.MonitoradosViewHolder holder, final int position) {
        MonitorMonitorado monitorMonitorado = monitoradoMonitoradoList.get(position);
        holder.nome.setText(monitorMonitorado.getMonitorado().getNome());

        if(monitorOnCheckedChangeListener != null){
            holder.statusMonitorado.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

    public static class MonitoradosViewHolder extends RecyclerView.ViewHolder{
        public Switch statusMonitorado;
        public TextView nome;

        public MonitoradosViewHolder(View view){
            super(view);
            statusMonitorado = (Switch) view.findViewById(R.id.status_monitorado);
            nome = (TextView) view.findViewById(R.id.nome_monitorado);
        }
    }

}
