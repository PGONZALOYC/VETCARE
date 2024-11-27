package com.example.vetcare.actividades;

import static android.app.PendingIntent.getActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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

import com.example.vetcare.modelo.Cita;
import com.example.vetcare.modelo.Mascota;
import com.example.vetcare.modelo.Producto;
import com.example.vetcare.modelo.Sede;
import com.example.vetcare.modelo.Veterinario;
import com.example.vetcare.sqlite.Vetcare;
import com.example.vetcare.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.example.vetcare.servicios.FloatingButtonService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class SesionActivity extends AppCompatActivity  implements View.OnClickListener {
    static EditText txtCorreo, txtClave;
    Button btnIngresar, btnRegistrarse, btnSOS;
    CheckBox chkRecordar;
    TextView logTxtOlvidasteContrasena;
    boolean conexionExitosa = false;
    private static Toast toastActual;
    private ProgressDialog progressDialog;
    ArrayList<Mascota> listaMascotas;
    ArrayList<Veterinario> listaVeterinarios;
    ArrayList<Cita> listaCitas;
    ArrayList<Sede> listaSedes;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sesion);

        SharedPreferences sharedPreferences = getSharedPreferences("Sistema", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

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

        // Boton flotante
        if (!Settings.canDrawOverlays(this)) {
            // Solicitar permiso para mostrar vistas flotantes
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, 101); // 101 es un REQUEST_CODE arbitrario
        } else {
            // Iniciar el servicio flotante si el permiso ya está concedido
            Intent intent = new Intent(this, FloatingButtonService.class);
            startService(intent);
        }


        if(sharedPreferences.getBoolean("recuerda", false)){
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) { // El mismo REQUEST_CODE que usaste al solicitar el permiso
            if (Settings.canDrawOverlays(this)) {
                // Permiso concedido, iniciar el servicio flotante
                Intent intent = new Intent(this, FloatingButtonService.class);
                startService(intent);
            } else {
                // Permiso denegado, mostrar mensaje al usuario
                Toast.makeText(this, "Permiso necesario para mostrar el botón flotante", Toast.LENGTH_SHORT).show();
            }
        }
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
            redirigirSOS(); // Método para manejar el botón SOS
        }
    }


    private void iniciarSesion() {
        // Validar credenciales en base de datos o lógica específica
        if (conexionExitosa) {
            //Intent bienvenida = new Intent(this, ReservaCitaActivity.class);
            Intent bienvenida = new Intent(SesionActivity.this, BienvenidaActivity.class);
            //bienvenida.putExtra("nombre", "Dinamita");
            SharedPreferences sharedPreferences = getSharedPreferences("Sistema", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            insertarMascotasEnSharedPreferences();
            insertarVeterinariosEnSharedPreferences();
            insertarCitasEnSharedPreferences();
            insertarSedesEnSharedPreferences();

            if(chkRecordar.isChecked()){
                editor.putString("correo", txtCorreo.getText().toString());
                editor.putString("clave", txtClave.getText().toString());
                editor.putBoolean("recuerda", true);
                editor.apply();
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
    public class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getSharedPreferences("Sistema", MODE_PRIVATE);
            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            int cnx = 0;
            Usuario usuarioDAO = new Usuario();

            Hash hash = new Hash();
            String txtCorr;
            String txtClav;

            if(!sharedPreferences.getString("correo", "-").equals("-")){
                txtCorr = sharedPreferences.getString("correo", "-");
            } else{
                txtCorr = txtCorreo.getText().toString();
            }

            if(!sharedPreferences.getString("clave", "-").equals("-")){
                txtClav = sharedPreferences.getString("clave", "-");
            } else{
                txtClav = txtClave.getText().toString();
            }
            Usuario user = usuarioDAO.obtenerInformacionUsuario(txtCorr);
            guardarCorreoEnSharedPreferences(user.getId_Usuario(), user.getNombres(), user.getApellidos(), user.getTelefono(), user.getCorreo(), user.getContraseña());

            //Cifrar la clave
            txtClav = hash.StringToHash(txtClav,"SHA256").toLowerCase();
            if(usuarioDAO.loginUsuario(txtCorr, txtClav) && listaMascotas!=null && listaVeterinarios!=null && listaCitas!=null && listaSedes!=null){
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

    private void guardarCorreoEnSharedPreferences(int id_usuario, String nombre, String apellido, String telefono, String correo, String clave) {
        SharedPreferences sharedPreferences = getSharedPreferences("Sistema", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Mascota mascotaDAO = new Mascota();
        Veterinario veterinarioDAO = new Veterinario();
        Cita citaDAO = new Cita();
        Sede sedeDAO = new Sede();
        listaMascotas = mascotaDAO.obtenerMascotasPorCorreo(correo);
        listaVeterinarios = veterinarioDAO.obtenerVeterinarios();
        listaCitas = citaDAO.obtenerCitas();
        listaSedes = sedeDAO.obtenerSedes();

        if(!sharedPreferences.getBoolean("recuerda", false)){
            editor.putInt("id_usuario", id_usuario);
            editor.putString("nombre", nombre);
            editor.putString("apellido", apellido);
            editor.putString("telefono", telefono);
            editor.putString("correo", correo);
            editor.putString("clave", clave);
            editor.apply();
        }
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

    public void insertarMascotasEnSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Crear un objeto Gson con la configuración de @Expose
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(listaMascotas);

        // Guardar el JSON en SharedPreferences
        editor.putString("listaMascotas", json);
        editor.apply();
    }

    public void insertarVeterinariosEnSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Crear un objeto Gson con la configuración de @Expose
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(listaVeterinarios);

        // Guardar el JSON en SharedPreferences
        editor.putString("listaVeterinarios", json);
        editor.apply();
    }

    public void insertarCitasEnSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Crear un objeto Gson con la configuración de @Expose
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(listaCitas);

        // Guardar el JSON en SharedPreferences
        editor.putString("listaCitasGenerales", json);
        editor.apply();
    }

    public void insertarSedesEnSharedPreferences() {
        SharedPreferences sharedPreferences = getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Crear un objeto Gson con la configuración de @Expose
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(listaSedes);

        // Guardar el JSON en SharedPreferences
        editor.putString("listaSedes", json);
        editor.apply();
    }

    private void redirigirSOS(){
        //nunmero de la veterinaria
        String numVetCare= "tel:928270448";
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse(numVetCare));

        startActivity(callIntent);
    }
}