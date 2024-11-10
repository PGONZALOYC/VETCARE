package com.example.vetcare.actividades;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetcare.R;
import com.example.vetcare.clases.Hash;
import com.example.vetcare.clases.MySQLConnector;
import com.example.vetcare.sqlite.Vetcare;
import com.example.vetcare.modelo.Usuario;

import java.util.concurrent.CountDownLatch;

public class SesionActivity extends AppCompatActivity  implements View.OnClickListener {
    EditText txtCorreo, txtClave;
    Button btnIngresar, btnRegistrarse, btnSOS;
    CheckBox chkRecordar;
    TextView logTxtOlvidasteContrasena;
    boolean conexionExitosa = false;
    private Toast toastActual;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sesion);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        txtCorreo = findViewById(R.id.logTxtCorreo);
        txtClave = findViewById(R.id.logTxtClave);
        btnIngresar = findViewById(R.id.logBtnIngresar);
        btnRegistrarse = findViewById(R.id.logBtnRegistrate); // Nuevo botón Registrarse
        btnSOS = findViewById(R.id.logBtnSOS); // Botón SOS
        chkRecordar = findViewById(R.id.logChkRecordar);
        logTxtOlvidasteContrasena = findViewById(R.id.logTxtOlvidasteContrasena);

        // Configurar listeners para los botones y el TextView
        btnIngresar.setOnClickListener(this);
        btnRegistrarse.setOnClickListener(this); // Listener para el botón Registrarse
        btnSOS.setOnClickListener(this); // Listener para el botón SOS
        logTxtOlvidasteContrasena.setOnClickListener(this); // Listener para el olvido de contraseña
    }

    @Override
    public void onClick(View v) {
// Usando if-else if para manejar los clics
        if (v.getId() == R.id.logBtnIngresar) {
            new ConexionTask().execute();

            showLoadingDialog();

            // Ejecutar tareas en un hilo separado
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        freezeExecution();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //CODIGO DESPUES DEL CONGELAMIENTO
                                iniciarSesion();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();



        }
        else if (v.getId() == R.id.logBtnRegistrate) {
            registrar(); // Método para manejar el botón Registrarse

        } else if (v.getId() == R.id.logTxtOlvidasteContrasena) {
            olvidasteContrasena(); // Método para manejar el olvido de contraseña
        }
        else if (v.getId() == R.id.logBtnSOS) {
            mostrarSOS(); // Método para manejar el botón SOS
        }
    }

    private void iniciarSesion() {
        //Se creo la BD
        //objeto de la BD
        Vetcare vt = new Vetcare(getApplicationContext());

        // Validar credenciales en base de datos o lógica específica
        if (conexionExitosa) {
            //Intent bienvenida = new Intent(this, ReservaCitaActivity.class);
            Intent bienvenida = new Intent(SesionActivity.this, BienvenidaActivity.class);
            //bienvenida.putExtra("nombre", "Dinamita");

            if(chkRecordar.isChecked()){
                //Aca se guarda el correo y clave
                //vt.agregarUsuario(1,txtCorreo,txtClave);
            }
            //vt.agregarUsuario(1,txtCorreo,txtClave,"Arturo","Romero Gonzales","78459612","04/09/2003","948156147","Masculino");
            startActivity(bienvenida);
            finish();
        }

    }

        private void registrar() {
        Intent registro = new Intent(this, RegistroActivity.class);
        startActivity(registro);
    }
//
    private void mostrarSOS() {
        // Mostrar mensaje de emergencia o realizar alguna acción de emergencia
        //Toast.makeText(this, "¡Emergencia SOS activada!", Toast.LENGTH_LONG).show();
    }
//
    private void olvidasteContrasena() {
        Intent olvidasteContrasena = new Intent(this, OlvidasteContrasenaActivity.class);
        startActivity(olvidasteContrasena);
    }

    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    private class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            Usuario usuario = new Usuario();
            int cnx = 0;
            Hash hash = new Hash();
            String txtClav = txtClave.getText().toString();
            //Cifrar la clave
            txtClav = hash.StringToHash(txtClav,"SHA256").toLowerCase();
            if(usuario.loginUsuario(txtCorreo.getText().toString(), txtClav)){
                guardarCorreoEnSharedPreferences(txtCorreo.getText().toString());
                cnx = 1;
            }
            return cnx;

        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                hideLoadingDialog();
                conexionExitosa = true;
            } else{
                hideLoadingDialog();
                mostrarToast("Error: Credenciales incorrectas");
            }
        }
    }

    private void guardarCorreoEnSharedPreferences(String correo) {
        SharedPreferences sharedPreferences = getSharedPreferences("CorreoGuardado", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("correo_usuario", correo);
        editor.apply();
    }

    //Congela los procesos mientras espera que conexionExitosa sea true para continuar con las posteriores instrucciones
    private void freezeExecution() throws InterruptedException {
        while (!conexionExitosa) {
            Thread.sleep(100); // Esperar un breve periodo antes de volver a comprobar
        }
    }

    //Método para desplegar Toasts sin esperar a que termine el anterior toast (lo reemplaza)
    private void mostrarToast(String message) {
        if (toastActual != null) {
            toastActual.cancel();
        }
        toastActual = Toast.makeText(SesionActivity.this, message, Toast.LENGTH_SHORT);
        toastActual.show();
    }

    //Dialogo de carga mientras espera al congelamiento -- mostrar
    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logueando...");
        progressDialog.setCancelable(false); // No se puede cancelar tocando fuera del diálogo
        progressDialog.show();
    }

    //Dialogo de carga mientras espera al congelamiento -- ocultar
    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}