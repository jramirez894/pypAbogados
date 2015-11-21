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
    //Creacion de Variables deacuerdo a la vista
    AutoCompleteTextView buscarCliente;
    RadioGroup radioGroup;
    RadioButton radioCedula;
    RadioButton radioRadicado;

    ListView listaClientes;

    //Array se encargaran de guardar la informacion
    ArrayList<ItemListaCLientes>arrayList= new ArrayList<ItemListaCLientes>();
    ArrayList<InformacionCliente>arrayListInformacion= new ArrayList<InformacionCliente>();
    ArrayList<InformacionProceso>arrayListProcesos= new ArrayList<InformacionProceso>();
    ArrayList<InformacionProceso>arrayListRadicado= new ArrayList<InformacionProceso>();
    ArrayList<String>arrayListCedula= new ArrayList<String>();
    ArrayList<String>arrayListNumRadicado= new ArrayList<String>();

    //Variable booleana que se encargara de verificar
    boolean existe = false;
    //Variable que guardara la respuesta del servidor
    String respuesta = "";
    //Variable Json donde se guardar los clientes que nos traiga el servidor
    JSONArray objClientes;

    //Variables de Clases que contienen Variables de tipo String para poder acceder a ellas y guardar inf
    InformacionCliente cliente;
    InformacionProceso procesos;

    //Variable booleana que utilizaremos para saber por cual buscar
    boolean opcionRadicado;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);

        //Relacionamos la variables
        buscarCliente = (AutoCompleteTextView) findViewById(R.id.autoCompleteBuscarCliente);
        radioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        radioCedula = (RadioButton) findViewById(R.id.radioButtonCedula);
        radioRadicado = (RadioButton) findViewById(R.id.radioButtonRadicado);

        listaClientes = (ListView) findViewById(R.id.listaClientes);

        //Preguntamos si el radio esta checkiado para poner el titulo por el cual se va a buscar
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

        //Metodo para saber cual de los radioButton estan chek para saber por cual me va a buscar
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (radioCedula.isChecked())
                {
                    buscarCliente.setHint("Buscar Por Cedula");
                    //Actualizamos el autocomplete
                    buscarCliente.setAdapter(new ArrayAdapter<String>(MenuPrincipal.this, android.R.layout.simple_dropdown_item_1line, arrayListCedula));
                    //Variable booleana que luego usaremos para saber si buscamos por cedula
                    opcionRadicado = false;
                }
                else
                {
                    if (radioRadicado.isChecked())
                    {
                        buscarCliente.setHint("Buscar Por #Radicado");
                        //Actualizamos el autocomplete
                        buscarCliente.setAdapter(new ArrayAdapter<String>(MenuPrincipal.this, android.R.layout.simple_dropdown_item_1line, arrayListNumRadicado));
                        //Variable booleana que luego usaremos para saber si buscamos por cedula
                        opcionRadicado = true;
                    }

                }
            }
        });

        //El click de la lista
        listaClientes.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                //Hacemos la conexion con la tarea para que nos abra la informacion y mostramos un cargando por si se demoora
                //la conexion
                Toast.makeText(MenuPrincipal.this, "Cargando...", Toast.LENGTH_SHORT).show();
                TareaProcesos tareaProcesos = new TareaProcesos();
                tareaProcesos.execute(String.valueOf(position));
            }
        });

        //Clic del autocomplete
        buscarCliente.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //Preguntamos cual radioButton esta check para saber por cual buscar
                if (radioCedula.isChecked())
                {
                    //capturamos lo que dice el autocomplete
                    String cedula = buscarCliente.getText().toString();
                    //Variable de la posocion del autocomplete
                    int posicion = 0;

                    //un for para recorrer y buscar en donde se encuentra el nombre que fue ingresado en el autoccomplete
                    for(int i = 0; i < arrayListInformacion.size(); i++) {
                        //preguntamos si lo que dice en el autocomplete es lo que esta guardado en el array en la posicon
                        //que este el for en el Strgin getCedula y solo entra si lo encuentra y nos guarda la posicion en que encontrada
                        if (cedula.equalsIgnoreCase(arrayListInformacion.get(i).getCedula())) {
                            posicion = i;
                            break;
                        }
                    }
                    //Hacemos un mensaje que salga cargando y llamamos a la tarea que se va a encargar de trar los procesos y le enviamos como
                    //parametro la posicion en la que fue encontrado el nombre del autocomplete
                    Toast.makeText(MenuPrincipal.this, "Cargando...", Toast.LENGTH_SHORT).show();
                    TareaProcesos tareaProcesos = new TareaProcesos();
                    tareaProcesos.execute(String.valueOf(posicion));
                }
                else
                {
                    if (radioRadicado.isChecked())
                    {
                        //capturamos lo que dice el autocomplete
                        String radicado = buscarCliente.getText().toString();
                        //Variable de la posocion del autocomplete
                        int posicion = 0;

                        //un for para recorrer y buscar en donde se encuentra el nombre que fue ingresado en el autoccomplete
                        for(int i = 0; i < arrayListRadicado.size(); i++)
                        {
                            //preguntamos si lo que dice en el autocomplete es lo que esta guardado en el array en la posicon
                            //que este el for en el Strgin getCedula y solo entra si lo encuentra y nos guarda la posicion en que encontrada
                            if(radicado.equalsIgnoreCase(arrayListRadicado.get(i).getNumRadicado()))
                            {
                                posicion = i;
                                break;
                            }
                        }
                        //Hacemos un mensaje que salga cargando y llamamos a la tarea que se va a encargar de trar los procesos y le enviamos como
                        //parametro la posicion en la que fue encontrado el nombre del autocomplete
                        Toast.makeText(MenuPrincipal.this, "Cargando...", Toast.LENGTH_SHORT).show();
                        TareaProcesos tareaProcesos = new TareaProcesos();
                        tareaProcesos.execute(String.valueOf(posicion));
                    }

                }


            }
        });

        //Aqui llamamos al metodo que nos abrira la conexion de tipo de busqueda
        EjecutarTarea();
    }

    //Metodo que ejuta el tipo de Busqueda
    public void EjecutarTarea()
    {
        TareaListado tareaListado = new TareaListado();
        tareaListado.execute();

        TareaRadicado tareaRadicado = new TareaRadicado();
        tareaRadicado.execute();
    }


    //Metodos Para el menu
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

    //Alerta Personalizada para cerrar Sesion
    public void AlertaCerrar()
    {
        //Inflamos la vista del layout y le decimos cual es el layout
        LayoutInflater inflaterAlert = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialoglayout = inflaterAlert.inflate(R.layout.alert_cerrar, null);


        //Creamos las variables de AlertDialog para poder crear la alerta
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialoglayout);

        //el booton positivo de la alerta que tiene como titulo Aceptar
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //Lanzar la Actividad Principal
                Intent intent = new Intent(MenuPrincipal.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //el booton negative de la alerta que tiene como titulo Cancelar y no hace nada por eso tiene el null
        builder.setNegativeButton("Cancelar", null);
        //para que la alerta no se quite al dar clic por la pantalla
        builder.setCancelable(false);
        //para que la alerta se muestre
        builder.show();
    }

    //Clases Asyntask para traer los datos de los clientes
    private class TareaListado extends AsyncTask<String,Integer,Boolean>
    {
        //Variable para capturar la respuesta del servidor
        private String respStr;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        protected Boolean doInBackground(String... params)
        {
            //Declaramos la variables con las que vamos hacer la conexion y le asignamos la url a la que nos vamos a conectar
            boolean resul = true;
            HttpClient httpClient;
            List<NameValuePair> nameValuePairs;
            HttpPost httpPost;
            httpClient= new DefaultHttpClient();
            httpPost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/ControllerClientes.php");

            //Enviamos las variable option que va a llevar getAllCliente por que la webService recibe muchas opciones
            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("option", "getAllClient"));

            try
            {
                //ejecutamos lo que va hacer enviado
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //Capturamos la respuesta del servidor
                HttpResponse resp= httpClient.execute(httpPost);

                //Combertimos esa respuesta en un String
                respStr = EntityUtils.toString(resp.getEntity());

                //Guardamos esa respuesta en jSon y lo convertimos array por que la respuesta llegaba en dos {{
                JSONObject respJSON = new JSONObject(respStr);
                //Acedemos a items quue es la respuesta del servidor
                JSONArray objItems = respJSON.getJSONArray("items");
                //guardamos a items en la posicion 0
                objClientes = objItems.getJSONArray(0);

                //Guardamos la respuesta del servidor en un String
                respuesta= String.valueOf(objClientes);

                //Limpiamos los array para que no se duplique a la hora de abrir nuevamente la clase
                arrayListInformacion.clear();
                arrayList.clear();
                arrayListCedula.clear();

                //Recorremos el jsonOnb
                for(int i = 0; i < objClientes.length(); i++)
                {
                    //Guardamos lo que vamos recorriendo en jSobj en la posicion que valla en for
                    JSONObject obj = objClientes.getJSONObject(i);
                    //En este array solo guardamos el nombre y el apellido que es  la lista que es vista en la pantalla
                    arrayList.add(new ItemListaCLientes(obj.getString("nombre")+" "+obj.getString("apellido")));
                    //En este array si guardamos que nos trajo el servidor para luego acceder a ellos
                    arrayListInformacion.add(new InformacionCliente(obj.getString("idClientes"),obj.getString("cedula"),obj.getString("nombre"),obj.getString("apellido"),obj.getString("direccion"),obj.getString("ciudad"),obj.getString("telefono"),obj.getString("correo"),obj.getString("idProcesos")));
                    //En este array solo guardamos la cedula para luego ser utilizada y poder buscar por cedula
                    arrayListCedula.add(obj.getString("cedula"));
                    //la variable pasa hacer verdadera por que encontro el registro
                    existe = true;
                }

                resul = true;
            }
            //catch para evitar errores
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

        //Metodo que ejecuta cuando la conexion se hizo bien
        protected void onPostExecute(Boolean result)
        {
            //Actualizamos la lista y el autocomplete con los array indicados
            listaClientes.setAdapter(new AdapterListaClientes(MenuPrincipal.this,arrayList));
            buscarCliente.setAdapter(new ArrayAdapter<String>(MenuPrincipal.this,android.R.layout.simple_dropdown_item_1line,arrayListCedula));
        }
    }

    //Clases Asyntask para traer los datos
    private class TareaProcesos extends AsyncTask<String,Integer,Boolean>
    {
        //Variable donde va hacer guardada la respuesta del servidor
        private String respStr;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        protected Boolean doInBackground(String... params)
        {
            //Declaramos la variables con las que vamos hacer la conexion y le asignamos la url a la que nos vamos a conectar
            boolean resul = true;
            HttpClient httpClient;
            List<NameValuePair> nameValuePairs;
            HttpPost httpPost;
            httpClient= new DefaultHttpClient();
            httpPost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/ControllerProcesos.php");

            //Decidir si buscar por cedula o radicado
            //Aqui pregunamos si la variable buscar por radicado quedo verdadera de no ser asi queda con la variable cliente
            //queda con la posicion 0 del array
            if (opcionRadicado)
            {
                //Recorremos el array que tiene la informacion
                for(int i = 0; i < arrayListInformacion.size(); i++)
                {
                    //preguntamos si el array donde quedo guardado el num de radicado en la posicion 0 en idProceso es igual al array
                    //que estamos recorriendo en la posicion 0 en idProceso si lo son entonces la variable cliente va a quedar con la
                    //posicion donde fue encontrado el idProceso
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

            //Enviamos la opcion y enviamos el idProceso a la webService
            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("option", "getAllProcess"));
            nameValuePairs.add(new BasicNameValuePair("idProcesos", cliente.getIdProcesos()));

            try
            {
                //ejecutamos lo que va hacer enviado
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //Capturamos la respuesta del servidor
                HttpResponse resp= httpClient.execute(httpPost);

                //Combertimos esa respuesta en un String
                respStr = EntityUtils.toString(resp.getEntity());

                //Guardamos esa respuesta en jSon y lo convertimos array por que la respuesta llegaba en dos {{
                JSONObject respJSON = new JSONObject(respStr);
                //En este Json array capturamos a items
                JSONArray objItems = respJSON.getJSONArray("items");
                //En este jSon array guardamos lo que halla en items en la posicion 0
                JSONArray objProceso = objItems.getJSONArray(0);

                //Guardamos lo que quedo en la posicon 0 de items en un variable de tipo String
                respuesta= String.valueOf(objProceso);

                //Limpiamos el array para que se repita el registro al reiniciar la clase
                arrayListProcesos.clear();

                //Recoremos a objProceso
                for(int i = 0; i < objProceso.length(); i++)
                {
                    //Guardamos lo que vamos recorriendo en jSobj en la posicion que valla en for
                    JSONObject obj = objProceso.getJSONObject(i);
                    //En este Array guardamos lo que tenia el jSon
                    arrayListProcesos.add(new InformacionProceso(obj.getString("idProcesos"),obj.getString("numRadicado"),obj.getString("demandado"),obj.getString("juzgadoInicial"),obj.getString("juzgadoActual"),obj.getString("descripcion"),obj.getString("ultimaActuacion"),obj.getString("numInterno"),obj.getString("foto"),obj.getString("idEstadoProceso"),obj.getString("idTipoProceso")));
                    //la variable pasa hacer verdadera por que encontro el registro
                    existe = true;
                }

                resul = true;
            }
            //catch para evitar errores
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

        //Metodo que se ejecuta si la conexion se hizo
        protected void onPostExecute(Boolean result)
        {
            //En la variable procceso guardamos la posicion del proceso
            procesos = arrayListProcesos.get(0);

            //LLamamos la clase Constantes que se encargara de guardar la respuesta del servidor
            //en sus respectivas variables
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

            //Abrir la clase donde se mostrara la informacion
            Intent intent = new Intent(MenuPrincipal.this, Informacion.class);
            startActivity(intent);
        }
    }

    //Clases Asyntask para traer los datos de los clientes
    private class TareaRadicado extends AsyncTask<String,Integer,Boolean>
    {
        //Variable donde va hacer guardada la respuesta del servidor
        private String respStr;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        protected Boolean doInBackground(String... params)
        {
            //Declaramos la variables con las que vamos hacer la conexion y le asignamos la url a la que nos vamos a conectar
            boolean resul = true;
            HttpClient httpClient;
            List<NameValuePair> nameValuePairs;
            HttpPost httpPost;
            httpClient= new DefaultHttpClient();
            httpPost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/ControllerProcesos.php");

            //Enviamos la opcion y enviamos el idProceso a la webService
            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("option", "getRadicados"));

            try
            {
                //ejecutamos lo que va hacer enviado
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //Capturamos la respuesta del servidor
                HttpResponse resp= httpClient.execute(httpPost);

                //Combertimos esa respuesta en un String
                respStr = EntityUtils.toString(resp.getEntity());

                //Guardamos esa respuesta en jSon y lo convertimos array por que la respuesta llegaba en dos {{
                JSONObject respJSON = new JSONObject(respStr);
                //En este Json array capturamos a items
                JSONArray objItems = respJSON.getJSONArray("items");
                //En este jSon array guardamos lo que halla en items en la posicion 0
                JSONArray objRadicado = objItems.getJSONArray(0);

                //Capturamos lo que hay en el jSonObj en una variable de tipo String
                respuesta= String.valueOf(objRadicado);

                //Limpiamos el array para que se repita el registro al reiniciar la clase
                arrayListRadicado.clear();
                arrayListNumRadicado.clear();

                //Recoremos a objRadicado
                for(int i = 0; i < objRadicado.length(); i++)
                {
                    //Guardamos lo que vamos recorriendo en jSobj en la posicion que valla en for
                    JSONObject obj = objRadicado.getJSONObject(i);
                    //En este Array guardamos lo que tenia el jSon
                    arrayListRadicado.add(new InformacionProceso(obj.getString("idProcesos"),obj.getString("numRadicado"),obj.getString("demandado"),obj.getString("juzgadoInicial"),obj.getString("juzgadoActual"),obj.getString("descripcion"),obj.getString("ultimaActuacion"),obj.getString("numInterno"),obj.getString("foto"),obj.getString("idEstadoProceso"),obj.getString("idTipoProceso")));
                    //En este array guardamos el numero de radicado para luego buscar por el numero de radicado
                    arrayListNumRadicado.add(obj.getString("numRadicado"));
                    //la variable pasa hacer verdadera por que encontro el registro
                    existe = true;
                }
                resul = true;
            }
            //catch para evitar errores
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
        }
    }
}
