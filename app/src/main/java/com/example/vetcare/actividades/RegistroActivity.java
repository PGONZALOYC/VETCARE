package com.example.vetcare.actividades;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.example.vetcare.clases.Hash;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;
import com.example.vetcare.fragmentos.AgregarMascotaFragment;
import com.example.vetcare.modelo.Mascota;
import com.example.vetcare.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener{
    EditText regTxtRegistroNombre, regTxtRegistroApellido, regTxtRegistroDni,regTxtRegistroFechaNacimiento, regTxtRegistroTelefono, regTxtRegistroCorreo, regTxtRegistroContrasena, regTxtRegistroConfirmarContrasena;
    RadioGroup regGrpRegistroSexo;
    RadioButton regRbtRegistroSinDefinir, regRbtRegistroFemenino, regRbtRegistroMasculino;
    CheckBox regChkRegistroTerminos;
    ImageButton imageButtonSiguiente;
    boolean conexionExitosa = false;
    private static Toast toastActual;
    private ProgressDialog progressDialog;
    String dni="";
    String nombre="";
    String apellido="";


    View vista;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        regTxtRegistroNombre = findViewById(R.id.regTxtRegistroNombre);
        regTxtRegistroApellido = findViewById(R.id.regTxtRegistroApellido);
        regTxtRegistroDni = findViewById(R.id.regTxtRegistroDni);
        regTxtRegistroFechaNacimiento = findViewById(R.id.regTxtRegistroFechaNacimiento);
        regTxtRegistroTelefono = findViewById(R.id.regTxtRegistroTelefono);
        regTxtRegistroCorreo = findViewById(R.id.regTxtRegistroCorreo);
        regTxtRegistroContrasena = findViewById(R.id.regTxtRegistroContrasena);
        regTxtRegistroConfirmarContrasena = findViewById(R.id.regTxtRegistroConfirmarContrasena);

        regGrpRegistroSexo = findViewById(R.id.regGrpRegistroSexo);
        regRbtRegistroSinDefinir = findViewById(R.id.regRbtRegistroSinDefinir);
        regRbtRegistroMasculino = findViewById(R.id.regRbtRegistroMasculino);
        regRbtRegistroFemenino = findViewById(R.id. regRbtRegistroFemenino);
        regChkRegistroTerminos = findViewById(R.id.regChkRegistroTerminos);

        regTxtRegistroFechaNacimiento.setOnClickListener(this);
        regChkRegistroTerminos.setOnClickListener(this);

        // Initialize ImageButton and set onClick listener
        imageButtonSiguiente = findViewById(R.id.imageButtonSiguiente);
        imageButtonSiguiente.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imageButtonSiguiente) {
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
                                if (conexionExitosa) {
                                    Toast.makeText(RegistroActivity.this, "Usuario registrado", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(RegistroActivity.this, SesionActivity.class);
                                    startActivity(intent);
                                    finish();

                                }else{
                                    Toast.makeText(RegistroActivity.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (view.getId() == R.id.regTxtRegistroFechaNacimiento) {
            SeleccionarFecha();
        } else if (view.getId() == R.id.regChkRegistroTerminos) {
            mostrarTerminos();
        }
    }

    private boolean validarFormulario() {
        // 1. Validar que ningún campo esté vacío
        if (regTxtRegistroNombre.getText().toString().trim().isEmpty() ||
                regTxtRegistroApellido.getText().toString().trim().isEmpty() ||
                regTxtRegistroDni.getText().toString().trim().isEmpty() ||
                regTxtRegistroFechaNacimiento.getText().toString().trim().isEmpty() ||
                regTxtRegistroCorreo.getText().toString().trim().isEmpty() ||
                regTxtRegistroContrasena.getText().toString().trim().isEmpty() ||
                regTxtRegistroConfirmarContrasena.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos.", Toast.LENGTH_LONG).show();
            return false;
        }

        // 2. Validar formato del correo electrónico
        String correo = regTxtRegistroCorreo.getText().toString().trim();
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            Toast.makeText(this, "Correo electrónico no válido.", Toast.LENGTH_LONG).show();
            return false;
        }

        // 3. Validar formato del DNI (suponiendo que el DNI debe tener 8 dígitos)
        String dni = regTxtRegistroDni.getText().toString().trim();
        if (!dni.matches("\\d{8}")) {
            Toast.makeText(this, "DNI debe tener 8 dígitos.", Toast.LENGTH_LONG).show();
            return false;
        }

        // 4. Validar que la clave y la clave2 sean iguales
        String clave = regTxtRegistroContrasena.getText().toString().trim();
        String clave2 = regTxtRegistroConfirmarContrasena.getText().toString().trim();
        if (!clave.equals(clave2)) {
            Toast.makeText(this, "Las contraseñas no coinciden.", Toast.LENGTH_LONG).show();
            return false;
        }

        // 5. Validar que se haya aceptado los términos
        if (!regChkRegistroTerminos.isChecked()) {
            Toast.makeText(this, "Debes aceptar los términos y condiciones.", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void mostrarTerminos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Términos y Condiciones");
        builder.setMessage("Términos y Condiciones de Uso\n" +
                "Actualizado el 01-09-2024\n" +
                "\n" +
                "Bienvenido a Vetcare. Al acceder y utilizar nuestra aplicación móvil, aceptas los siguientes términos y condiciones. " +
                "Si no estás de acuerdo con estos términos, por favor no utilices la app.\n" +
                "\n" +
                "1. Aceptación de los Términos\n" +
                "\n" +
                "Al descargar, instalar o usar Vetcare, aceptas estos términos y condiciones y nuestra política de privacidad. " +
                "Si no aceptas estos términos, no debes usar la app.\n" +
                "\n" +
                "2. Uso de la App\n" +
                "\n" +
                "2.1 Licencia de Uso: Te otorgamos una licencia no exclusiva, intransferible y revocable para usar la app en tu dispositivo " +
                "móvil conforme a estos términos.\n" +
                "\n" +
                "2.2 Restricciones: No puedes modificar, reproducir, distribuir, vender, o crear trabajos derivados de la app sin nuestro " +
                "consentimiento previo por escrito. Tampoco debes usar la app para fines ilegales o no autorizados.\n" +
                "\n" +
                "3. Registro y Seguridad\n" +
                "\n" +
                "3.1 Cuenta de Usuario: Para acceder a ciertas funciones, debes crear una cuenta proporcionando información veraz y completa. " +
                "Eres responsable de mantener la confidencialidad de tu cuenta y contraseña.\n" +
                "\n" +
                "3.2 Seguridad: Nos reservamos el derecho de suspender o cancelar tu cuenta si sospechamos que se está utilizando de manera " +
                "fraudulenta o en violación de estos términos.\n" +
                "\n" +
                "4. Contenido de Usuario\n" +
                "\n" +
                "4.1 Responsabilidad del Contenido: Eres el único responsable del contenido que publiques o transmitas a través de la app. " +
                "No publicaremos ni aprobaremos contenido que sea ilegal, ofensivo o que viole los derechos de terceros.\n" +
                "\n" +
                "4.2 Licencia de Contenido: Al publicar contenido en la app, nos otorgas una licencia mundial, no exclusiva, libre de regalías " +
                "y sublicenciable para usar, reproducir y distribuir dicho contenido.\n" +
                "\n" +
                "5. Propiedad Intelectual\n" +
                "\n" +
                "Todos los derechos de propiedad intelectual sobre la app y su contenido, incluyendo marcas registradas, derechos de autor y patentes, " +
                "pertenecen a Vetcare o a sus licenciantes.\n" +
                "\n" +
                "6. Modificaciones de la App y Términos\n" +
                "\n" +
                "Nos reservamos el derecho de modificar o interrumpir la app en cualquier momento, así como de actualizar estos términos. Las " +
                "modificaciones entrarán en vigor en cuanto se publiquen en la app. Tu uso continuado de la app después de dichas modificaciones implica " +
                "tu aceptación de los nuevos términos.\n" +
                "\n" +
                "7. Limitación de Responsabilidad\n" +
                "\n" +
                "La app se proporciona \"tal cual\" y \"según disponibilidad\". No garantizamos que la app estará libre de errores o que funcionará " +
                "sin interrupciones. En la máxima medida permitida por la ley, no seremos responsables de ningún daño indirecto, incidental o consecuente " +
                "que surja del uso o la imposibilidad de uso de la app.\n" +
                "\n" +
                "8. Ley Aplicable\n" +
                "\n" +
                "Estos términos se rigen por las leyes de Perú. Cualquier disputa que surja en relación con estos términos será resuelta en los tribunales " +
                "competentes de Lima.\n" +
                "\n" +
                "9. Contacto\n" +
                "\n" +
                "Si tienes preguntas sobre estos términos, puedes contactarnos en vetcare_contact@vetcareapp.com o en Av.El Sol 461 SJL, Lima.\n" +
                "\n" +
                "10. Terminación\n" +
                "\n" +
                "Podemos suspender o terminar tu acceso a la app si incumples estos términos o por cualquier motivo que consideremos necesario " +
                "para proteger la integridad de la app.\n");
        regChkRegistroTerminos.setChecked(false);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                regChkRegistroTerminos.setChecked(true);
                dialog.dismiss();
            }
        });
        AlertDialog terminos = builder.create();
        terminos.setCancelable(false);
        terminos.setCanceledOnTouchOutside(false);
        terminos.show();
    }

    private void SeleccionarFecha() {
        DatePickerDialog dpd;
        final Calendar fechaActual = Calendar.getInstance();
        int dia = fechaActual.get(Calendar.DAY_OF_MONTH); //1...28|29|30|31
        int mes = fechaActual.get(Calendar.MONTH);        //0..11
        int anio = fechaActual.get(Calendar.YEAR);        //2024
        dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int y, int m, int d) {
                regTxtRegistroFechaNacimiento.setText(y+"-"+((m+1)< 10?"0"+(m+1):(m+1))+"-"+(d<10?"0"+d:d));
            }
        },anio,mes,dia);
        dpd.show();
    }
    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    public class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            SharedPreferences sharedPreferences = getSharedPreferences("Sistema", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            int cnx = 0;
            Usuario usuarioDAO = new Usuario();
            Hash hash = new Hash();
            //SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
            String dni = regTxtRegistroDni.getText().toString();
            String nombre = regTxtRegistroNombre.getText().toString();
            String apellido = regTxtRegistroApellido.getText().toString();
            String telefono = regTxtRegistroTelefono.getText().toString();
            String correo = regTxtRegistroCorreo.getText().toString();
            String contrasena = regTxtRegistroContrasena.getText().toString();
            String contrasenaHash = hash.StringToHash(contrasena, "SHA-256").toLowerCase();
            Date fechaNacimiento = Date.valueOf(regTxtRegistroFechaNacimiento.getText().toString().trim());
            String sexo = "";
            int selectedId = regGrpRegistroSexo.getCheckedRadioButtonId();
            if (selectedId == R.id.regRbtRegistroMasculino) {
                sexo = "Masculino";
            } else if (selectedId == R.id.regRbtRegistroFemenino) {
                sexo = "Femenino";
            } else {
                sexo = "Sin definir";
            }
            String txtCorr;
            String txtClav;
            if(!sharedPreferences.getString("correo", "-").equals("-")){
                txtCorr = sharedPreferences.getString("correo", "-");
            } else{
                txtCorr = regTxtRegistroNombre.getText().toString();
            }

            if(!sharedPreferences.getString("clave", "-").equals("-")){
                txtClav = sharedPreferences.getString("clave", "-");
            } else{
                txtClav = regTxtRegistroContrasena.getText().toString();
            }

            if(usuarioDAO.agregarUsuario(dni, nombre, apellido, telefono, correo,contrasenaHash,null,fechaNacimiento,sexo)){
                //guardarCorreoEnSharedPreferences(usuarioDAO.obtenerInformacionUsuario(txtCorr).getId_Usuario(), usuarioDAO.obtenerInformacionUsuario(txtCorr).getNombres(), usuarioDAO.obtenerInformacionUsuario(txtCorr).getApellidos(), usuarioDAO.obtenerInformacionUsuario(txtCorr).getTelefono(), usuarioDAO.obtenerInformacionUsuario(txtCorr).getCorreo(), usuarioDAO.obtenerInformacionUsuario(txtCorr).getContraseña());
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
        ArrayList<Mascota> listaMascotas = mascotaDAO.obtenerMascotasPorCorreo(correo);
        insertarMascotasEnSharedPreferences(listaMascotas);

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

    public void insertarMascotasEnSharedPreferences(ArrayList<Mascota> lista) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Crear un objeto Gson con la configuración de @Expose
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(lista);

        // Guardar el JSON en SharedPreferences
        editor.putString("listaMascotas", json);
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
        toastActual = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toastActual.show();
    }

    //Dialogo de carga mientras espera al congelamiento -- mostrar
    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando usuario...");
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