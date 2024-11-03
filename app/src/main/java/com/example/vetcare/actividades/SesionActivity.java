package com.example.vetcare.actividades;

import android.content.Intent;
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

public class SesionActivity extends AppCompatActivity  implements View.OnClickListener {
    EditText txtCorreo, txtClave;
    Button btnIngresar, btnRegistrarse, btnSOS;
    CheckBox chkRecordar;
    TextView logTxtOlvidasteContrasena;
    boolean conexionExitosa = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sesion);

        new ConexionTask().execute();

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
            iniciarSesion(txtCorreo.getText().toString(), txtClave.getText().toString());
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

    private void iniciarSesion(String txtCorreo, String txtClave) {
        //Se creo la BD
        //objeto de la BD
        Vetcare vt = new Vetcare(getApplicationContext());
        //objeto de hash
        Hash hash = new Hash();
        //Cifrar la clave
        txtClave = hash.StringToHash(txtClave,"SHA256").toLowerCase();

//        // Cifrar la clave ingresada
//        txtClave = hash.StringToHash(txtClave, "SHA256").toLowerCase();
//
//        // Validar credenciales en base de datos
//        String claveGuardada = vt.getValue("clave"); // Obtener la clave guardada para el usuario
//
//        // Verificar si el usuario existe y comparar contraseñas
//        if (vt.usuarioAgregado() && claveGuardada != null && txtCorreo.equals("dinamita@gmail.com") && txtClave.equals(claveGuardada)) {
//            Intent bienvenida = new Intent(this, BienvenidaActivity.class);
//            bienvenida.putExtra("nombre", "Dinamita");
//            startActivity(bienvenida);
//            finish();
//        } else {
//            Toast.makeText(this, "Error: Credenciales incorrectas", Toast.LENGTH_LONG).show();
//        }

        // Validar credenciales en base de datos o lógica específica
        if (txtCorreo.equals("dinamita@gmail.com") && txtClave.equals("a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3")) {

            //Intent bienvenida = new Intent(this, ReservaCitaActivity.class);
            Intent bienvenida = new Intent(SesionActivity.this, BienvenidaActivity.class);
            bienvenida.putExtra("nombre", "Dinamita");
            if(chkRecordar.isChecked()){
                //Aca se guarda el correo y clave
                //vt.agregarUsuario(1,txtCorreo,txtClave);
            }
            //vt.agregarUsuario(1,txtCorreo,txtClave,"Arturo","Romero Gonzales","78459612","04/09/2003","948156147","Masculino");
            startActivity(bienvenida);
            finish();
        } else {
            Toast.makeText(SesionActivity.this, "Error: Credenciales incorrectas", Toast.LENGTH_LONG).show();
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
            MySQLConnector mySQLConnector = new MySQLConnector();
            int cnx = 0;
            if(mySQLConnector.conecta() != null){
                cnx = 1;
            }
            return cnx;

        }

        @Override
        protected void onPostExecute(Integer result) {
            // Muestra un Toast con el resultado de la conexión
            if (result == 1) {
                Toast.makeText(SesionActivity.this, "Conexión exitosa", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(SesionActivity.this, "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        }
    }
}