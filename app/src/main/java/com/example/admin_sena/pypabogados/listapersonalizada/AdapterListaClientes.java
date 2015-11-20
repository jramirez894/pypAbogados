package com.example.admin_sena.pypabogados.listapersonalizada;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.admin_sena.pypabogados.R;

import java.util.List;

/**
 * Created by Admin_Sena on 04/11/2015.
 */
public class AdapterListaClientes extends ArrayAdapter
{

    public AdapterListaClientes(Context context, List objects)
    {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView==null)
        {
            LayoutInflater inflater= (LayoutInflater)parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.itemlistaclientes,null);
        }

        ItemListaCLientes items = (ItemListaCLientes)getItem(position);

        TextView nombre = (TextView)convertView.findViewById(R.id.nombre_ListaCliente);
        nombre.setText(items.getNombre());
        return convertView;
    }
}
