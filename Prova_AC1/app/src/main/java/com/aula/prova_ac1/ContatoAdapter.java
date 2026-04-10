package com.aula.prova_ac1;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ContatoAdapter extends ArrayAdapter<Contato> {

    private Context context;
    private List<Contato> listaContatos;

    public ContatoAdapter(Context context, List<Contato> listaContatos) {
        super(context, R.layout.item_contato, listaContatos);
        this.context = context;
        this.listaContatos = listaContatos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_contato, parent, false);
        }

        Contato contato = listaContatos.get(position);

        TextView tvNome = convertView.findViewById(R.id.tvNomeItem);
        TextView tvTelefone = convertView.findViewById(R.id.tvTelefoneItem);
        TextView tvEmail = convertView.findViewById(R.id.tvEmailItem);
        TextView tvCategoria = convertView.findViewById(R.id.tvCategoriaItem);
        TextView tvCidade = convertView.findViewById(R.id.tvCidadeItem);
        TextView tvFavorito = convertView.findViewById(R.id.tvFavoritoItem);

        tvNome.setText(contato.getNome());
        tvTelefone.setText(contato.getTelefone());
        tvEmail.setText(contato.getEmail());
        tvCategoria.setText(contato.getCategoria());
        tvCidade.setText(contato.getCidade());
        tvFavorito.setText(contato.isFavorito() ? "★ Favorito" : "");

        return convertView;
    }

    public void atualizarLista(List<Contato> novaLista) {
        listaContatos.clear();
        listaContatos.addAll(novaLista);
        notifyDataSetChanged();
    }
}