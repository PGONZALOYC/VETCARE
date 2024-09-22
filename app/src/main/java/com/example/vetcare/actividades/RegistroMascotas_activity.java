package com.example.vetcare.actividades;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetcare.R;

public class RegistroMascotas_activity extends AppCompatActivity implements View.OnClickListener {

    EditText txtNombreM, txtAnos, txtMeses;
    Spinner cboTipoMascota, cboTipoRaza;
    ImageButton imageButtonNext2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_mascotas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtAnos = findViewById(R.id.regTxtAnos);
        txtNombreM = this.<EditText>findViewById(R.id.regTxtNombreM);
        txtMeses = findViewById(R.id.regTxtMeses);

        cboTipoMascota = findViewById(R.id.regCboTipoMascota);
        cboTipoRaza = findViewById(R.id.regCboRazaMascota);

        imageButtonNext2 = findViewById(R.id.imageButtonNext2);
        imageButtonNext2.setOnClickListener(this);

        llenarTiposMascota();
        llenarRazasMascota();
    }

    private void llenarTiposMascota() {
        String[] tiposMascota = {"--Seleccione Tipo de Mascota", "Perro", "Gato", "Pez", "Hamster"};
        cboTipoMascota.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tiposMascota));
    }

    private void llenarRazasMascota() {
        String[] razasMascota = {"--Seleccione Raza", "Bulldog", "Pastor Alemán", "Chihuahua", "Siamés", "Persa", "Común", "Goldfish", "Betta", "Guppy", "Ruso", "Dwarf", "Campbell"};
        cboTipoRaza.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, razasMascota));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButtonNext2) {
            // Obtener los valores ingresados
            String nombre = txtNombreM.getText().toString().trim();
            String anos = txtAnos.getText().toString().trim();
            String meses = txtMeses.getText().toString().trim();
            String tipoMascota = cboTipoMascota.getSelectedItem().toString();
            String razaMascota = cboTipoRaza.getSelectedItem().toString();

            // Verificar que todos los campos estén completos y válidos
            if (nombre.isEmpty()) {
                txtNombreM.setError("Por favor, ingrese el nombre.");
            } else if (anos.isEmpty()) {
                txtAnos.setError("Por favor, ingrese los años.");
            } else if (meses.isEmpty()) {
                txtMeses.setError("Por favor, ingrese los meses.");
            } else if (cboTipoMascota.getSelectedItemPosition() == 0) {
                // Si no se ha seleccionado un tipo válido
                ((android.widget.TextView) cboTipoMascota.getSelectedView()).setError("Seleccione un tipo de mascota.");
            } else if (cboTipoRaza.getSelectedItemPosition() == 0) {
                // Si no se ha seleccionado una raza válida
                ((android.widget.TextView) cboTipoRaza.getSelectedView()).setError("Seleccione una raza.");
            } else {
                // Si todas las validaciones son correctas, avanzar a la siguiente actividad
                Intent intent = new Intent(RegistroMascotas_activity.this, ReservaCitaActivity.class);
                startActivity(intent);
            }
        }
     }
    }