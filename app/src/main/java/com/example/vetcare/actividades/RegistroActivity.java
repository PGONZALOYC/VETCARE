package com.example.vetcare.actividades;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetcare.R;

import java.util.Calendar;

public class RegistroActivity extends AppCompatActivity implements View.OnClickListener{
    EditText regTxtRegistroNombre, regTxtRegistroApellido, regTxtRegistroDni,regTxtRegistroFechaNacimiento, regTxtRegistroTelefono, regTxtRegistroCorreo, regTxtRegistroContrasena, regTxtRegistroConfirmarContrasena;
    RadioGroup regGrpRegistroSexo;
    RadioButton regRbtRegistroSinDefinir, regRbtRegistroFemenino, regRbtRegistroMasculino;
    CheckBox regChkRegistroTerminos;
    ImageButton imageButtonSiguiente;
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
            //Handle the click for the ImageButton
            if (validarFormulario()) {
                // If the form is valid, navigate to the next activity
                Intent intent = new Intent(RegistroActivity.this, RegistroPrimeraMascotaActivity.class);
                startActivity(intent);
            }
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
        builder.setMessage("Términos y Condiciones de Uso" +
                "Actualizado el 01-09-2024\n" +
                "\n" +
                "Bienvenido a Burros al Volante. Al acceder y utilizar nuestra" +
                " aplicación móvil, aceptas los siguientes términos y condiciones. " +
                "Si no estás de acuerdo con estos términos, por favor no utilices" +
                " la app.\n" +
                "\n" +
                "1. Aceptación de los Términos\n" +
                "\n" +
                "Al descargar, instalar o usar Burros al Volante, aceptas " +
                "estos términos y condiciones y nuestra política de privacidad." +
                " Si no aceptas estos términos, no debes usar la app.\n" +
                "\n" +
                "2. Uso de la App\n" +
                "\n" +
                "2.1 Licencia de Uso: Te otorgamos una licencia no exclusiva, " +
                "intransferible y revocable para usar la app en tu dispositivo " +
                "móvil conforme a estos términos.\n" +
                "\n" +
                "2.2 Restricciones: No puedes modificar, reproducir, distribuir, " +
                "vender, o crear trabajos derivados de la app sin nuestro " +
                "consentimiento previo por escrito. Tampoco debes usar la app " +
                "para fines ilegales o no autorizados.\n" +
                "\n" +
                "3. Registro y Seguridad\n" +
                "\n" +
                "3.1 Cuenta de Usuario: Para acceder a ciertas funciones, debes " +
                "crear una cuenta proporcionando información veraz y completa. " +
                "Eres responsable de mantener la confidencialidad de tu cuenta " +
                "y contraseña.\n" +
                "\n" +
                "3.2 Seguridad: Nos reservamos el derecho de suspender o cancelar " +
                "tu cuenta si sospechamos que se está utilizando de manera " +
                "fraudulenta o en violación de estos términos.\n" +
                "\n" +
                "4. Contenido de Usuario\n" +
                "\n" +
                "4.1 Responsabilidad del Contenido: Eres el único responsable " +
                "del contenido que publiques o transmitas a través de la app. " +
                "No publicaremos ni aprobaremos contenido que sea ilegal, " +
                "ofensivo o que viole los derechos de terceros.\n" +
                "\n" +
                "4.2 Licencia de Contenido: Al publicar contenido en la app, " +
                "nos otorgas una licencia mundial, no exclusiva, libre de regalías " +
                "y sublicenciable para usar, reproducir y distribuir dicho contenido.\n" +
                "\n" +
                "5. Propiedad Intelectual\n" +
                "\n" +
                "Todos los derechos de propiedad intelectual sobre la app y su contenido, " +
                "incluyendo marcas registradas, derechos de autor y patentes, pertenecen " +
                "a Burros Volante o a sus licenciantes.\n" +
                "\n" +
                "6. Modificaciones de la App y Términos\n" +
                "\n" +
                "Nos reservamos el derecho de modificar o interrumpir la app en " +
                "cualquier momento, así como de actualizar estos términos. Las " +
                "modificaciones entrarán en vigor en cuanto se publiquen en la app." +
                " Tu uso continuado de la app después de dichas modificaciones implica " +
                "tu aceptación de los nuevos términos.\n" +
                "\n" +
                "7. Limitación de Responsabilidad\n" +
                "\n" +
                "La app se proporciona \"tal cual\" y \"según disponibilidad\". " +
                "No garantizamos que la app estará libre de errores o que funcionará" +
                " sin interrupciones. En la máxima medida permitida por la ley, no " +
                "seremos responsables de ningún daño indirecto, incidental o consecuente " +
                "que surja del uso o la imposibilidad de uso de la app.\n" +
                "\n" +
                "8. Ley Aplicable\n" +
                "\n" +
                "Estos términos se rigen por las leyes de Perú. Cualquier disputa que " +
                "surja en relación con estos términos será resuelta en los tribunales " +
                "competentes de Lima/Lima.\n" +
                "\n" +
                "9. Contacto\n" +
                "\n" +
                "Si tienes preguntas sobre estos términos, puedes contactarnos en " +
                "burritos_volante@upn.pe o en Av El Sol 461, San Juan de Lurigancho 15434\n" +
                "\n" +
                "10. Terminación\n" +
                "\n" +
                "Podemos suspender o terminar tu acceso a la app si incumples " +
                "estos términos o por cualquier motivo que consideremos necesario " +
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

}