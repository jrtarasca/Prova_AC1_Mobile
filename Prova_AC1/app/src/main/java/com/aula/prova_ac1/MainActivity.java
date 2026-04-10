package com.aula.prova_ac1;


import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText etNome, etTelefone, etEmail, etCidade, etBusca;
    private Spinner spinnerCategoria, spinnerFiltro;
    private Switch switchFavorito;
    private Button btnSalvar, btnLimpar, btnMostrarFavoritos;
    private ListView listViewContatos;

    private Databasehelper db;
    private ContatoAdapter adapter;
    private List<Contato> listaContatos;
    private Contato contatoEmEdicao = null;
    private boolean mostrаndоFavoritos = false;

    private String[] categorias = {"Família", "Amigos", "Trabalho", "Outros"};
    private String[] categoriasFiltro = {"Todas", "Família", "Amigos", "Trabalho", "Outros"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new Databasehelper(this);

        inicializarViews();

        configurarSpinners();

        carregarTodosContatos();

        configurarEventos();
    }

    private void inicializarViews() {
        etNome = findViewById(R.id.etNome);
        etTelefone = findViewById(R.id.etTelefone);
        etEmail = findViewById(R.id.etEmail);
        etCidade = findViewById(R.id.etCidade);
        etBusca = findViewById(R.id.etBusca);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        spinnerFiltro = findViewById(R.id.spinnerFiltro);
        switchFavorito = findViewById(R.id.switchFavorito);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnLimpar = findViewById(R.id.btnLimpar);
        btnMostrarFavoritos = findViewById(R.id.btnMostrarFavoritos);
        listViewContatos = findViewById(R.id.listViewContatos);
    }

    private void configurarSpinners() {
        ArrayAdapter<String> adapterCategoria = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categorias);
        adapterCategoria.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapterCategoria);

        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categoriasFiltro);
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltro.setAdapter(adapterFiltro);
    }

    private void carregarTodosContatos() {
        listaContatos = db.buscarTodos();
        adapter = new ContatoAdapter(this, listaContatos);
        listViewContatos.setAdapter(adapter);
    }

    private void configurarEventos() {

        btnSalvar.setOnClickListener(v -> {
            if (validarCampos()) {
                if (contatoEmEdicao == null) {
                    inserirContato();
                } else {
                    atualizarContato();
                }
            }
        });

        btnLimpar.setOnClickListener(v -> limparFormulario());

        listViewContatos.setOnItemClickListener((parent, view, position, id) -> {
            Contato contato = listaContatos.get(position);
            carregarContatoParaEdicao(contato);
        });

        listViewContatos.setOnItemLongClickListener((parent, view, position, id) -> {
            Contato contato = listaContatos.get(position);
            confirmarExclusao(contato);
            return true;
        });

        spinnerFiltro.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                filtrarPorCategoria(categoriasFiltro[position]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        etBusca.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String termoBusca = s.toString().trim();
                if (termoBusca.isEmpty()) {
                    carregarTodosContatos();
                } else {
                    List<Contato> resultado = db.buscarPorNome(termoBusca);
                    adapter.atualizarLista(resultado);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnMostrarFavoritos.setOnClickListener(v -> {
            mostrаndоFavoritos = !mostrаndоFavoritos;
            if (mostrаndоFavoritos) {
                List<Contato> favoritos = db.buscarFavoritos();
                adapter.atualizarLista(favoritos);
                btnMostrarFavoritos.setText("Mostrar Todos");
            } else {
                carregarTodosContatos();
                btnMostrarFavoritos.setText("Só Favoritos");
            }
        });
    }

    private boolean validarCampos() {
        String nome = etNome.getText().toString().trim();
        String telefone = etTelefone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        if (nome.isEmpty()) {
            etNome.setError("Nome é obrigatório");
            etNome.requestFocus();
            return false;
        }
        if (telefone.isEmpty()) {
            etTelefone.setError("Telefone é obrigatório");
            etTelefone.requestFocus();
            return false;
        }
        if (email.isEmpty()) {
            etEmail.setError("E-mail é obrigatório");
            etEmail.requestFocus();
            return false;
        }
        return true;
    }

    private Contato obterContatoDoFormulario() {
        Contato c = new Contato();
        c.setNome(etNome.getText().toString().trim());
        c.setTelefone(etTelefone.getText().toString().trim());
        c.setEmail(etEmail.getText().toString().trim());
        c.setCategoria(spinnerCategoria.getSelectedItem().toString());
        c.setCidade(etCidade.getText().toString().trim());
        c.setFavorito(switchFavorito.isChecked());
        return c;
    }

    private void inserirContato() {
        Contato novoContato = obterContatoDoFormulario();
        long id = db.inserirContato(novoContato);
        if (id > 0) {
            Toast.makeText(this, "Contato salvo com sucesso!", Toast.LENGTH_SHORT).show();
            limparFormulario();
            carregarTodosContatos();
        } else {
            Toast.makeText(this, "Erro ao salvar contato.", Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarContato() {
        Contato contato = obterContatoDoFormulario();
        contato.setId(contatoEmEdicao.getId()); // Mantém o ID original
        int linhas = db.atualizarContato(contato);
        if (linhas > 0) {
            Toast.makeText(this, "Contato atualizado!", Toast.LENGTH_SHORT).show();
            limparFormulario();
            carregarTodosContatos();
        } else {
            Toast.makeText(this, "Erro ao atualizar.", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarContatoParaEdicao(Contato contato) {
        contatoEmEdicao = contato;
        etNome.setText(contato.getNome());
        etTelefone.setText(contato.getTelefone());
        etEmail.setText(contato.getEmail());
        etCidade.setText(contato.getCidade());
        switchFavorito.setChecked(contato.isFavorito());

        for (int i = 0; i < categorias.length; i++) {
            if (categorias[i].equals(contato.getCategoria())) {
                spinnerCategoria.setSelection(i);
                break;
            }
        }

        btnSalvar.setText("Atualizar");
        Toast.makeText(this, "Editando: " + contato.getNome(), Toast.LENGTH_SHORT).show();
    }

    private void confirmarExclusao(Contato contato) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Contato")
                .setMessage("Deseja excluir " + contato.getNome() + "?")
                .setPositiveButton("Excluir", (dialog, which) -> {
                    int linhas = db.excluirContato(contato.getId());
                    if (linhas > 0) {
                        Toast.makeText(this, "Contato excluído.", Toast.LENGTH_SHORT).show();
                        if (contatoEmEdicao != null &&
                                contatoEmEdicao.getId() == contato.getId()) {
                            limparFormulario();
                        }
                        carregarTodosContatos();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void filtrarPorCategoria(String categoria) {
        List<Contato> resultado;
        if (categoria.equals("Todas")) {
            resultado = db.buscarTodos();
        } else {
            resultado = db.buscarPorCategoria(categoria);
        }
        adapter.atualizarLista(resultado);
    }

    private void limparFormulario() {
        etNome.setText("");
        etTelefone.setText("");
        etEmail.setText("");
        etCidade.setText("");
        spinnerCategoria.setSelection(0);
        switchFavorito.setChecked(false);
        contatoEmEdicao = null;
        btnSalvar.setText("Salvar");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (db != null) {
            db.close();
        }
    }
}