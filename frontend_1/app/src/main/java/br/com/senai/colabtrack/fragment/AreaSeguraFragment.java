package br.com.senai.colabtrack.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import br.com.senai.colabtrack.ColabTrackApplication;
import br.com.senai.colabtrack.R;
import br.com.senai.colabtrack.activity.AreaSeguraActivity;
import br.com.senai.colabtrack.adapter.AreaSeguraMonitoradoAdapter;
import br.com.senai.colabtrack.domain.AreaSegura;
import br.com.senai.colabtrack.domain.AreaSeguraMonitorado;
import br.com.senai.colabtrack.domain.Monitor;
import br.com.senai.colabtrack.domain.Monitorado;
import br.com.senai.colabtrack.domain.to.AreaSeguraTO;
import br.com.senai.colabtrack.domain.util.AreaSeguraMonitoradoUtil;
import br.com.senai.colabtrack.request.HttpCallback;
import br.com.senai.colabtrack.service.AreaSeguraMonitoradoService;
import br.com.senai.colabtrack.service.AreaSeguraService;
import br.com.senai.colabtrack.service.MonitorService;
import br.com.senai.colabtrack.service.MonitoradoService;
import br.com.senai.colabtrack.domain.util.AreaSeguraUtil;
import br.com.senai.colabtrack.util.ColorUtil;
import br.com.senai.colabtrack.util.HttpUtil;
import br.com.senai.colabtrack.util.JsonUtil;
import petrov.kristiyan.colorpicker.ColorPicker;

/**
 * Created by kevin on 07/07/17.
 */

public class AreaSeguraFragment extends BaseFragment {

    private TextInputLayout textInputLayoutNome;
    private EditText editTextNome;
    private SeekBar seekBarRaio;
    private ImageView imageViewCorDisabled;
    private ImageView imageViewCor;
    private ImageView imageViewCorBordaDisabled;
    private ImageView imageViewCorBorda;
    private ProgressBar progressBarCadastrar;
    private Button buttonCadastrar;
    private TextView textViewRaioValor;
    private RecyclerView recyclerViewMonitorados;
    private int imageViewCorSelecionada;
    private int imageViewCorBordaSelecionada;

    private AreaSegura areaSegura;
    private List<AreaSeguraMonitorado> areaSeguraMonitorados;
    private AreaSeguraMonitoradoAdapter areaSeguraMonitoradoAdapter;

