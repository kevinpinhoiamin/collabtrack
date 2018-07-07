package br.com.senai.colabtrack.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.stream.Collectors;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.R;
import br.com.senai.colabtrack.activity.AreaSeguraActivity;
import br.com.senai.colabtrack.activity.AreasSegurasActivity;
import br.com.senai.colabtrack.domain.AreaSegura;
import br.com.senai.colabtrack.domain.AreaSeguraMonitorado;
import br.com.senai.colabtrack.domain.Localizacao;
import br.com.senai.colabtrack.domain.Monitorado;
import br.com.senai.colabtrack.domain.Status;
import br.com.senai.colabtrack.domain.util.LocalizacaoUtil;
import br.com.senai.colabtrack.domain.util.MonitorMonitoradoUtil;
import br.com.senai.colabtrack.domain.util.StatusUtil;
import br.com.senai.colabtrack.fragment.dialog.FiltrarLocalizacoesDialogFragment;
import br.com.senai.colabtrack.request.BuscarLocalizacaoRequest;
import br.com.senai.colabtrack.request.HttpCallback;
import br.com.senai.colabtrack.service.mapa.MapArea;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.domain.MonitorMonitorado;
import br.com.senai.colabtrack.domain.util.MonitorUtil;
import br.com.senai.colabtrack.service.mapa.MapAreaService;
import br.com.senai.colabtrack.service.mapa.MapMonitorado;
import br.com.senai.colabtrack.service.AreaSeguraMonitoradoService;
import br.com.senai.colabtrack.service.AreaSeguraService;
import br.com.senai.colabtrack.domain.util.AreaSeguraUtil;
import br.com.senai.colabtrack.service.LocalizacaoService;
import br.com.senai.colabtrack.service.MonitorMonitoradoService;
import br.com.senai.colabtrack.service.MonitorService;
import br.com.senai.colabtrack.util.ColorUtil;
import br.com.senai.colabtrack.util.DateUtil;
import br.com.senai.colabtrack.util.JsonUtil;
import br.com.senai.colabtrack.util.Permission;

/**
 * Created by kevin on 24/05/17.
 */

public class GoogleMapFragment extends BaseFragment implements GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraMoveListener, ActivityCompat.OnRequestPermissionsResultCallback {

    // Instância no monitor autenticado
    private Monitor monitor;
    // Instãncia do mapa
    private GoogleMap mMap;
    // Instância da view do mapa
    private MapView mapView;
    // Intância do fragment
    private  GoogleMapFragment thisFragment = this;
    // Classe de serviço
    private LocalizacaoService localizacaoService;
    // Instância da lista de círculos
    private Map<String, MapArea> mapAreas = new HashMap<>();
    private Map<String, MapMonitorado> mapLocalizacoes = new HashMap<>();
    // Lista dos monitorado do monitor
    private List<MonitorMonitorado> monitorMonitorados = new ArrayList<>();
    // Instância da posição da câmara no mapa
    private CameraPosition cameraPosition;
    private float zoom = 13;
    private float bearing = 90;
    private float tilt = 40;
    // Instância da última área segura que foi clicada
    private AreaSegura areaSeguraSelecionada;
    // Instância do status que será mostrado no mapa
    private Status statusMostrar;
    // Item do menu da área segura
    private MenuItem menuItemAreaSegura;
    // Constantes das requisições
    private static final int NOVA_AREA_SEGURA_RQUEST_CODE = 1;
    private static final int LISTAR_AREA_SEGURA_REQUEST_CODE = 2;
    private static final int LOCALIZACAO_REQUEST_CODE = 3;
    private static final int FILTRAR_LOCALIZACOES_DIALOG_REQUEST_CODE = 4;

    private static int DEFAULT_FILTER_PERIODO = 1;
    private static int DEFAULT_FILTER_PONTOS = 50;

    private BroadcastReceiver broadcastReceiver;

    private MonitorMonitoradoService monitorMonitoradoService;

    private RelativeLayout localizacaoLoader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Classe de serviço
        localizacaoService = new LocalizacaoService(getContext());
        monitorMonitoradoService = new MonitorMonitoradoService(getContext());

