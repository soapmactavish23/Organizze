package com.example.organizze.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.organizze.R;
import com.example.organizze.config.ConfiguracaoFirebase;
import com.example.organizze.helper.Base64Custom;
import com.example.organizze.helper.DataCustom;
import com.example.organizze.model.Movimentacao;
import com.example.organizze.model.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DespesasActivity extends AppCompatActivity {

    private TextInputEditText txtData, txtCategoria, txtDescricao;
    private EditText txtValor;
    private Movimentacao movimentacao;
    private DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
    private FirebaseAuth autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
    private Double despesaTotal;
    private Double despesaGerada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_despesas);

        txtValor = findViewById(R.id.editValor);
        txtCategoria = findViewById(R.id.editCategoria);
        txtData = findViewById(R.id.editData);
        txtDescricao = findViewById(R.id.editDescricao);

        //Recuperar a data atual
        txtData.setText(DataCustom.dataAtual());

        recuperarDespesaTotal();

    }

    public void salvarDespesa(View view){
        if(validarCamposDespesa()){
            movimentacao = new Movimentacao();
            movimentacao.setValor(Double.parseDouble(txtValor.getText().toString()));
            movimentacao.setCategoria(txtCategoria.getText().toString());
            movimentacao.setData(txtData.getText().toString());
            movimentacao.setDescricao(txtDescricao.getText().toString());
            movimentacao.setTipo("d");

            despesaGerada = movimentacao.getValor();
            Double despesaAtualizada = despesaTotal + despesaGerada;
            atualizarDespesa( despesaAtualizada );

            movimentacao.salvar(movimentacao.getData());
            finish();
        }else{
            Toast.makeText(getApplicationContext(), "Preencha todos os campos", Toast.LENGTH_SHORT).show();
        }
    }

    public Boolean validarCamposDespesa(){

        String valor = txtValor.getText().toString();
        String data = txtData.getText().toString();
        String categoria = txtCategoria.getText().toString();
        String descricao = txtDescricao.getText().toString();

        if(valor.isEmpty() || data.isEmpty() || categoria.isEmpty() || descricao.isEmpty()){
            return false;
        }else{
            return true;
        }
    }

    public void recuperarDespesaTotal(){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.condificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);

        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Usuario usuario = dataSnapshot.getValue(Usuario.class);
                despesaTotal = usuario.getDespesaTotal();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void atualizarDespesa(Double despesa){
        String emailUsuario = autenticacao.getCurrentUser().getEmail();
        String idUsuario = Base64Custom.condificarBase64(emailUsuario);
        DatabaseReference usuarioRef = firebaseRef.child("usuarios").child(idUsuario);
        usuarioRef.child("despesaTotal").setValue(despesa);
    }

}
