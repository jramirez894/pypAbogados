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
    //Declaracion de Variables
    EditText documento;
    EditText contrasena;
    Button iniciar;

    //Variable booleana que se encargara de verificar si el usuario existe
    boolean existe = false;

    //Variable para capturar la respuesta del servidor;
    String respuesta = "";

    //Varriables para la animacion cargando
    ProgressBar progressLogin;
    View layoutProgress;
    View layoutMain;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Variables para ocultar la parte superior de la pantalla
        ActionBar actionBar= getSupportActionBar();
        actionBar.hide();

        //Se relacionan las variables con el id que existe en la parte grafica
        documento =(EditText)findViewById(R.id.editNum_Documento);
        contrasena =(EditText)findViewById(R.id.editContrasena);
        iniciar =(Button)findViewById(R.id.buttonIniciarSesion);

        //Animacion Vista Login
        progressLogin = (ProgressBar) findViewById(R.id.progressLogin);

        //Animacion Vistas del login
        layoutProgress = (View) findViewById(R.id.layoutProgress);
        layoutMain = (View) findViewById(R.id.layout_MainActivity);

        //Click del boton
        iniciar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //Se capturar las textos en las variables de tipo String para verificar si estan Vacias y si no se pone
                //en funcion la animacion y se coneccta con la tarea para hacer la conexion enviando como parametro los dos textos
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
        //Variable que guardara la respuesta del servidor
        private String respStr;
        //Animacion de la pantalla
        int progreso;

        @TargetApi(Build.VERSION_CODES.KITKAT)
        protected Boolean doInBackground(String... params)
        {
            //Declaramos la variables con las que vamos hacer la conexion y le asignamos la url a la que nos vamos a conectar
            boolean resul = true;
            HttpClient httpClient;
            List<NameValuePair> nameValuePairs;
            HttpPost httpPost;
            httpClient= new DefaultHttpClient();
            httpPost = new HttpPost("http://appjuzgado.pypabogados.com/Controller/ControllerLogin.php");

            //Enviamos las variables que fueron enviadas como parametros a la webservice con los campos que la webservice
            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("cedula", params[0]));
            nameValuePairs.add(new BasicNameValuePair("password", params[1]));
            nameValuePairs.add(new BasicNameValuePair("option",  "signInUsser"));

            try
            {
                //ejecutamos lo que va hacer enviado
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                //Capturamos la respuesta del servidor
                HttpResponse resp= httpClient.execute(httpPost);

                //Combertimos esa respuesta en un String
                respStr = EntityUtils.toString(resp.getEntity());

                //Combertimos ese String en un Json para poder aceeder a los mensajes que el servidor nos responde
                JSONObject respJSON = new JSONObject(respStr);
                JSONArray objItems = respJSON.getJSONArray("items");

                //Como la respuesta ya quedo en jSon la convertimos a un array para poder comparar que es lo que dice el mensaje
                respuesta= String.valueOf(objItems);

                //preguntamos si la respuesta es igual a no existe y cambiamos la variable existe
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
            //catch evitar errores cuando se haga la conexion
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

            //La animacion que va a durar hasta que el servidor de una respuesta
            while (progreso<100)
            {
                progreso++;
                publishProgress(progreso);
                SystemClock.sleep(20);
            }

            return resul;
        }

        //Metodos de la animacion
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

        //Metodo que se ejucuta cuando en la conexion no salio ningun error
        protected void onPostExecute(Boolean result)
        {
            //Toast.makeText(MainActivity.this,respStr,Toast.LENGTH_SHORT).show();

            //preguntamos si la variable existe que verdadera y si lo es nos abrira la siguiente interfaz
            if (existe)
            {
                //Lanzamos la otra actividad
                Intent intent = new Intent(MainActivity.this, MenuPrincipal.class);
                startActivity(intent);
                //Paramos la animacion
                layoutProgress.setVisibility(View.GONE);
                layoutMain.setVisibility(View.VISIBLE);
                //un Finish para que no se devuelva para esta actividad si no que se salga por completo
                finish();
            }
            //De haber quedado la variable existe en false nos saldra un mensaje y volvera a mostrar la pantalla inicial
            else
            {
                Toast.makeText(MainActivity.this, "El Usuario no Existe", Toast.LENGTH_SHORT).show();
                layoutProgress.setVisibility(View.GONE);
                layoutMain.setVisibility(View.VISIBLE);
            }
        }
    }
}