        Bundle bundle = this.getArguments();
        if(bundle != null && bundle.containsKey("statusMostrar")){
            this.statusMostrar = StatusUtil.writeToStatus(bundle.getParcelable("statusMostrar"));
        }

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("areaSeguraSelecionada")) {
                areaSeguraSelecionada = AreaSeguraUtil.writeToAraSegura(savedInstanceState.getParcelable("areaSeguraSelecionada"));
            }
            if (savedInstanceState.containsKey("zoom")) {
                zoom = savedInstanceState.getFloat("zoom");
            }
            if (savedInstanceState.containsKey("bearing")) {
                bearing = savedInstanceState.getFloat("bearing");
            }
            if (savedInstanceState.containsKey("tilt")) {
                tilt = savedInstanceState.getFloat("tilt");
            }
            monitor = MonitorUtil.writeToMonitor(savedInstanceState.getParcelable("monitor"));
        }

        this.broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent != null && intent.hasExtra("localizacao")) {
                    Localizacao localizacao = LocalizacaoUtil.writeToLocalizacao(intent.getParcelableExtra("localizacao"));
                    mostrarLocalizacao(localizacao, true);
                }

            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ColabTrackApplication.LOCALIZACAO_BROADCAST_RECEIVER);
        this.getActivity().registerReceiver(broadcastReceiver, intentFilter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_google_map, container, false);

        mapView = (MapView) rootView.findViewById(R.id.googleMapView);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this.onMapReady());

        localizacaoLoader = (RelativeLayout) rootView.findViewById(R.id.localizacao_loader);

        MapsInitializer.initialize(getActivity().getApplicationContext());

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);

        if(areaSeguraSelecionada != null){
            bundle.putParcelable("areaSeguraSelecionada", AreaSeguraUtil.writeToParcel(areaSeguraSelecionada));
        }

        if(cameraPosition != null){
            bundle.putFloat("zoom", cameraPosition.zoom);
            bundle.putFloat("bearing", cameraPosition.bearing);
            bundle.putFloat("tilt", cameraPosition.tilt);
        }

        bundle.putParcelable("monitor", MonitorUtil.writeToParcel(monitor));
        
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menuItemAreaSegura = menu.findItem(R.id.nav_item_areas_seguras);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_item_areas_seguras:
                Intent intent = new Intent(getActivity(), AreasSegurasActivity.class);
                startActivityForResult(intent, LISTAR_AREA_SEGURA_REQUEST_CODE);
                break;
            case R.id.nav_item_filtrar:
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("monitorMonitorados", (ArrayList<? extends Parcelable>) MonitorMonitoradoUtil.writeToParcelList(monitorMonitorados));

                FiltrarLocalizacoesDialogFragment fragment = new FiltrarLocalizacoesDialogFragment();
                fragment.setArguments(bundle);
                fragment.setTargetFragment(thisFragment, FILTRAR_LOCALIZACOES_DIALOG_REQUEST_CODE);
                fragment.show(getFragmentManager(), ColabTrackApplication.DEBUG_TAG);

                break;
        }
        return true;
    }

    private OnMapReadyCallback onMapReady(){
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Configurações do mapa
                mMap = googleMap;
                //mMap.setOnMarkerClickListener(thisFragment);
                mMap.setOnCameraMoveListener(thisFragment);

                if(Permission.checkLocationPermission(thisFragment.getActivity())) {
                    mMap.setMyLocationEnabled(true);
                } else {
                    Permission.requestLocationPermission((AppCompatActivity) thisFragment.getActivity(), LOCALIZACAO_REQUEST_CODE);
                }

                new CarregaMonitor().execute();
                //new CarregarLocalizacoes().execute();
            }
        };
    }

    /*
    GoogleMap.OnMapLoadedCallback onMapLoaded() {
        return new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                new CarregarAreasSeguras().execute();
                new CarregaMonitor().execute();
            }
        };
    }
    */

    private void updateLocation(LatLng location, boolean animateCamera){

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude, location.longitude), zoom);
        if(animateCamera){
            mMap.animateCamera(cameraUpdate);
        }else{
            mMap.moveCamera(cameraUpdate);
        }

        cameraPosition = new CameraPosition.Builder()
                .target(location)           // Sets the center of the map to location user
                .zoom(zoom)                 // Sets the zoom
                .bearing(bearing)           // Sets the orientation of the camera to east
                .tilt(tilt)                 // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder

        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    /**
     * Método responsável por exeibir a área segura no mapa
     * @param areaSeguraMostrar Instância da área segura
     */
    private void mostrarArea(AreaSegura areaSeguraMostrar){

        if (mMap == null) {
            return;
        }

        LatLng location = new LatLng(areaSeguraMostrar.getLatitude(), areaSeguraMostrar.getLongitude());

        Circle circle = mMap.addCircle(new CircleOptions()
                .center(location)
                .radius(areaSeguraMostrar.getRaio())
                .strokeWidth(10)
                .fillColor(ColorUtil.getOpacityColor(areaSeguraMostrar.getCor()))
                .strokeColor(areaSeguraMostrar.getCorBorda()));

        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_flag))
                .title(areaSeguraMostrar.getNome()));

        // Busca os dados de realção da área segura com o monitorado
        AreaSeguraMonitoradoService areaSeguraMonitoradoService = new AreaSeguraMonitoradoService(getContext());
        List<AreaSeguraMonitorado> areaSeguraMonitorados = areaSeguraMonitoradoService.findByAreaSegura(areaSeguraMostrar.getId());

        MapArea mapArea = new MapArea(circle, marker, areaSeguraMostrar, areaSeguraMonitorados);

        // Adiciona a área segura na lista
        mapAreas.put(circle.getId(), mapArea);

    }

    /**
     * Método responsável por mostrar a localização do monitorado no mapa
     * @param localizacao Instância da localização
     * @param ultimaLocalizacao Define se é a última localização do monitorado, se sim adicionar um marcador
     */
    private void mostrarLocalizacao(Localizacao localizacao, boolean ultimaLocalizacao){

        if (mMap == null) {
            return;
        }

        LatLng latLng = new LatLng(localizacao.getLatitude(), localizacao.getLongitude());

        // Monta o objeto MapMonitorado
        MapMonitorado mapMonitorado = new MapMonitorado();
        mapMonitorado.setLocalizacao(localizacao);
        //MapAreaService.adicionarCorMapMonitorado(mapMonitorado, mapLocalizacoes);

        // MonitorMonitorado
        MonitorMonitorado monitorMonitorado = monitorMonitoradoService.find(monitor.getId(), localizacao.getMonitorado().getId());

        // Defina a cor que será utilizada na localização
        int color = ColorUtil.getColor(monitorMonitorado.getCor());

        Circle circle = mMap.addCircle(new CircleOptions()
                        .center(latLng)
                        .radius(4)
                        .fillColor(color)
                        .strokeColor(color));

        mapMonitorado.setCircle(circle);

        // Adiciona o marcador
        if(ultimaLocalizacao){

            // Remove os markers de todas as localizações menos da última
            MapAreaService.removerMarkers(mapMonitorado, mapLocalizacoes, localizacao);

            // Adicionar o marker na últime localização
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_smile))
                    .title(localizacao.getMonitorado().getNome() + " | " + DateUtil.fromAPITODescribed(localizacao.getData()))
            );

            mapMonitorado.setMarker(marker);

        }

        // Adiciona a licalização na lista de localizações
        mapLocalizacoes.put(circle.getId(), mapMonitorado);

    }

    private void removerLocalizacoes() {

        if (mapLocalizacoes != null && mapLocalizacoes.size() > 0) {
            mapLocalizacoes = new HashMap<>();
            mMap.clear();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode == LOCALIZACAO_REQUEST_CODE && Permission.checkLocationPermission(permissions, grantResults)) {
            mMap.setMyLocationEnabled(true);
        }

    }

    @Override
    public void onMapLongClick(LatLng location) {
        Intent intent = new Intent(getActivity(), AreaSeguraActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("latitude", location.latitude);
        bundle.putDouble("longitude", location.longitude);
        intent.putExtras(bundle);
        startActivityForResult(intent, NOVA_AREA_SEGURA_RQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(data == null || resultCode != getActivity().RESULT_OK){
            return;
        }

        Bundle bundle = data.getExtras();

        // Mostra a área segura que foi cadastrada
        if(requestCode == NOVA_AREA_SEGURA_RQUEST_CODE){
            AreaSegura areaSegura = AreaSeguraUtil.writeToAraSegura(bundle.getParcelable("areaSegura"));
            mostrarArea(areaSegura);
            navegarParaAreaSegura(areaSegura, false);
        }else if(requestCode == LISTAR_AREA_SEGURA_REQUEST_CODE){
            List<AreaSegura> areasSegurasRemovidas = AreaSeguraUtil.writeToAreaSeguraList(bundle.getParcelableArrayList("areasSegurasRemovidas"));
            List<AreaSegura> areaSegurasEditadas = AreaSeguraUtil.writeToAreaSeguraList(bundle.getParcelableArrayList("areasSegurasEditadas"));
            AreaSegura areaSeguraMostrar = AreaSeguraUtil.writeToAraSegura(bundle.getParcelable("areaSeguraMostrar"));
            mapAreas = MapAreaService.removerAreasSeguras(areasSegurasRemovidas, mapAreas);
            mapAreas = MapAreaService.editarAreasSeguras(areaSegurasEditadas, mapAreas);
            navegarParaAreaSegura(areaSeguraMostrar, true);
        } else if (requestCode == FILTRAR_LOCALIZACOES_DIALOG_REQUEST_CODE) {
            long[] monitorados = bundle.getLongArray("monitorados");

            int periodo = bundle.getInt("periodo");
            int pontos = bundle.getInt("pontos");
            buscarLocalizacoes(monitorados, periodo, pontos, true);
        }

    }

    /*
    @Override
    public boolean onMarkerClick(Marker marker) {
        for (MapArea mapArea : mapAreas.values()){
            if(mapArea.getMarker().getId().equals(marker.getId())){
                areaSeguraSelecionada = mapArea.getAreaSegura();
            }
        }
        return false;
    }
    */

    @Override
    public void onCameraMove() {
        cameraPosition = mMap.getCameraPosition();
    }

    /**
     * Carrega as áreas seguras que estão na base de dados do celular
     */
    private class CarregarAreasSeguras extends AsyncTask<Void, Void, List<AreaSegura>>{

        // Instância da classe de serviço
        private AreaSeguraService areaSeguraService;
        private CarregarAreasSeguras(){
            areaSeguraService = new AreaSeguraService(getActivity().getApplicationContext());
        }

        @Override
        protected List<AreaSegura> doInBackground(Void... params) {
            return areaSeguraService.findAll();
        }

        @Override
        protected void onPostExecute(List<AreaSegura> areaSeguras) {
            if(areaSeguras != null && areaSeguras.size() > 0){
                for (AreaSegura areaSegura : areaSeguras){
                    mostrarArea(areaSegura);
                }

                // Atualiza a localização para a primeira área segura da lista
                if(mapLocalizacoes == null || mapLocalizacoes.size() == 0) {
                    navegarParaAreaSegura(areaSeguraSelecionada == null ? areaSeguras.get(0) : areaSeguraSelecionada, areaSeguraSelecionada == null ? true : false);
                }

            }
        }

    }

    /**
     * Método responsável por alterar a posição do mapa para uma das áreas seguras e mostras suas informações
     * @param areaSegura Área segura que será mostrada
     */
    private void navegarParaAreaSegura(AreaSegura areaSegura, boolean animateCamera){

        if(areaSegura instanceof AreaSegura){

            for (MapArea mapArea : mapAreas.values()){
                if(mapArea.getAreaSegura().getId() == areaSegura.getId()){
                    Marker marker = mapArea.getMarker();
                    updateLocation(marker.getPosition(), animateCamera);
                    marker.showInfoWindow();
                    areaSeguraSelecionada = areaSegura;
                }
            }

        }

    }

    private void navegarParaUltimaLocalizacao(){

        if(mapLocalizacoes != null && mapLocalizacoes.size() > 0){

            long ultimoID = 0;
            MapMonitorado ultimoMapMonitorado = null;
            for(MapMonitorado mapMonitoradoIteracao : mapLocalizacoes.values()) {

                if(statusMostrar != null && statusMostrar.getMonitorado() != null && statusMostrar.getMonitorado().getId() == mapMonitoradoIteracao.getLocalizacao().getMonitorado().getId()){

                    if (mapMonitoradoIteracao.getLocalizacao().getId() > ultimoID) {
                        ultimoID = mapMonitoradoIteracao.getLocalizacao().getId();
                        ultimoMapMonitorado = mapMonitoradoIteracao;
                    }

                }else if (statusMostrar == null && mapMonitoradoIteracao.getLocalizacao().getId() > ultimoID) {
                    ultimoID = mapMonitoradoIteracao.getLocalizacao().getId();
                    ultimoMapMonitorado = mapMonitoradoIteracao;
                }

            }

            if(ultimoMapMonitorado != null){

                updateLocation(new LatLng(ultimoMapMonitorado.getLocalizacao().getLatitude(), ultimoMapMonitorado.getLocalizacao().getLongitude()), true);
                Marker marker = ultimoMapMonitorado.getMarker();
                if(marker != null) {
                    marker.showInfoWindow();
                }

            }

        }

    }

    /**
     * Classe responsável por carregar os dados do monitor autenticado
     */
    private class CarregaMonitor extends AsyncTask<Void, Void, Monitor>{

        // Instância da classe de serviço
        private MonitorService monitorService;
        private CarregaMonitor(){
            this.monitorService = new MonitorService(getActivity().getApplicationContext());
        }

        @Override
        protected Monitor doInBackground(Void... params) {
            return monitorService.findAutenticado();
        }

        @Override
        protected void onPostExecute(Monitor monitorAutenticado) {
            monitor = monitorAutenticado;
            new CarregaMonitorados().execute();
        }

    }

    /**
     * Classe responsável por buscar a associação entre os monitorados e o monitor autenticado
     */
    private class CarregaMonitorados extends AsyncTask<Void, Void, List<MonitorMonitorado>>{

        private MonitorMonitoradoService monitorMonitoradoService;
        private CarregaMonitorados(){
            this.monitorMonitoradoService = new MonitorMonitoradoService((getActivity().getApplicationContext()));
        }

        @Override
        protected List<MonitorMonitorado> doInBackground(Void... params) {
            return monitorMonitoradoService.findAll();
        }

        @Override
        protected void onPostExecute(List<MonitorMonitorado> monitorMonitoradosList) {

            List<Long> monitorados = new ArrayList<>();
            monitorMonitorados = monitorMonitoradosList;

            boolean hasPrincipal = false;
            ListIterator iterator = monitorMonitorados.listIterator();
            while (iterator.hasNext()){
                MonitorMonitorado monitorMonitorado = (MonitorMonitorado) iterator.next();
                if(monitorMonitorado.isPrincipal()){
                    hasPrincipal = true;
                }
                monitorados.add(monitorMonitorado.getMonitorado().getId());
            }


            if(hasPrincipal){
                mMap.setOnMapLongClickListener(thisFragment);
            }else {
                menuItemAreaSegura.setVisible(false);
            }

            if (statusMostrar != null) {
                DEFAULT_FILTER_PERIODO = 5;
                monitorados = new ArrayList<>();
                monitorados.add(statusMostrar.getMonitorado().getId());
            }

            buscarLocalizacoes(monitorados, DEFAULT_FILTER_PERIODO, DEFAULT_FILTER_PONTOS, false);

        }
    }

    private void  buscarLocalizacoes(List<Long> monitorados, int periodo, int pontos, boolean mensagemDeValidacao) {

        long[] monitoradosArray = new long[monitorados.size()];
        for (int i = 0; i < monitorados.size(); i++){
            monitoradosArray[i] = monitorados.get(i);
        }

        buscarLocalizacoes(monitoradosArray, periodo, pontos, mensagemDeValidacao);

    }

    private void buscarLocalizacoes(long[] monitorados, final int periodo, final int pontos, final boolean mensagemDeValidacao) {

        localizacaoLoader.setVisibility(View.VISIBLE);

        new BuscarLocalizacaoRequest(monitorados, periodo, pontos, new HttpCallback() {
            @Override
            public void onSuccess(String response) {

                // Limpa o mapa
                removerLocalizacoes();

                List<Localizacao> localizacaoList1 = JsonUtil.toObjectList(response, Localizacao[].class);
                List<Localizacao> localizacaoList2 = localizacaoService.findAll(localizacaoList1, periodo, pontos);
                if ((localizacaoList1 != null && localizacaoList1.size() > 0) || (localizacaoList2 != null && localizacaoList2.size() > 0)) {

                    mapLocalizacoes = new HashMap<>();

                    // Parse das localizações
                    List<Localizacao> localizacaoList = new ArrayList<>();
                    List<Localizacao> localizacaoList3 = new ArrayList<>();

                    // Adiciona todas as localizações em uma única lista
                    localizacaoList3.addAll(new ArrayList<>(localizacaoList1));
                    localizacaoList3.addAll(new ArrayList<>(localizacaoList2));

                    // Filtra as localizações repetidas
                    if (localizacaoList3.size() > 0) {

                        List<Long> localizacoesAdicionadas = new ArrayList<>();

                        for(Localizacao localizacao1 : localizacaoList3) {
                            for(Localizacao localizacao2 : localizacaoList3) {
                                if(localizacao1.getId() == localizacao2.getId()) {
                                    if (!localizacoesAdicionadas.contains(localizacao1.getId())) {
                                        localizacoesAdicionadas.add(localizacao1.getId());
                                        localizacaoList.add(localizacao1);
                                    }
                                }
                            }
                        }

                    }

                    if(localizacaoList != null && localizacaoList.size() > 0) {
                        for (int i = 0; i < localizacaoList.size(); i++) {
                            mostrarLocalizacao(localizacaoList.get(i), MapAreaService.ehUltimaLocalizacao(localizacaoList.get(i), localizacaoList));
                        }
                        navegarParaUltimaLocalizacao();
                    }

                } else if (mensagemDeValidacao){
                    Toast.makeText(ColabTrackApplication.getContext(), getString(R.string.nenhuma_localizaca_encontrada), Toast.LENGTH_LONG).show();
                }

                localizacaoLoader.setVisibility(View.INVISIBLE);
                new CarregarAreasSeguras().execute();

            }

            @Override
            public void onError(VolleyError error) {
                localizacaoLoader.setVisibility(View.INVISIBLE);
            }
        }).execute();

    }

}