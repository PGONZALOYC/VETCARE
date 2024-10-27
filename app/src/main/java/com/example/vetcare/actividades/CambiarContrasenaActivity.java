package com.example.vetcare.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetcare.R;
import com.example.vetcare.clases.Hash;
import com.example.vetcare.sqlite.Vetcare;

public class CambiarContrasenaActivity extends AppCompatActivity implements View.OnClickListener {
    EditText camTxtCambiarContrasenaNueva, camTxtCambiarContrasenaConfirmarNueva;
    Button camBtnCambiarContrasenaAceptar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cambiar_contrasena);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        camTxtCambiarContrasenaNueva = findViewById(R.id.camTxtCambiarContrasenaNueva);
        camTxtCambiarContrasenaConfirmarNueva = findViewById(R.id.camTxtCambiarContrasenaConfirmarNueva);
        camBtnCambiarContrasenaAceptar = findViewById(R.id.camBtnCambiarContrasenaAceptar);
        camBtnCambiarContrasenaAceptar.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.camBtnCambiarContrasenaAceptar) {
            //cambiarContrasena(); // Método para manejar el botón Enviar

            // Retornar a la actividad de sesión
            Intent intent = new Intent(this, SesionActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void cambiarContrasena() {

//        Intent intent = new Intent(this, SesionActivity.class);
//        startActivity(intent);
        String nuevaContrasena = camTxtCambiarContrasenaNueva.getText().toString();
        String confirmarContrasena = camTxtCambiarContrasenaConfirmarNueva.getText().toString();
        if (nuevaContrasena.equals(confirmarContrasena)) {
            // Cifrar la nueva contraseña
            Hash hash = new Hash();
            String nuevaContrasenaCifrada = hash.StringToHash(nuevaContrasena, "SHA256").toLowerCase();

            // Crear un objeto de la base de datos
            Vetcare vt = new Vetcare(getApplicationContext());

            // Actualizar la contraseña en la base de datos (puedes usar el ID o correo del usuario como referencia)
            if (vt.actualizarClave("dinamita@gmail.com", nuevaContrasenaCifrada)) {
                Toast.makeText(this, "Contraseña cambiada exitosamente", Toast.LENGTH_SHORT).show();
                // Retornar a la actividad de sesión
                Intent intent = new Intent(this, SesionActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Error al cambiar la contraseña", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
        }

    }
}