package br.senai.collabtrack.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import br.senai.collabtrack.CollabtrackApplication;
import br.senai.collabtrack.R;
import br.senai.collabtrack.domain.Monitor;
import br.senai.collabtrack.service.MonitorService;

public class HomeActivity extends AppCompatActivity {

    private HomeActivity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        thisActivity = this;

        verificaAutenticacao();

        Button buttonCadastrar = (Button) findViewById(R.id.button_cadastrar);
        buttonCadastrar.setOnClickListener(cadastrar());

        Button buttonEntrar = (Button) findViewById(R.id.button_entrar);
        buttonEntrar.setOnClickListener(entrar());
    }

    /**
     * Método responsável pelo evento de clique do botão de increva-se
     * @return Instância do evento de clique
     */
    private View.OnClickListener cadastrar(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(thisActivity, SignUpActivity.class));
            }
        };
    }

    /**
     * Método responsável pelo evento de clique do botão entrar
     * @return Instância do evento de clique
     */
    private View.OnClickListener entrar(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(thisActivity, SignInActivity.class));
            }
        };
    }

    /**
     * Método que verifica se o monitor já está autenticado
     */
    private void verificaAutenticacao(){
        MonitorService service = new MonitorService(CollabtrackApplication.getContext());
        Monitor monitor = service.findAutenticado();
        if(monitor != null){
            Intent intent = new Intent(thisActivity, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

}
