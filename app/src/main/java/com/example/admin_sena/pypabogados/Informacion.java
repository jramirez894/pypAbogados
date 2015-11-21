package com.example.admin_sena.pypabogados;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin_sena.pypabogados.infclientes.Constantes;
import com.example.admin_sena.pypabogados.infclientes.InformacionProceso;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Informacion extends AppCompatActivity
{
    //Variables de la vista
    TextView numRadicado;
    TextView nomCliente;
    TextView txtdemandado;
    TextView txtjuzgadoActual;
    TextView txtultimaActuacion;

    EditText ediUltimActuacion;
    ImageView imgfoto;

    MenuItem menuEditar;
    MenuItem menuGuardar;

    View layoutConsultar;
    View layoutEditar;

    //Varibles Fotografia
    private static final int TAKE_PICTURE = 1;
    private Uri imageUri;

    //Un random para generar el numero aleatorio con el que quedara cada fotografia
    Random random;
    int numeroAleatorio;

    String respuesta = "";
    boolean existe = false;

    String idProceso;
    String radicado;
    String demandante;
    String demandado;
    String juzgadoInicial;
    String juzgadoActual;
    String ultimaActuacion;
    String descripcion;
    String numInterno;
    String foto;
    String idEstadoProceso;
    String idTipoProceso;

    String nombreFoto;
    File photo;

    //Array se encargaran de guardar la informacion
    ArrayList<InformacionProceso>procesoArrayList = new ArrayList<InformacionProceso>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);

        //Muestra en la parte superior el volver hacia la interfaz anterior
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Volver");

        //Relacionamos la variables
        layoutConsultar = (View)findViewById(R.id.layoutConsultar);
        layoutEditar = (View)findViewById(R.id.layoutEditar);

        numRadicado = (TextView)findViewById(R.id.textView_NumRadicado);
        nomCliente = (TextView)findViewById(R.id.textView_NomCliente);
        txtdemandado = (TextView)findViewById(R.id.textView_Demandado);
        txtjuzgadoActual = (TextView)findViewById(R.id.textView_JuzgadoActual);
        txtultimaActuacion = (TextView)findViewById(R.id.textView_UltimaActuacion);
        ediUltimActuacion = (EditText)findViewById(R.id.edit_UltimaActuacion_Modificar);

        //Mostramos en las variables un texto y lo que quedo en Constantes
        numRadicado.setText("#Radicado: "+ Constantes.numRadicado);
        nomCliente.setText("Demandante: "+Constantes.demandante);
        txtdemandado.setText("Demandado: "+Constantes.demandado);
        txtjuzgadoActual.setText("Juzgado Actual: "+Constantes.juzgadoActual);
        txtultimaActuacion.setText("Ultima Actuacion: "+Constantes.ultimaActuacion);
        ediUltimActuacion.setText(Constantes.ultimaActuacion);

        //Relacionamos la variable
        imgfoto = (ImageView)findViewById(R.id.imageFoto_Modificar);

        //Le asignamos memoria a la foto
        random = new Random();

        //Clic de la imagen de la foto
        imgfoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Llama al metodo LanzarFoto
                LanzarFoto();
            }
        });
    }

    //Metodo que abre la camara del dispositivo movil
    public void LanzarFoto()
    {
        //Generamos un numero aleatorio
        random.nextInt();
        numeroAleatorio = random.nextInt(1000000000);

        //Lanzamos la camara del dispositivo
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        //Le asignamos un numero en formato jpeg en la sdcar del dispositivo
        nombreFoto = Environment.getExternalStorageDirectory()+"/"+numeroAleatorio+".jpeg";
        //guardamos el nombre de la foto
        photo = new File(nombreFoto);
        //Guardamos la foto en la sd
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        //Ejecutamos lo anterior
        startActivityForResult(intent, TAKE_PICTURE);
    }

    //Metodo para saber si deseo guardar la foto o no
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case TAKE_PICTURE:
                //Preguntamos si decidio guardar la foto y la convierte a un bitmap para luego ser utilizada y puesta
                //en la imagen donde aparece la camara
                if (resultCode == Activity.RESULT_OK)
                {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try
                    {
                        //Guarda la foto en la sd
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        //Coloca la foto que fue tomada en donde aparecia la cam
                        imgfoto.setImageBitmap(bitmap);
                        imgfoto.setContentDescription(selectedImage.toString());


                    }
                    //Evitar posibles errores y si sale algun error saldra un mensaje que dira que no se cargo
                    catch (Exception e)
                    {
                        Toast.makeText(this, "La Foto No Se Cargo", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }

    //Clase que se encarga de enviar la foto a la webservice
    class UploaderFoto extends AsyncTask
    {
        //Variable que tiene el nombre de la foto
        String miFoto = "";
        //Variable que captura la respuesta del servidor
        String respStr;

        @Override
        protected Object doInBackground(Object[] params)
        {
            //Asociamos la variable con el parametro que fue enviado
            miFoto = String.valueOf(params[0]);

            try
            {
                //Declaramos la variables con las que vamos hacer la conexion y le asignamos la url a la que nos vamos a conectar
                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                HttpPost httppost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/upload.php");

                //Creamos una variable de tipo file que es la que va a tener con lo que quedo miFoto
                File file = new File(miFoto);
                //Creamos una variable de Tipo MultiparEntity  y le asignamos memoria
                MultipartEntity mpEntity = new MultipartEntity();
                //Creamos una variable ContentBody que es la que se va de guardar el formato con el que la foto quedara
                ContentBody foto = new FileBody(file, "image/jpeg");
                //a la variable mpEntity le enviamos foto y la recibira la web service en el campo fotoUp
                mpEntity.addPart("fotoUp", foto);
                //Ejecutamos lo que vamos a enviar
                httppost.setEntity(mpEntity);

                //Guardamos la repuesta del servidor
                HttpResponse resp = httpclient.execute(httppost);

                //Capturamos la respuesta del servidor
                respStr = EntityUtils.toString(resp.getEntity());

                //Ejecutamos la conexion
                httpclient.getConnectionManager().shutdown();

            }
            //Evitar errores
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        //Si la conexion fue exitosa entra a este metodo
        @Override
        protected void onPostExecute(Object o)
        {
            super.onPostExecute(o);
            //Saldra un mensaje que nos dira que fue exitosa la modificacion y nos llevara a la interfaz MenuPrincipal
            Toast.makeText(Informacion.this, "Datos Modificados con Exito", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Informacion.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
        }
    }

    //Metodo del Menu Superior del dispositivo
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_informacion, menu);

        //Elementos del menu item
        menuEditar = (MenuItem) menu.findItem(R.id.editar_Cliente);
        menuGuardar = (MenuItem) menu.findItem(R.id.guardar_Cliente);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //Preguntamos a que item le dio click
        switch (item.getItemId())
        {
            case R.id.editar_Cliente:
                //Se oculta una lo que hay en la pantalla y se visualiza lo que hay abajo osea el campo editable y la camara
                layoutConsultar.setVisibility(View.GONE);
                layoutEditar.setVisibility(View.VISIBLE);
                menuEditar.setVisible(false);
                menuGuardar.setVisible(true);
                break;

            case R.id.guardar_Cliente:
                //El click del guardar
                //Capturamoo lo que dice en las dos variables por si depronto estan vaccias de ser asi
                //Saldra un mensaje que dira que faltan datos por llenar y si no llama al metodo AlertaConfirmacion
                String capActuacion = ediUltimActuacion.getText().toString();
                String capFoto = imgfoto.getContentDescription().toString();

                if (capActuacion.equals("") ||
                        capFoto.equals("camara"))
                {
                    Toast.makeText(Informacion.this,"Faltan Datos Por LLenar",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    AlertaConfirmacion();
                }
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    //Alerta de Confirmacion
    public void AlertaConfirmacion()
    {
        //Inflamos la vista del layout y le decimos cual es el layout
        LayoutInflater inflaterAlert = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialoglayout = inflaterAlert.inflate(R.layout.alertadeconfirmacion, null);

        //Creamos las variables de AlertDialog para poder crear la alerta
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialoglayout);

        //el booton positivo de la alerta que tiene como titulo Aceptar
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //Capturamos lo que esta en la variables
                String capActuacion = ediUltimActuacion.getText().toString();
                String nomFoto = String.valueOf(numeroAleatorio)+".jpeg";

                //Lanzamos la conexion enviando los dos parametros que van hacer modificados
                TareaUpdadte tareaUpdadte = new TareaUpdadte();
                tareaUpdadte.execute(capActuacion,nomFoto);

                //Aparecera un mensaje cargando por si la conexion se demora mucho
                Toast.makeText(Informacion.this,"Cargando Por Favor Espere..",Toast.LENGTH_LONG).show();
            }
        });
        //el booton negative de la alerta que tiene como titulo Cancelar y no hace nada por eso tiene el null
        builder.setNegativeButton("Cancelar", null);
        //para que la alerta no se quite al dar clic por la pantalla
        builder.setCancelable(false);

        //para que la alerta se muestre
        builder.show();
    }

    //Clases Asyntask para modificar datos de la tabla productos
    private class TareaUpdadte extends AsyncTask<String,Integer,Boolean>
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
            httpPost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/ControllerProcesos.php");

            //Enviamos las variable option que va a llevar y se envian las variables de la Clase Constantes por que solo van hacer
            //modificados dos campos
            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("idProcesos", Constantes.idProcesos));
            nameValuePairs.add(new BasicNameValuePair("numRadicado", Constantes.numRadicado));
            nameValuePairs.add(new BasicNameValuePair("demandado", Constantes.demandado));
            nameValuePairs.add(new BasicNameValuePair("juzgadoInicial", Constantes.juzgadoIncial));
            nameValuePairs.add(new BasicNameValuePair("juzgadoActual", Constantes.juzgadoActual));
            nameValuePairs.add(new BasicNameValuePair("descripcion", Constantes.descripcion));
            nameValuePairs.add(new BasicNameValuePair("ultimaActuacion", params[0]));
            nameValuePairs.add(new BasicNameValuePair("numInterno", Constantes.numInterno));
            nameValuePairs.add(new BasicNameValuePair("foto", params[1]));
            nameValuePairs.add(new BasicNameValuePair("idEstadoProceso", Constantes.idEstadoProceso));
            nameValuePairs.add(new BasicNameValuePair("idTipoProceso", Constantes.idTipoProceso));

            nameValuePairs.add(new BasicNameValuePair("option", "updateProcess"));

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

                //Guardamos la respuesta del servidor en un String
                respuesta= String.valueOf(objItems);

                //Preguntamos si la variable respuesta quedo con No Existe de ser Asi la varibale existe queda en false
                //y si no que da en true
                if(respuesta.equalsIgnoreCase("No Existe"))
                {
                    existe = false;
                }
                else
                {
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
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            return resul;
        }

        //Metodo que se ejecuta si la conexion se hizo
        protected void onPostExecute(Boolean result)
        {
            //Preguntamos Si la variable quedo Verdadera si lo es ejecutamos la enviada de la foto y le enviamos como parametro el nombre
            //de la foto y si no saldra un error que nos dira que error al modificar usuario
            if(existe)
            {
                UploaderFoto nuevaTarea = new UploaderFoto();
                nuevaTarea.execute(nombreFoto);
            }
            else
            {
                Toast.makeText(Informacion.this, "Error al Modificar Usuario", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
