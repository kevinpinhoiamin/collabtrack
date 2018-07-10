package br.senai.collabtrack.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;

import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.domain.Monitorado;
import br.senai.collabtrack.domain.Status;
import br.senai.collabtrack.util.ColorUtil;

/**
 * Created by kevin on 8/7/17.
 */

public class MonitorMonitoradoAdapter extends RecyclerView.Adapter<MonitorMonitoradoAdapter.MonitoradosViewHolder>{

    private final Context context;
    private final List<MonitorMonitorado> monitoradoMonitoradoList;
    private final MonitoradoOnClickListener monitoradoOnClickListener;

    public MonitorMonitoradoAdapter(Context context, List<MonitorMonitorado> monitoradoMonitoradoList, MonitoradoOnClickListener monitoradoOnClickListener){
        this.context = context;
        this.monitoradoMonitoradoList = monitoradoMonitoradoList;
        this.monitoradoOnClickListener = monitoradoOnClickListener;
    }

    @Override
    public int getItemCount() {
        return monitoradoMonitoradoList != null ? monitoradoMonitoradoList.size() : 0;
    }

    @Override
    public MonitoradosViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_monitor_monitorado, viewGroup, false);
        MonitoradosViewHolder holder = new MonitoradosViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MonitoradosViewHolder holder, final int position) {
        MonitorMonitorado monitorMonitorado = monitoradoMonitoradoList.get(position);
        holder.borda.setBackgroundColor(ColorUtil.getColor(monitorMonitorado.getCor()));
        holder.nome.setText(monitorMonitorado.getMonitorado().getNome() + (!monitorMonitorado.isAtivo() ? " ("+context.getString(R.string.inativo)+")" : ""));
        holder.card.setCardBackgroundColor(monitorMonitorado.isAtivo() ? ContextCompat.getColor(context, R.color.activeColor) : ContextCompat.getColor(context, R.color.inactiveColor));

        if(monitorMonitorado.isAtivo() && monitoradoOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    monitoradoOnClickListener.onClickMonitorado(holder.itemView, position);
                }
            });
        }
    }

    public interface MonitoradoOnClickListener{
        void onClickMonitorado(View view, int idx);
    }

    public static class MonitoradosViewHolder extends RecyclerView.ViewHolder{
        public FrameLayout borda;
        public TextView nome;
        public CardView card;

        public MonitoradosViewHolder(View view){
            super(view);
            borda = (FrameLayout) view.findViewById(R.id.borda_monitorado);
            nome = (TextView) view.findViewById(R.id.nome_monitorado);
            card = view.findViewById(R.id.card_view_monitorado);
        }
    }

    public void removerMonitorado(Long idMonitorado){
        Monitorado monitorado = new Monitorado();
        monitorado.setId(idMonitorado);
        removerMonitorado(monitorado);
    }

    public void removerMonitorado(Monitorado monitorado){

        if (monitoradoMonitoradoList != null && monitoradoMonitoradoList.size() > 0) {
            int index = -1;
            ListIterator<MonitorMonitorado> iterator = monitoradoMonitoradoList.listIterator();
            while(iterator.hasNext()){
                // Buscando a posição do item posterior
                int position = iterator.nextIndex();
                // Trocando a posição na lista
                MonitorMonitorado monitorMonitorado = iterator.next();
                if(monitorado.getId() == monitorMonitorado.getMonitorado().getId()){
                    index = position;
                }
            }

            if(index != -1) {
                monitoradoMonitoradoList.remove(index);
                notifyItemRemoved(index);
            }
        }

    }

}
