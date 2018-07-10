package br.senai.collabtrack.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.R;
import br.senai.collabtrack.activity.ChatActivity;
import br.senai.collabtrack.activity.MainActivity;
import br.senai.collabtrack.adapter.MonitorMonitoradoAdapter;
import br.senai.collabtrack.domain.MonitorMonitorado;
import br.senai.collabtrack.domain.to.DataFirebaseTO;
import br.senai.collabtrack.service.MonitorMonitoradoService;

public class MonitoradosFragment extends BaseFragment{

    // Classe de serviço
    private MonitorMonitoradoService monitorMonitoradoService;
    // Lista de monitorado
    private List<MonitorMonitorado> monitorMonitoradoList;

    // Activity principal
    private MainActivity mainActivity;

    // Recycler View e Adapter
    private RecyclerView recyclerView;
    private MonitorMonitoradoAdapter monitorMonitoradoAdapter;

    // Broadcast Receiver
    private BroadcastReceiver broadcastReceiver;

    // Request codes
    private static int CHAT_RQUEST = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Classe de serviço e lista de monitorado
        monitorMonitoradoService = new MonitorMonitoradoService(getContext());
        monitorMonitoradoList = monitorMonitoradoService.findAll();

        // MainActivity
        mainActivity = (MainActivity) getActivity();

        // Se encontrou apenas um monitoradona lista, abre o chat
        if(monitorMonitoradoList != null && monitorMonitoradoList.size() == 1){
            startChat(0);
        }

        // Broadcast Receiver para remoção do monitorado
        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (monitorMonitoradoAdapter == null) {
                    return;
                }

                if(intent.hasExtra("acao") && intent.getIntExtra("acao", 0) == DataFirebaseTO.ACAO_REMOCAO_MONITORADO && intent.hasExtra("id_monitorado")){
                    monitorMonitoradoAdapter.removerMonitorado(intent.getLongExtra("id_monitorado", 0));
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CollabtrackApplication.MONITORADO_TO_BROADCAST_RECEIVER);
        this.getActivity().registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitorados, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_monitorados);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        monitorMonitoradoAdapter = new MonitorMonitoradoAdapter(getContext(), monitorMonitoradoList, onClickMonitorado());
        recyclerView.setAdapter(monitorMonitoradoAdapter);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(monitorMonitoradoList != null && monitorMonitoradoList.size() == 1){
            mainActivity.changeMenuItem(mainActivity.MENU_ITEM_MAPA);
            GoogleMapFragment mapFragment = new GoogleMapFragment();
            mainActivity.getSupportFragmentManager().beginTransaction().add(R.id.mainFrame, mapFragment, mainActivity.FRAGMENT_TAG_MAPA).commitAllowingStateLoss();
        }

    }

    private MonitorMonitoradoAdapter.MonitoradoOnClickListener onClickMonitorado(){
        return new MonitorMonitoradoAdapter.MonitoradoOnClickListener() {
            @Override
            public void onClickMonitorado(View view, int idx) {
                startChat(idx);
            }
        };
    }

    private void startChat(int idx){
        Intent intent = new Intent(getActivity(), ChatActivity.class);
        intent.putExtra("idMonitorado",             monitorMonitoradoList.get(idx).getMonitorado().getId());
        intent.putExtra("nomeMonitorado",           monitorMonitoradoList.get(idx).getMonitorado().getNome());
        intent.putExtra("quantidadeMonitorados",    monitorMonitoradoList.size());
        startActivityForResult(intent, CHAT_RQUEST);
    }

}
