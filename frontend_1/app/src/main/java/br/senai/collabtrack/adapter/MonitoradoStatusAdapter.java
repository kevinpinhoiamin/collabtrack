package br.senai.collabtrack.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;

import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.domain.Monitorado;
import br.senai.collabtrack.domain.Status;
import br.senai.collabtrack.util.ColorUtil;
import br.senai.collabtrack.util.DateUtil;

/**
 * Created by kevin on 8/9/17.
 */

public class MonitoradoStatusAdapter extends RecyclerView.Adapter<MonitoradoStatusAdapter.StatusViewHolder>{

    private final Context context;
    private final List<Status> statusList;
    private StatusOnClickListener statusOnClickListener;

    public MonitoradoStatusAdapter(Context context, List<Status> statusList, StatusOnClickListener statusOnClickListener){
        this.context = context;
        this.statusList = statusList;
        this.statusOnClickListener = statusOnClickListener;
    }

    @Override
    public int getItemCount() {
        return statusList != null ? statusList.size() : 0;
    }

    @Override
    public StatusViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_monitorado_status, viewGroup, false);
        StatusViewHolder holder = new StatusViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final StatusViewHolder holder, final int position) {
        final Status status = statusList.get(position);
        holder.cardView.setBackgroundColor(status.isAtivo() ? ContextCompat.getColor(context, R.color.activeColor) : ContextCompat.getColor(context, R.color.inactiveColor));
        holder.bordaMonitorado.setBackgroundColor(ColorUtil.getColor(status.getCor()));
        holder.bordaSuperior.setBackgroundColor(ColorUtil.getColor(status.getCor()));
        holder.bordaCentral.setBackgroundColor(ColorUtil.getColor(status.getCor()));
        holder.bordaInferior.setBackgroundColor(ColorUtil.getColor(status.getCor()));
        holder.nome.setText(status.getMonitorado().getNome());
        holder.monitoramentoImage.setImageResource(status.isEmMonitoramento() ? R.drawable.ic_notification_active_green : R.drawable.ic_notification_inactive_red);
        holder.monitoramentoText.setText(context.getString(status.isEmMonitoramento() ? R.string.em_monitoramento : R.string.nao_esta_em_monitoramento));
        holder.ativoInativo.setText(status.isAtivo() ? R.string.ativo : R.string.inativo);
        holder.tipoMonitor.setText(status.isPrincipal() ? R.string.monitor_principal : R.string.monitor_secundario);
        String data = DateUtil.fromAPITODescribed(status.getData());
        holder.data.setText("Última atualização: " + (data == null || data.equals("") ? "indisponível" : data));
        if (status.isEmMonitoramento() && DateUtil.isFiveMinutesOlder(status.getData())) {
            holder.data.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        status.setBateria(686);
        status.setWifi(false);
        status.setInternetMovel(true);
        status.setLocalizacao(true);

        holder.bateriaStatus.setText(status.getBateria()+"%");
        int ic_battery = R.drawable.ic_battery_full;
        if(status.getBateria() <= 0){
            ic_battery = R.drawable.ic_battery_alert;
        }else if(status.getBateria() <= 20){
            ic_battery = R.drawable.ic_battery_20;
        }else if(status.getBateria() <= 30){
            ic_battery = R.drawable.ic_battery_30;
        }else if(status.getBateria() <= 50){
            ic_battery = R.drawable.ic_battery_50;
        }else if(status.getBateria() <= 60){
            ic_battery = R.drawable.ic_battery_60;
        }else if(status.getBateria() <= 80){
            ic_battery = R.drawable.ic_battery_80;
        }else if(status.getBateria() <= 90){
            ic_battery = R.drawable.ic_battery_90;
        }
        holder.bateriaImageView.setImageResource(ic_battery);
        if(status.isWifi()){
            holder.conexaoImageView.setImageResource(R.drawable.ic_wi_fi);
            holder.conxaoStatus.setText(R.string.wi_fi);
        }else if(status.isInternetMovel()){
            holder.conexaoImageView.setImageResource(R.drawable.ic_signal);
            holder.conxaoStatus.setText(R.string.movel);
        }else{
            holder.conexaoImageView.setImageResource(R.drawable.ic_signal_alert);
            holder.conxaoStatus.setText(R.string.desconectado);
        }
        holder.localizacaoStatus.setText(status.isLocalizacao() ? R.string.ligada : R.string.desligada);
        holder.localizacaoImageView.setImageResource(status.isLocalizacao() ? R.drawable.ic_location_on : R.drawable.ic_location_off);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusOnClickListener.onClickStatus(holder.itemView, status);
            }
        });
    }

    public interface StatusOnClickListener{
        void onClickStatus(View view, Status status);
    }

    public static class StatusViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public FrameLayout bordaMonitorado;
        public FrameLayout bordaSuperior;
        public FrameLayout bordaCentral;
        public FrameLayout bordaInferior;
        public TextView nome;
        public ImageView monitoramentoImage;
        public TextView monitoramentoText;
        public TextView tipoMonitor;
        public TextView ativoInativo;
        public TextView data;
        public ImageView bateriaImageView;
        public TextView bateriaStatus;
        public ImageView conexaoImageView;
        public TextView conxaoStatus;
        public ImageView localizacaoImageView;
        public TextView localizacaoStatus;

        StatusViewHolder(View view){
            super(view);

            cardView = (CardView) view.findViewById(R.id.card_view_status);
            bordaMonitorado = (FrameLayout)  view.findViewById(R.id.borda_monitorado);
            bordaSuperior = (FrameLayout) view.findViewById(R.id.borda_superior);
            bordaCentral = (FrameLayout) view.findViewById(R.id.borda_central);
            bordaInferior = (FrameLayout) view.findViewById(R.id.borda_inferior);
            nome = (TextView) view.findViewById(R.id.nome_monitorado);
            monitoramentoImage = (ImageView) view.findViewById(R.id.image_view_monitoramento);
            monitoramentoText = (TextView) view.findViewById(R.id.text_view_monitoramento);
            tipoMonitor = (TextView) view.findViewById(R.id.tipo_monitor);
            ativoInativo = (TextView) view.findViewById(R.id.ativo_inativo);
            data = (TextView) view.findViewById(R.id.data_status);
            bateriaImageView = (ImageView) view.findViewById(R.id.image_view_bateria);
            bateriaStatus = (TextView) view.findViewById(R.id.bateria_status);
            conexaoImageView = (ImageView) view.findViewById(R.id.image_view_conexao);
            conxaoStatus = (TextView) view.findViewById(R.id.conexao_status);
            localizacaoImageView = (ImageView) view.findViewById(R.id.image_view_localizacao);
            localizacaoStatus = (TextView) view.findViewById(R.id.localizacao_status);

        }
    }

    public void alterarStatus(Status status){

        boolean changed = false;
        if (statusList != null && statusList.size() > 0) {

            ListIterator<Status> iterator = statusList.listIterator();
            while (iterator.hasNext()) {
                // Buscando a posição do item posterior
                int position = iterator.nextIndex();
                // Trocando a posição na lista
                Status statusIterator = iterator.next();
                if (status.getId() == statusIterator.getId()) {
                    status.setData(statusIterator.getData());
                    statusList.set(position, status);
                    notifyItemChanged(position);
                    changed = true;
                }
            }

        }

        // Se não encontrou o item na lista, faz a adição
        if (!changed) {
            statusList.add(status);
            notifyItemInserted(statusList.size() - 1);
        }

    }

    public void editarStatus(Status status){

        if (statusList != null && statusList.size() > 0) {
            int index = -1;
            ListIterator<Status> iterator = statusList.listIterator();
            while (iterator.hasNext()) {
                // Buscando a posição do item posterior
                int position = iterator.nextIndex();
                // Trocando a posição na lista
                Status statusIterator = iterator.next();
                if (status.getId() == statusIterator.getId()) {
                    statusIterator.setLocalizacao(status.isLocalizacao());
                    statusIterator.setWifi(status.isWifi());
                    statusIterator.setInternetMovel(status.isInternetMovel());
                    statusIterator.setBateria(status.getBateria());
                    if (status.getData() != null && !status.getData().equals("")) {
                        statusIterator.setData(status.getData());
                    }
                    index = position;
                }
            }

            if (index != -1) {
                notifyItemChanged(index);
            }
        }

    }

    public void removerMonitorado(Monitorado monitorado){

        if (statusList != null && statusList.size() > 0) {
            int index = -1;
            ListIterator<Status> iterator = statusList.listIterator();
            while(iterator.hasNext()){
                // Buscando a posição do item posterior
                int position = iterator.nextIndex();
                // Trocando a posição na lista
                Status status = iterator.next();
                if(monitorado.getId() == status.getMonitorado().getId()){
                    index = position;
                }
            }
            if (index != -1) {
                statusList.remove(index);
                notifyItemRemoved(index);
            }
        }

    }

    public void removerMonitorado(Long idMonitorado){
        Monitorado monitorado = new Monitorado();
        monitorado.setId(idMonitorado);
        removerMonitorado(monitorado);
    }

    public void alterarMonitoramento(MonitorMonitorado monitorMonitorado){

        if (statusList != null && statusList.size() > 0) {
            int index = -1;
            ListIterator<Status> iterator = statusList.listIterator();
            while (iterator.hasNext()) {
                // Buscando a posição do item posterior
                int position = iterator.nextIndex();
                // Trocando a posição na lista
                Status status = iterator.next();
                if (monitorMonitorado.getMonitorado().getId() == status.getMonitorado().getId()) {
                    status.setEmMonitoramento(monitorMonitorado.isEmMonitoramento());
                    index = position;
                }
            }

            if (index != -1) {
                notifyItemChanged(index);
            }
        }

    }

}