    private AreaSeguraMonitoradoAdapter.AreaSeguraMonitoradoOnCheckedChangeListener areaSeguraMonitoradoOnCheckedChangeListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup viewGroup, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_area_segura, viewGroup, false);

        // Objetos do layout
        textInputLayoutNome =       (TextInputLayout)   view.findViewById(R.id.text_input_layout_nome);
        editTextNome =              (EditText)          view.findViewById(R.id.edit_text_nome);
        seekBarRaio =               (SeekBar)           view.findViewById(R.id.seek_bar_raio);
        imageViewCorDisabled =      (ImageView)         view.findViewById(R.id.image_view_cor_disabled);
        imageViewCor =              (ImageView)         view.findViewById(R.id.image_view_cor);
        imageViewCorBordaDisabled = (ImageView)         view.findViewById(R.id.image_view_cor_borda_disabled);
        imageViewCorBorda =         (ImageView)         view.findViewById(R.id.image_view_cor_borda);
        progressBarCadastrar =      (ProgressBar)       view.findViewById(R.id.progress_bar_button_cadastrar);
        buttonCadastrar =           (Button)            view.findViewById(R.id.button_cadastrar);
        textViewRaioValor =         (TextView)          view.findViewById(R.id.text_view_raio_valor);

        // Dados passados pelo bundle
        Intent intent = getActivity().getIntent();
        Bundle bundle = intent.getExtras();
        if(intent.hasExtra("areaSegura")){
            areaSegura = AreaSeguraUtil.writeToAraSegura(bundle.getParcelable("areaSegura"));
            editTextNome.setText(areaSegura.getNome());
            seekBarRaio.setProgress(areaSegura.getRaio() / 100);
            imageViewCorSelecionada = areaSegura.getCor();
            imageViewCorBordaSelecionada = areaSegura.getCorBorda();
            buttonCadastrar.setText(R.string.editar);
        }else if(intent.hasExtra("latitude") && intent.hasExtra("longitude")){
            areaSegura = new AreaSegura();
            areaSegura.setLatitude(bundle.getDouble("latitude"));
            areaSegura.setLongitude(bundle.getDouble("longitude"));
        }

        // Texto padrão do raio
        textViewRaioValor.setText((areaSegura.getRaio() > 0 ? areaSegura.getRaio() : 100) + " " + getString(R.string.metros).toLowerCase());

        // Evento do seek bar
        seekBarRaio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeProgress(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Cores padões do color picker
        if(savedInstanceState == null && imageViewCorSelecionada == 0 && imageViewCorBordaSelecionada == 0){
            imageViewCorSelecionada = ColabTrackApplication.COLOR_PICKER_DEFAULT_COLOR;
            imageViewCorBordaSelecionada = ColabTrackApplication.COLOR_PICKER_DEFAULT_BORDER_COLOR;
        }else if(savedInstanceState != null && savedInstanceState.containsKey("imageViewCorSelecionada") && savedInstanceState.containsKey("imageViewCorBordaSelecionada")){
            imageViewCorSelecionada = savedInstanceState.getInt("imageViewCorSelecionada");
            imageViewCorBordaSelecionada = savedInstanceState.getInt("imageViewCorBordaSelecionada");
            areaSeguraMonitorados = AreaSeguraMonitoradoUtil.writeToAreaSeguraMonitoradoList(savedInstanceState.getParcelableArrayList("areaSeguraMonitorados"));

        }
        imageViewCor.setBackgroundColor(ColorUtil.getColor(imageViewCorSelecionada));
        imageViewCorBorda.setBackgroundColor(ColorUtil.getColor(imageViewCorBordaSelecionada));

        // Iniciado o color picker
        imageViewCor.setOnClickListener(onClickShowColorPicker(imageViewCor));
        imageViewCorBorda.setOnClickListener(onClickShowColorPicker(imageViewCorBorda));

        // Submit do formulário
        buttonCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                areaSegura.setNome(editTextNome.getText().toString());
                areaSegura.setCor(imageViewCorSelecionada);
                areaSegura.setCorBorda(imageViewCorBordaSelecionada);
                areaSegura.setRaio(seekBarRaio.getProgress() * 100);

                if(areaSegura.getNome().equals("")){
                    textInputLayoutNome.setError(getString(R.string.campo_obrigatorio));
                    return;
                }else{
                    textInputLayoutNome.setErrorEnabled(false);
                    textInputLayoutNome.setError(null);
                }

                desabilitarFormulario();

                if(areaSegura.getId() == 0){
                    new SalvarAreaSegura().execute();
                }else{
                    new EditarAreaSegura().execute();
                }

            }
        });

        // Evento responsável por ativar / inativar o monitorado
        areaSeguraMonitoradoOnCheckedChangeListener = new AreaSeguraMonitoradoAdapter.AreaSeguraMonitoradoOnCheckedChangeListener() {
            @Override
            public void onCheckedChangeAreaSeguraMonitorado(boolean isChecked, int position) {
                AreaSeguraMonitorado areaSeguraMonitorado = areaSeguraMonitorados.get(position);
                areaSeguraMonitorado.setAtiva(isChecked);
                areaSeguraMonitorados.set(position, areaSeguraMonitorado);
            }
        };

        // Buscando os monitorados
        if(savedInstanceState == null){
            // RecyclerView
            recyclerViewMonitorados = (RecyclerView) view.findViewById(R.id.recycler_view_areas_seguras_monitorados);
            recyclerViewMonitorados.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerViewMonitorados.setItemAnimator(new DefaultItemAnimator());
            recyclerViewMonitorados.setHasFixedSize(true);
            if(areaSegura.getId() == 0){
                areaSeguraMonitorados = new ArrayList<>();
                new BuscarMonitoradosSalvar(false).execute();
            }else{
                new BuscarMonitoradosEditar().execute();
            }
        }else{
            areaSeguraMonitoradoAdapter = new AreaSeguraMonitoradoAdapter(getContext(), areaSeguraMonitorados, areaSeguraMonitoradoOnCheckedChangeListener);
            recyclerViewMonitorados.setAdapter(areaSeguraMonitoradoAdapter);
        }

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finalizar();
                break;
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle savedIstanceState) {
        super.onSaveInstanceState(savedIstanceState);

        // Salva as cores selecionadas para não perder ao girar a tela
        savedIstanceState.putInt("imageViewCorSelecionada", imageViewCorSelecionada);
        savedIstanceState.putInt("imageViewCorBordaSelecionada", imageViewCorBordaSelecionada);
        savedIstanceState.putParcelableArrayList("areaSeguraMonitorados", (ArrayList<? extends Parcelable>) AreaSeguraMonitoradoUtil.writeToParcelList(areaSeguraMonitorados));
    }

    /**
     * Método responsável por alterar o valor do progresso no text view e impedir que o progresso seja menor que 1
     * @param progress Valor do progresso no seek bar
     */
    private void changeProgress(int progress){
        if(progress < 1){
            seekBarRaio.setProgress(1);
        }

        int metros = seekBarRaio.getProgress() * 100;
        String raio = metros < 1000 ? metros + " " + getString(R.string.metros).toLowerCase() : metros / 1000 + " " + getString(R.string.km);
        textViewRaioValor.setText(raio);
    }

    /**
     * Método responsável por desabilitar fo formulário e mostrar o loader
     */
    private void desabilitarFormulario(){
        editTextNome.setEnabled(false);
        editTextNome.setFocusable(false);
        editTextNome.setFocusableInTouchMode(false);
        imageViewCorDisabled.setVisibility(View.VISIBLE);
        imageViewCor.setOnClickListener(null);
        imageViewCorBordaDisabled.setVisibility(View.VISIBLE);
        imageViewCorBorda.setOnClickListener(null);
        seekBarRaio.setEnabled(false);
        seekBarRaio.setFocusable(false);
        seekBarRaio.setFocusableInTouchMode(false);
        buttonCadastrar.setEnabled(false);
        buttonCadastrar.setText("");
        progressBarCadastrar.setVisibility(View.VISIBLE);
    }

    /**
     * Método responsável por habilitar o formulário e esconder o loader
     */
    private void habilitarFormulario(){
        editTextNome.setEnabled(true);
        editTextNome.setFocusable(true);
        editTextNome.setFocusableInTouchMode(true);
        imageViewCorDisabled.setVisibility(View.GONE);
        imageViewCor.setOnClickListener(onClickShowColorPicker(imageViewCor));
        imageViewCorBordaDisabled.setVisibility(View.GONE);
        imageViewCorBorda.setOnClickListener(onClickShowColorPicker(imageViewCorBorda));
        seekBarRaio.setEnabled(true);
        seekBarRaio.setFocusable(true);
        seekBarRaio.setFocusableInTouchMode(true);
        buttonCadastrar.setEnabled(true);
        buttonCadastrar.setText(getString(R.string.cadastrar));
        progressBarCadastrar.setVisibility(View.GONE);
    }

    /**
     * Método responsável por criar e mostrar o color picker
     * @param view // View que terá a cor de fundo trocada pela cor selecionada no picker
     * @return Evento de clique
     */
    private View.OnClickListener onClickShowColorPicker(final ImageView view){

        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Instância
                ColorPicker colorPicker = new ColorPicker(getContext());
                // Título
                colorPicker.setTitle(getString(R.string.selecione_uma_cor));
                // Cor padrão selecionada
                if(view.getId() == R.id.image_view_cor){
                    colorPicker.setDefaultColorButton(imageViewCorSelecionada);
                }else if(view.getId() == R.id.image_view_cor_borda){
                    colorPicker.setDefaultColorButton(imageViewCorBordaSelecionada);
                }
                // Evento de clique em uma das cores
                colorPicker.setOnFastChooseColorListener(new ColorPicker.OnFastChooseColorListener() {
                    @Override
                    public void setOnFastChooseColorListener(int position, int color) {
                        view.setBackgroundColor(color);
                        if(view.getId() == R.id.image_view_cor){
                            imageViewCorSelecionada = color;
                        }else if(view.getId() == R.id.image_view_cor_borda){
                            imageViewCorBordaSelecionada = color;
                        }
                    }

                    @Override
                    public void onCancel() {

                    }
                });
                colorPicker.show();

            }
        };

    }

    /**
     * Classe responsável por buscar os monitorados da base de dados local
     */
    private class BuscarMonitoradosSalvar extends AsyncTask<Void, Void, List<Monitorado>>{

        private boolean adicionar;
        private MonitoradoService monitoradoService;

        public BuscarMonitoradosSalvar(boolean adicionar){
            this.adicionar = adicionar;
            this.monitoradoService = new MonitoradoService(getActivity().getApplicationContext());
        }

        @Override
        protected List<Monitorado> doInBackground(Void... params) {
            return  monitoradoService.findPrincipal();
        }

        @Override
        protected void onPostExecute(List<Monitorado> monitoradoList) {

            List<AreaSeguraMonitorado> areaSeguraMonitoradosEditar = new ArrayList<>();
            for(Monitorado monitorado : monitoradoList){
                AreaSeguraMonitorado areaSeguraMonitorado = new AreaSeguraMonitorado();
                areaSeguraMonitorado.setAreaSegura(areaSegura);
                areaSeguraMonitorado.setMonitorado(monitorado);
                areaSeguraMonitorado.setAtiva(!adicionar);
                if (!adicionar) {
                    areaSeguraMonitorados.add(areaSeguraMonitorado);
                } else {
                    areaSeguraMonitoradosEditar.add(areaSeguraMonitorado);
                }
            }

            if (!adicionar) {
                areaSeguraMonitoradoAdapter = new AreaSeguraMonitoradoAdapter(getContext(), areaSeguraMonitorados, areaSeguraMonitoradoOnCheckedChangeListener);
                recyclerViewMonitorados.setAdapter(areaSeguraMonitoradoAdapter);
            } else {
                areaSeguraMonitoradoAdapter.adicionarMonitorados(areaSeguraMonitoradosEditar);
            }

        }
    }

    private class BuscarMonitoradosEditar extends AsyncTask<Void, Void, List<AreaSeguraMonitorado>>{

        private AreaSeguraMonitoradoService areaSeguraMonitoradoService;

        public BuscarMonitoradosEditar(){
            areaSeguraMonitoradoService = new AreaSeguraMonitoradoService(getActivity().getApplicationContext());
        }

        @Override
        protected List<AreaSeguraMonitorado> doInBackground(Void... params) {
            return areaSeguraMonitoradoService.findPrincipalByAreaSegura(areaSegura.getId());
        }

        @Override
        protected void onPostExecute(List<AreaSeguraMonitorado> areaSeguraMonitoradoList) {

            areaSeguraMonitorados = areaSeguraMonitoradoList;

            areaSeguraMonitoradoAdapter = new AreaSeguraMonitoradoAdapter(getContext(), areaSeguraMonitorados, areaSeguraMonitoradoOnCheckedChangeListener);
            recyclerViewMonitorados.setAdapter(areaSeguraMonitoradoAdapter);

            new BuscarMonitoradosSalvar(true).execute();
        }
    }

    /**
     * Classe responsável por fazer o cadastro de uma área segura
     */
    private class SalvarAreaSegura extends AsyncTask<Void, Void, Void> {

        private AreaSeguraTO areaSeguraTO;
        private MonitorService monitorService;
        private Monitor monitor;
        private SalvarAreaSegura(){
            monitorService = new MonitorService(getActivity().getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... params) {

            monitor = monitorService.findAutenticado();

            HttpUtil.post(JsonUtil.toJson(new AreaSeguraTO(areaSegura, monitor, areaSeguraMonitorados)), ColabTrackApplication.API_PATH_AREA_SEGURA, new HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    areaSeguraTO = (AreaSeguraTO) JsonUtil.toObject(response, AreaSeguraTO.class);
                    salvarAreaSegura();
                }

                @Override
                public void onError(VolleyError error) {
                    habilitarFormulario();
                }
            });

            return null;
        }

        /**
         * Método responsável por efetuar o cadastro da área segura na base de dados local
         */
        private void salvarAreaSegura(){
            // Serviço
            AreaSeguraMonitoradoService areaSeguraMonitoradoService = new AreaSeguraMonitoradoService(getActivity().getApplicationContext());
            AreaSeguraService areaSeguraService = new AreaSeguraService(getActivity().getApplicationContext());

            // Cadastro da área segura
            areaSeguraService.save(areaSeguraTO.getAreaSegura());
            // Pega a área segura com o ID que foi inserida
            areaSegura = areaSeguraTO.getAreaSegura();

            // Iteração para efetuar os cadastros da relação
            for (AreaSeguraMonitorado areaSeguraMonitorado : areaSeguraTO.getAreaSeguraMonitorados()){
                // Relação entre área segura e monitorado
                areaSeguraMonitoradoService.save(areaSeguraMonitorado);
            }

            finalizar();
        }

    }

    /**
     * Classe responsável por fazer a edição de uma área segura
     */
    private class EditarAreaSegura extends AsyncTask<Void, Void, Void>{

        private AreaSeguraTO areaSeguraTO;
        private MonitorService monitorService;
        private Monitor monitor;
        private EditarAreaSegura(){
            monitorService = new MonitorService(getActivity().getApplicationContext());
        }

        @Override
        protected Void doInBackground(Void... params) {

            monitor = monitorService.findAutenticado();

            HttpUtil.put(JsonUtil.toJson(new AreaSeguraTO(areaSegura, monitor, areaSeguraMonitorados)), ColabTrackApplication.API_PATH_AREA_SEGURA, new HttpCallback() {
                @Override
                public void onSuccess(String response) {
                    areaSeguraTO = (AreaSeguraTO) JsonUtil.toObject(response, AreaSeguraTO.class);
                    editarAreaSegura();
                }

                @Override
                public void onError(VolleyError error) {
                    habilitarFormulario();
                }
            });

            return null;
        }

        /**
         * Método responsável por efetuar a edição da área segura na base de dados local
         */
        private void editarAreaSegura(){
            // Serviço
            AreaSeguraMonitoradoService areaSeguraMonitoradoService = new AreaSeguraMonitoradoService(getActivity().getApplicationContext());
            AreaSeguraService areaSeguraService = new AreaSeguraService(getActivity().getApplicationContext());

            // Edição da área segura
            areaSeguraService.update(areaSeguraTO.getAreaSegura());

            // Exclui a relação entre área segura e monitorados
            areaSeguraMonitoradoService.delete(areaSeguraTO.getAreaSegura().getId());
            // Iteração para efetuar os cadastros da relação
            for (AreaSeguraMonitorado areaSeguraMonitorado : areaSeguraTO.getAreaSeguraMonitorados()){
                // Relação entre área segura e monitorado
                areaSeguraMonitoradoService.save(areaSeguraMonitorado);
            }

            finalizar();
        }

    }

    /**
     * Método responsável por finalizar a activity, passando a área segura cadastrada/editada para apresentar na lista
     */
    private void finalizar(){

        ((AreaSeguraActivity) getActivity()).hideKeyboard();

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("areaSegura", AreaSeguraUtil.writeToParcel(areaSegura));
        getActivity().setResult(Activity.RESULT_OK, intent);
        intent.putExtras(bundle);
        getActivity().finish();
    }

}
