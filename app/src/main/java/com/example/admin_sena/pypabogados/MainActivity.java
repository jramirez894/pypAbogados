package com.example.admin_sena.pypabogados;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity
{
    EditText documento;
    EditText contrasena;

    Button iniciar;

    boolean existe = false;

    String respuesta = "";

    ProgressBar progressLogin;
    View layoutProgress;
    View layoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();

        documento =(EditText)findViewById(R.id.editNum_Documento);
        contrasena =(EditText)findViewById(R.id.editContrasena);

        iniciar =(Button)findViewById(R.id.buttonIniciarSesion);

        //Vista Login
        progressLogin = (ProgressBar) findViewById(R.id.progressLogin);

        //Vistas del login
        layoutProgress = (View) findViewById(R.id.layoutProgress);
        layoutMain = (View) findViewById(R.id.layout_MainActivity);

        iniciar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                String capDocumento = documento.getText().toString();
                String capContrasena = contrasena.getText().toString();
                if (capDocumento.equals("") ||
                        capContrasena.equals("")) {
                    Toast.makeText(MainActivity.this, "Faltan Datos Por Llenar", Toast.LENGTH_SHORT).show();
                } else
                {
                    layoutProgress.setVisibility(View.VISIBLE);
                    layoutMain.setVisibility(View.GONE);
                    TareaLogin tareaLogin = new TareaLogin();
                    tareaLogin.execute(capDocumento, capContrasena);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    //Clases Asyntask para login y usuario que inicia sesion
    private class TareaLogin extends AsyncTask<String,Integer,Boolean>
    {
        private String respStr;
        int progreso;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        protected Boolean doInBackground(String... params)
        {
            boolean resul = true;
            HttpClient httpClient;
            List<NameValuePair> nameValuePairs;
            HttpPost httpPost;
            httpClient= new DefaultHttpClient();
            httpPost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/ControllerLogin.php");

            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cedula", params[0]));
            nameValuePairs.add(new BasicNameValuePair("password", params[1]));
            nameValuePairs.add(new BasicNameValuePair("option",  "signInUsser"));

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
            } catch (JSONException e) {
                e.printStackTrace();
            }

            while (progreso<100)
            {
                progreso++;
                publishProgress(progreso);
                SystemClock.sleep(20);
            }

            return resul;
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progreso = 0;
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
            progressLogin.setProgress(values[0]);
        }

        protected void onPostExecute(Boolean result)
        {
            //Toast.makeText(MainActivity.this,respStr,Toast.LENGTH_SHORT).show();

            if (existe)
            {
                Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                startActivity(intent);
                layoutProgress.setVisibility(View.GONE);
                layoutMain.setVisibility(View.VISIBLE);
                finish();
            }
            else
            {
                Toast.makeText(MainActivity.this, "El Usuario no Existe", Toast.LENGTH_SHORT).show();
                layoutProgress.setVisibility(View.GONE);
                layoutMain.setVisibility(View.VISIBLE);
            }
        }
    }
}
