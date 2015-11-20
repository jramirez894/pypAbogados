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

    ArrayList<InformacionProceso>procesoArrayList = new ArrayList<InformacionProceso>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Volver");

        layoutConsultar = (View)findViewById(R.id.layoutConsultar);
        layoutEditar = (View)findViewById(R.id.layoutEditar);

        numRadicado = (TextView)findViewById(R.id.textView_NumRadicado);
        nomCliente = (TextView)findViewById(R.id.textView_NomCliente);
        txtdemandado = (TextView)findViewById(R.id.textView_Demandado);
        txtjuzgadoActual = (TextView)findViewById(R.id.textView_JuzgadoActual);
        txtultimaActuacion = (TextView)findViewById(R.id.textView_UltimaActuacion);

        ediUltimActuacion = (EditText)findViewById(R.id.edit_UltimaActuacion_Modificar);

        numRadicado.setText("#Radicado: "+ Constantes.numRadicado);
        nomCliente.setText("Demandante: "+Constantes.demandante);
        txtdemandado.setText("Demandado: "+Constantes.demandado);
        txtjuzgadoActual.setText("Juzgado Actual: "+Constantes.juzgadoActual);
        txtultimaActuacion.setText("Ultima Actuacion: "+Constantes.ultimaActuacion);

        ediUltimActuacion.setText(Constantes.ultimaActuacion);

        imgfoto = (ImageView)findViewById(R.id.imageFoto_Modificar);

        random = new Random();

        imgfoto.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                LanzarFoto();
            }
        });
    }

    public void LanzarFoto()
    {
        random.nextInt();
        numeroAleatorio = random.nextInt(1000000000);

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        nombreFoto = Environment.getExternalStorageDirectory()+"/"+numeroAleatorio+".jpeg";
        photo = new File(nombreFoto);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(photo));
        imageUri = Uri.fromFile(photo);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK)
                {
                    Uri selectedImage = imageUri;
                    getContentResolver().notifyChange(selectedImage, null);
                    ContentResolver cr = getContentResolver();
                    Bitmap bitmap;
                    try
                    {
                        bitmap = android.provider.MediaStore.Images.Media
                                .getBitmap(cr, selectedImage);

                        imgfoto.setImageBitmap(bitmap);
                        imgfoto.setContentDescription(selectedImage.toString());


                    }
                    catch (Exception e)
                    {
                        Toast.makeText(this, "La Foto No Se Cargo", Toast.LENGTH_SHORT)
                                .show();
                        Log.e("Camera", e.toString());
                    }
                }
        }
    }

    class UploaderFoto extends AsyncTask
    {
        String miFoto = "";
        String respStr;

        @Override
        protected Object doInBackground(Object[] params)
        {
            miFoto = String.valueOf(params[0]);

            try
            {

                HttpClient httpclient = new DefaultHttpClient();
                httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                HttpPost httppost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/upload.php");


                File file = new File(miFoto);
                MultipartEntity mpEntity = new MultipartEntity();
                ContentBody foto = new FileBody(file, "image/jpeg");
                mpEntity.addPart("fotoUp", foto);
                httppost.setEntity(mpEntity);
                HttpResponse resp = httpclient.execute(httppost);

                respStr = EntityUtils.toString(resp.getEntity());

                httpclient.getConnectionManager().shutdown();

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o)
        {
            super.onPostExecute(o);
            Toast.makeText(Informacion.this, "Datos Modificados con Exito", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Informacion.this, MenuPrincipal.class);
            startActivity(intent);
            finish();
        }
    }



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

        switch (item.getItemId())
        {
            case R.id.editar_Cliente:
                layoutConsultar.setVisibility(View.GONE);
                layoutEditar.setVisibility(View.VISIBLE);
                menuEditar.setVisible(false);
                menuGuardar.setVisible(true);
                break;

            case R.id.guardar_Cliente:
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
        LayoutInflater inflaterAlert = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialoglayout = inflaterAlert.inflate(R.layout.alertadeconfirmacion, null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialoglayout);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                String capActuacion = ediUltimActuacion.getText().toString();

                String nomFoto = String.valueOf(numeroAleatorio)+".jpeg";

                TareaUpdadte tareaUpdadte = new TareaUpdadte();
                tareaUpdadte.execute(capActuacion,nomFoto);

                Toast.makeText(Informacion.this,"Cargando Por Favor Espere..",Toast.LENGTH_LONG).show();


            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.setCancelable(false);

        builder.show();
    }

    //Clases Asyntask para modificar datos de la tabla productos
    private class TareaUpdadte extends AsyncTask<String,Integer,Boolean>
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
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse resp= httpClient.execute(httpPost);

                respStr = EntityUtils.toString(resp.getEntity());

                JSONObject respJSON = new JSONObject(respStr);
                JSONArray objItems = respJSON.getJSONArray("items");

                respuesta= String.valueOf(objItems);

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

        protected void onPostExecute(Boolean result)
        {
            //Toast.makeText(Perfil.this, respStr, Toast.LENGTH_SHORT).show();

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
