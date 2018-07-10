package br.senai.collabtrack.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.ListIterator;

import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.AreaSegura;
import br.senai.collabtrack.util.ColorUtil;

/**
 * Created by kevin on 18/05/17.
 */

public class AreaSeguraAdapter extends RecyclerView.Adapter<AreaSeguraAdapter.AreasSegurasViewHolder>{


    private final Context context;
    private final List<AreaSegura> areaSeguraList;
    private AreaSeguraOnClickListener areaSeguraOnClickListener;

    public AreaSeguraAdapter(Context context, List<AreaSegura> areaSeguraList, AreaSeguraOnClickListener areaSeguraOnClickListener){
        this.context = context;
        this.areaSeguraList = areaSeguraList;
        this.areaSeguraOnClickListener = areaSeguraOnClickListener;
    }

    @Override
    public int getItemCount() {
        return areaSeguraList != null ? areaSeguraList.size() : 0;
    }

    @Override
    public AreasSegurasViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_area_segura, viewGroup, false);
        AreasSegurasViewHolder holder = new AreasSegurasViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final AreasSegurasViewHolder holder, final int position) {
        final AreaSegura areaSegura = areaSeguraList.get(position);
        holder.nome.setText(areaSegura.getNome());
        holder.nome.setTextColor(areaSegura.getCor());
        holder.conteudo.setText(context.getString(R.string.raio)+": "+areaSegura.getRaio()+" "+context.getString(R.string.metros).toLowerCase());
        holder.cardView.findViewById(R.id.borda_area_segura).setBackgroundColor(areaSegura.getCorBorda());

        if(areaSeguraOnClickListener != null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    areaSeguraOnClickListener.onClickAreaSegura(holder.itemView, areaSegura);
                }
            });
        }
    }

    public interface AreaSeguraOnClickListener{
        void onClickAreaSegura(View view, AreaSegura areaSegura);
    }

    public static class AreasSegurasViewHolder extends RecyclerView.ViewHolder{
        public TextView nome;
        public TextView conteudo;
        public CardView cardView;
        public AreasSegurasViewHolder(View view) {
            super(view);

            nome = (TextView) view.findViewById(R.id.nome_area_segura);
            conteudo = (TextView) view.findViewById(R.id.conteudo_area_segura);
            cardView = (CardView) view.findViewById(R.id.card_view_area_segura);
        }
    }

    /**
     * Método responsável por remover uma área segura da lista
     * @param id ID da área segura que será removida
     */
    public void removeAreaSegura(long id){

        ListIterator iterator = areaSeguraList.listIterator();
        while (iterator.hasNext()){

            // Buscando a posição do item posterior
            int position = iterator.nextIndex();
            // Trocando a posição na lista
            AreaSegura areaSeguraIterator = (AreaSegura) iterator.next();
            // Pegando a área segura
            AreaSegura areaSegura = areaSeguraList.get(position);
            if(areaSegura.getId() == id) {
                // Removendo a área segura
                iterator.remove();
                areaSeguraList.remove(areaSeguraIterator);
                // Executando as animações para mostrar que a área segura foi removida
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, areaSeguraList.size());
            }

        }

    }

    /**
     * Método responsável por atualizar os dados de uma área segura da lista
     * @param areaSegura Ínstância com os novos dados da área segura
     */
    public void editAreaSegura(AreaSegura areaSegura){

        if (areaSeguraList != null && areaSeguraList.size() > 0) {

            int index = -1;
            ListIterator iterator = areaSeguraList.listIterator();
            while (iterator.hasNext()) {

                // Buscando a posição do item posterior
                int position = iterator.nextIndex();
                // Trocando a posição na lista
                AreaSegura areaSeguraIterator = (AreaSegura) iterator.next();
                // Pegando a área segura
                if (areaSeguraIterator.getId() == areaSegura.getId()) {
                    areaSeguraIterator.setNome(areaSegura.getNome());
                    areaSeguraIterator.setRaio(areaSegura.getRaio());
                    areaSeguraIterator.setCor(areaSegura.getCor());
                    areaSeguraIterator.setCorBorda(areaSegura.getCorBorda());
                    index = position;
                }

            }

            if(index != -1) {
                notifyItemChanged(index);
            }

        }

    }

}
