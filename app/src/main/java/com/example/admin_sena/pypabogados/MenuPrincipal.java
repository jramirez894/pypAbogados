package com.example.admin_sena.pypabogados;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.admin_sena.pypabogados.infclientes.Constantes;
import com.example.admin_sena.pypabogados.infclientes.InformacionCliente;
import com.example.admin_sena.pypabogados.infclientes.InformacionProceso;
import com.example.admin_sena.pypabogados.listapersonalizada.AdapterListaClientes;
import com.example.admin_sena.pypabogados.listapersonalizada.ItemListaCLientes;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MenuPrincipal extends AppCompatActivity
{
    AutoCompleteTextView buscarCliente;
    RadioGroup radioGroup;
    RadioButton radioCedula;
    RadioButton radioRadicado;

    ListView listaClientes;

    ArrayList<ItemListaCLientes>arrayList= new ArrayList<ItemListaCLientes>();
    ArrayList<InformacionCliente>arrayListInformacion= new ArrayList<InformacionCliente>();
    ArrayList<InformacionProceso>arrayListProcesos= new ArrayList<InformacionProceso>();
    ArrayList<InformacionProceso>arrayListRadicado= new ArrayList<InformacionProceso>();
    ArrayList<String>arrayListCedula= new ArrayList<String>();
    ArrayList<String>arrayListNumRadicado= new ArrayList<String>();

    boolean existe = false;
    String respuesta = "";
    JSONArray objClientes;

    InformacionCliente cliente;
    InformacionProceso procesos;

    boolean opcionRadicado;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        buscarCliente = (AutoCompleteTextView) findViewById(R.id.autoCompleteBuscarCliente);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioCedula = (RadioButton) findViewById(R.id.radioButtonCedula);
        radioRadicado = (RadioButton) findViewById(R.id.radioButtonRadicado);

        listaClientes = (ListView) findViewById(R.id.listaClientes);

        if (radioCedula.isChecked())
        {
            buscarCliente.setHint("Buscar Por Cedula");
        }
        else
        {
            if (radioRadicado.isChecked())
            {
                buscarCliente.setHint("Buscar Por #Radicado");
            }

        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioCedula.isChecked())
                {
                    buscarCliente.setHint("Buscar Por Cedula");
                    buscarCliente.setAdapter(new ArrayAdapter<String>(MenuPrincipal.this, android.R.layout.simple_dropdown_item_1line, arrayListCedula));
                    opcionRadicado = false;
                }
                else
                {
                    if (radioRadicado.isChecked())
                    {
                        buscarCliente.setHint("Buscar Por #Radicado");
                        buscarCliente.setAdapter(new ArrayAdapter<String>(MenuPrincipal.this, android.R.layout.simple_dropdown_item_1line, arrayListNumRadicado));
                        opcionRadicado = true;
                    }

                }
            }
        });

        listaClientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Toast.makeText(MenuPrincipal.this, "Cargando...", Toast.LENGTH_SHORT).show();
                TareaProcesos tareaProcesos = new TareaProcesos();
                tareaProcesos.execute(String.valueOf(position));
            }
        });

        buscarCliente.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (radioCedula.isChecked())
                {
                    String cedula = buscarCliente.getText().toString();
                    int posicion = 0;

                    for(int i = 0; i < arrayListInformacion.size(); i++)
                    {
                        if(cedula.equalsIgnoreCase(arrayListInformacion.get(i).getCedula()))
                        {
                            posicion = i;
                            break;
                        }
                    }

                    Toast.makeText(MenuPrincipal.this, "Cargando...", Toast.LENGTH_SHORT).show();
                    TareaProcesos tareaProcesos = new TareaProcesos();
                    tareaProcesos.execute(String.valueOf(posicion));
                }
                else
                {
                    if (radioRadicado.isChecked())
                    {
                        String radicado = buscarCliente.getText().toString();
                        int posicion = 0;

                        for(int i = 0; i < arrayListRadicado.size(); i++)
                        {
                            if(radicado.equalsIgnoreCase(arrayListRadicado.get(i).getNumRadicado()))
                            {
                                posicion = i;
                                break;
                            }
                        }

                        Toast.makeText(MenuPrincipal.this, "Cargando...", Toast.LENGTH_SHORT).show();
                        TareaProcesos tareaProcesos = new TareaProcesos();
                        tareaProcesos.execute(String.valueOf(posicion));
                    }

                }


            }
        });

        EjecutarTarea();
    }

    public void EjecutarTarea()
    {
        TareaListado tareaListado = new TareaListado();
        tareaListado.execute();

        TareaRadicado tareaRadicado = new TareaRadicado();
        tareaRadicado.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (item.getItemId())
        {
            case R.id.cerrarSesion:
                AlertaCerrar();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void AlertaCerrar()
    {
        LayoutInflater inflaterAlert = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialoglayout = inflaterAlert.inflate(R.layout.alert_cerrar, null);


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialoglayout);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                Intent intent = new Intent(MenuPrincipal.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.setCancelable(false);

        builder.show();
    }

    //Clases Asyntask para traer los datos de los clientes

    private class TareaListado extends AsyncTask<String,Integer,Boolean>
    {
        private String respStr;
        private JSONObject msg;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        protected Boolean doInBackground(String... params)
        {
            boolean resul = true;
            HttpClient httpClient;
            List<NameValuePair> nameValuePairs;
            HttpPost httpPost;
            httpClient= new DefaultHttpClient();
            httpPost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/ControllerClientes.php");

            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("option", "getAllClient"));

            try
            {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse resp= httpClient.execute(httpPost);

                respStr = EntityUtils.toString(resp.getEntity());

                JSONObject respJSON = new JSONObject(respStr);
                JSONArray objItems = respJSON.getJSONArray("items");
                objClientes = objItems.getJSONArray(0);

                respuesta= String.valueOf(objClientes);

                arrayListInformacion.clear();
                arrayList.clear();
                arrayListCedula.clear();

                for(int i = 0; i < objClientes.length(); i++)
                {
                    JSONObject obj = objClientes.getJSONObject(i);
                    arrayList.add(new ItemListaCLientes(obj.getString("nombre")+" "+obj.getString("apellido")));
                    arrayListInformacion.add(new InformacionCliente(obj.getString("idClientes"),obj.getString("cedula"),obj.getString("nombre"),obj.getString("apellido"),obj.getString("direccion"),obj.getString("ciudad"),obj.getString("telefono"),obj.getString("correo"),obj.getString("idProcesos")));
                    arrayListCedula.add(obj.getString("cedula"));
                    existe = true;
                }


                resul = true;
            }
            catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
                resul = false;
            }

            catch(ClientProtocolException e)
            {
                e.printStackTrace();
                resul = false;
            }

            catch (IOException e)
            {
                e.printStackTrace();
                resul = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return resul;
        }

        protected void onPostExecute(Boolean result)
        {
            //Toast.makeText(MenuPrincipal.this, " " + objClientes.length(), Toast.LENGTH_SHORT).show();
            listaClientes.setAdapter(new AdapterListaClientes(MenuPrincipal.this,arrayList));
            buscarCliente.setAdapter(new ArrayAdapter<String>(MenuPrincipal.this,android.R.layout.simple_dropdown_item_1line,arrayListCedula));
        }
    }

    public void buscarPorRadicado()
    {

    }

    //Clases Asyntask para traer los datos
    private class TareaProcesos extends AsyncTask<String,Integer,Boolean>
    {
        private String respStr;
        private JSONObject msg;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        protected Boolean doInBackground(String... params)
        {
            boolean resul = true;
            HttpClient httpClient;
            List<NameValuePair> nameValuePairs;
            HttpPost httpPost;
            httpClient= new DefaultHttpClient();
            httpPost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/ControllerProcesos.php");

            //Decidir si buscar por cedula o radicado
            if (opcionRadicado)
            {
                for(int i = 0; i < arrayListInformacion.size(); i++)
                {
                    if(arrayListRadicado.get(Integer.valueOf(params[0])).getIdProcesos().equalsIgnoreCase(arrayListInformacion.get(i).getIdProcesos()))
                    {
                        cliente = arrayListInformacion.get(i);
                    }
                }

            }
            else
            {
                cliente = arrayListInformacion.get(Integer.valueOf(params[0]));
            }


            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("option", "getAllProcess"));
            nameValuePairs.add(new BasicNameValuePair("idProcesos", cliente.getIdProcesos()));

            try
            {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse resp= httpClient.execute(httpPost);

                respStr = EntityUtils.toString(resp.getEntity());

                JSONObject respJSON = new JSONObject(respStr);
                JSONArray objItems = respJSON.getJSONArray("items");
                JSONArray objProceso = objItems.getJSONArray(0);

                respuesta= String.valueOf(objProceso);

                arrayListProcesos.clear();

                for(int i = 0; i < objProceso.length(); i++)
                {
                    JSONObject obj = objProceso.getJSONObject(i);
                    arrayListProcesos.add(new InformacionProceso(obj.getString("idProcesos"),obj.getString("numRadicado"),obj.getString("demandado"),obj.getString("juzgadoInicial"),obj.getString("juzgadoActual"),obj.getString("descripcion"),obj.getString("ultimaActuacion"),obj.getString("numInterno"),obj.getString("foto"),obj.getString("idEstadoProceso"),obj.getString("idTipoProceso")));
                    existe = true;
                }

                resul = true;
            }
            catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
                resul = false;
            }

            catch(ClientProtocolException e)
            {
                e.printStackTrace();
                resul = false;
            }

            catch (IOException e)
            {
                e.printStackTrace();
                resul = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return resul;
        }

        protected void onPostExecute(Boolean result)
        {
            //Toast.makeText(MenuPrincipal.this, "Proceso Cargado", Toast.LENGTH_SHORT).show();

            procesos = arrayListProcesos.get(0);

            Constantes.idProcesos = procesos.getIdProcesos();
            Constantes.numRadicado = procesos.getNumRadicado();
            Constantes.demandante = cliente.getNombre() + " " + cliente.getApellido();
            Constantes.demandado = procesos.getDemandado();
            Constantes.juzgadoIncial = procesos.getJuzgadoIncial();
            Constantes.juzgadoActual = procesos.getJuzgadoActual();
            Constantes.descripcion = procesos.getDescripcion();
            Constantes.ultimaActuacion =  procesos.getUltimaActuacion();
            Constantes.numInterno = procesos.getNumInterno();
            Constantes.foto = procesos.getFoto();
            Constantes.idEstadoProceso = procesos.getIdEstadoProceso();
            Constantes.idTipoProceso = procesos.getIdTipoProceso();

            Intent intent = new Intent(MenuPrincipal.this, Informacion.class);
            startActivity(intent);
        }
    }

    //Clases Asyntask para traer los datos de los clientes

    private class TareaRadicado extends AsyncTask<String,Integer,Boolean>
    {
        private String respStr;
        private JSONObject msg;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        protected Boolean doInBackground(String... params)
        {
            boolean resul = true;
            HttpClient httpClient;
            List<NameValuePair> nameValuePairs;
            HttpPost httpPost;
            httpClient= new DefaultHttpClient();
            httpPost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/ControllerProcesos.php");

            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("option", "getRadicados"));

            try
            {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse resp= httpClient.execute(httpPost);

                respStr = EntityUtils.toString(resp.getEntity());

                JSONObject respJSON = new JSONObject(respStr);
                JSONArray objItems = respJSON.getJSONArray("items");
                JSONArray objRadicado = objItems.getJSONArray(0);

                respuesta= String.valueOf(objRadicado);

                arrayListRadicado.clear();
                arrayListNumRadicado.clear();

                for(int i = 0; i < objRadicado.length(); i++)
                {
                    JSONObject obj = objRadicado.getJSONObject(i);
                    arrayListRadicado.add(new InformacionProceso(obj.getString("idProcesos"),obj.getString("numRadicado"),obj.getString("demandado"),obj.getString("juzgadoInicial"),obj.getString("juzgadoActual"),obj.getString("descripcion"),obj.getString("ultimaActuacion"),obj.getString("numInterno"),obj.getString("foto"),obj.getString("idEstadoProceso"),obj.getString("idTipoProceso")));
                    arrayListNumRadicado.add(obj.getString("numRadicado"));
                    existe = true;
                }


                resul = true;
            }
            catch(UnsupportedEncodingException e)
            {
                e.printStackTrace();
                resul = false;
            }

            catch(ClientProtocolException e)
            {
                e.printStackTrace();
                resul = false;
            }

            catch (IOException e)
            {
                e.printStackTrace();
                resul = false;
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return resul;
        }

        protected void onPostExecute(Boolean result)
        {
            //Toast.makeText(MenuPrincipal.this, ""+ arrayListRadicado.size(), Toast.LENGTH_SHORT).show();
            //listaClientes.setAdapter(new AdapterListaClientes(MenuPrincipal.this, arrayList));

        }
    }
}
