package br.com.senai.colabtrack.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.R;
import br.com.senai.colabtrack.domain.AreaSeguraMonitorado;
import br.com.senai.colabtrack.domain.Monitorado;

/**
 * Created by kevin on 05/07/17.
 */

public class AreaSeguraMonitoradoAdapter extends RecyclerView.Adapter<AreaSeguraMonitoradoAdapter.AreasSegurasMonitoradosViewHolder>{


    private final Context context;
    private List<AreaSeguraMonitorado> areaSeguraMonitoradoList;
    private AreaSeguraMonitoradoOnCheckedChangeListener areaSeguraMonitoradoOnCheckedChangeListener;

    public AreaSeguraMonitoradoAdapter(Context context, List<AreaSeguraMonitorado> areaSeguraMonitoradoList, AreaSeguraMonitoradoOnCheckedChangeListener areaSeguraMonitoradoOnCheckedChangeListener){
        this.context = context;
        this.areaSeguraMonitoradoList = areaSeguraMonitoradoList;
        this.areaSeguraMonitoradoOnCheckedChangeListener = areaSeguraMonitoradoOnCheckedChangeListener;
    }

    @Override
    public int getItemCount() {
        return areaSeguraMonitoradoList != null ? areaSeguraMonitoradoList.size() : 0;
    }

    @Override
    public AreasSegurasMonitoradosViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_area_segura_monitorado, viewGroup, false);
        AreasSegurasMonitoradosViewHolder holder = new AreasSegurasMonitoradosViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AreasSegurasMonitoradosViewHolder holder, final int position) {
        AreaSeguraMonitorado areaSeguraMonitorado = areaSeguraMonitoradoList.get(position);

        holder.nome.setText(areaSeguraMonitorado.getMonitorado().getNome());
        holder.status.setChecked(areaSeguraMonitorado.isAtiva());

        if(areaSeguraMonitoradoOnCheckedChangeListener != null){
            holder.status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    areaSeguraMonitoradoOnCheckedChangeListener.onCheckedChangeAreaSeguraMonitorado(isChecked, position);
                }
            });
        }
    }

    public interface AreaSeguraMonitoradoOnCheckedChangeListener{
        void onCheckedChangeAreaSeguraMonitorado(boolean isChecked, int position);
    }

    public static class AreasSegurasMonitoradosViewHolder extends RecyclerView.ViewHolder{
        public TextView nome;
        public Switch status;

        public AreasSegurasMonitoradosViewHolder(View view){
            super(view);

            nome = (TextView) view.findViewById(R.id.nome_monitorado);
            status = (Switch) view.findViewById(R.id.status_monitorado);
        }

    }

    private List<Long> pegarIdsMonitorado() {
        List<Long> monitoradosId = new ArrayList<>();
        ListIterator<AreaSeguraMonitorado> iterator = areaSeguraMonitoradoList.listIterator();
        while (iterator.hasNext()) {
            AreaSeguraMonitorado areaSeguraMonitorado = iterator.next();
            monitoradosId.add(areaSeguraMonitorado.getMonitorado().getId());
        }
        return monitoradosId;
    }

    public void adicionarMonitorados(List<AreaSeguraMonitorado> areaSeguraMonitorados) {

        if (areaSeguraMonitorados != null && areaSeguraMonitorados.size() > 0) {

            boolean added = false;
            List<Long> monitoradosId = pegarIdsMonitorado();

            ListIterator<AreaSeguraMonitorado> iterator = areaSeguraMonitorados.listIterator();
            while (iterator.hasNext()) {

                AreaSeguraMonitorado areaSeguraMonitorado = iterator.next();
                if (!monitoradosId.contains(areaSeguraMonitorado.getMonitorado().getId())) {
                    areaSeguraMonitoradoList.add(areaSeguraMonitorado);
                    added = true;
                }

            }

            if (added) {
                notifyDataSetChanged();
            }

        }

    }

}
