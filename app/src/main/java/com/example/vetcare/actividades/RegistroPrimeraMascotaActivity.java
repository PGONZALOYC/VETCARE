package com.example.vetcare.actividades;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetcare.R;
import com.example.vetcare.modelo.Mascota;

import java.sql.Date;

public class RegistroPrimeraMascotaActivity extends AppCompatActivity implements View.OnClickListener{
    EditText regTxtRegistroPrimMascotaNombre, regTxtRegistroPrimMascotaAnios, regTxtRegistroPrimMascotaMeses;
    Spinner regCboRegistroPrimMascotaTipo, regCboRegistroPrimMascotaRaza;
    ImageButton mascIconoRegistroPrimMascota,imageButtonSiguiente;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_primera_mascota);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        regTxtRegistroPrimMascotaNombre = this.<EditText>findViewById(R.id.regTxtRegistroPrimMascotaNombre);
        regTxtRegistroPrimMascotaAnios = findViewById(R.id.regTxtRegistroPrimMascotaAnios);
        regTxtRegistroPrimMascotaMeses = findViewById(R.id.regTxtRegistroPrimMascotaMeses);
        regCboRegistroPrimMascotaTipo = findViewById(R.id.regCboRegistroPrimMascotaTipo);
        regCboRegistroPrimMascotaRaza = findViewById(R.id.regCboRegistroPrimMascotaRaza);
        mascIconoRegistroPrimMascota = findViewById(R.id.mascIconoRegistroPrimMascota);
        imageButtonSiguiente = findViewById(R.id.imageButtonSiguiente);

        imageButtonSiguiente.setOnClickListener(this);

        llenarTiposMascota();
        //llenarRazasMascota();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButtonSiguiente) {
            // Obtener los valores ingresados
            String nombrePrimeraMascota = regTxtRegistroPrimMascotaNombre.getText().toString().trim();
            String aniosPrimeraMascota = regTxtRegistroPrimMascotaAnios.getText().toString().trim();
            String mesesPrimeraMascota = regTxtRegistroPrimMascotaMeses.getText().toString().trim();
            String tipoMascotaPrimeraMascota = regCboRegistroPrimMascotaTipo.getSelectedItem().toString();
            String razaMascotaPrimeraMascota = regCboRegistroPrimMascotaRaza.getSelectedItem().toString();

            // Verificar que todos los campos estén completos y válidos
            if (nombrePrimeraMascota.isEmpty()) {
                regTxtRegistroPrimMascotaNombre.setError("Por favor, ingrese el nombre.");
            } else if (aniosPrimeraMascota.isEmpty()) {
                regTxtRegistroPrimMascotaAnios.setError("Por favor, ingrese los años.");
            } else if (mesesPrimeraMascota.isEmpty()) {
                regTxtRegistroPrimMascotaMeses.setError("Por favor, ingrese los meses.");
            } else if (regCboRegistroPrimMascotaTipo.getSelectedItemPosition() == 0) {
                // Si no se ha seleccionado un tipo válido
                ((android.widget.TextView) regCboRegistroPrimMascotaTipo.getSelectedView()).setError("Seleccione un tipo de mascota.");
            } else if (regCboRegistroPrimMascotaRaza.getSelectedItemPosition() == 0) {
                // Si no se ha seleccionado una raza válida
                ((android.widget.TextView) regCboRegistroPrimMascotaRaza.getSelectedView()).setError("Seleccione una raza.");
            } else {
                new RegistroMascotaTask().execute();
                Intent intent = new Intent(RegistroPrimeraMascotaActivity.this, BienvenidaActivity.class);
                startActivity(intent);

            }

            // Si todas las validaciones son correctas, avanzar a la siguiente actividad
//            Intent intent = new Intent(RegistroPrimeraMascotaActivity.this, BienvenidaActivity.class);
//            startActivity(intent);
        }
    }
    private void llenarTiposMascota() {
        String[] tipos = {"Seleccione el tipo de mascota", "Perro", "Gato"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(regTxtRegistroPrimMascotaMeses.getContext(), android.R.layout.simple_spinner_dropdown_item, tipos);
        regCboRegistroPrimMascotaTipo.setAdapter(adapterTipo);

        // Agregar un listener para detectar cambios en el tipo de mascota seleccionado
        regCboRegistroPrimMascotaTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String tipoSeleccionado = parentView.getItemAtPosition(position).toString();
                llenarRazasMascota(tipoSeleccionado); // Actualiza las razas según el tipo seleccionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Opcional: Puedes dejar el spinner de razas vacío si no hay selección
                regCboRegistroPrimMascotaRaza.setAdapter(new ArrayAdapter<>(RegistroPrimeraMascotaActivity.this, android.R.layout.simple_spinner_dropdown_item, new String[]{}));
            }
        });
    }

    private void llenarRazasMascota(String tipoSeleccionado) {
        String[] razas;

        // Definir las razas según el tipo de mascota
        switch (tipoSeleccionado) {
            case "Perro":
                razas = new String[]{"Seleccione la raza", "Labrador", "Bulldog", "Poodle", "Pastor Alemán", "Golden Retriever"};
                break;
            case "Gato":
                razas = new String[]{"Seleccione la raza", "Siames", "Persa", "Maine Coon", "Bengalí", "Ragdoll"};
                break;
            default:
                razas = new String[]{"Seleccione la raza"};
                break;
        }

        // Actualizar el spinner de razas con las razas correspondientes
        ArrayAdapter<String> adapterRaza = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, razas);
        regCboRegistroPrimMascotaRaza.setAdapter(adapterRaza); // Aquí se corrige el Spinner correcto
    }
    private class RegistroMascotaTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            // Instancia de la clase Mascota
            Mascota mascotaDAO = new Mascota();

            // Capturar datos del formulario
            SharedPreferences sharedPreferences = getSharedPreferences("Sistema", Context.MODE_PRIVATE);
            int idUsuario = sharedPreferences.getInt("id_usuario", +1);
            String nombre = regTxtRegistroPrimMascotaNombre.getText().toString().trim();
            String tipo = regCboRegistroPrimMascotaTipo.getSelectedItem().toString();
            String raza = regCboRegistroPrimMascotaRaza.getSelectedItem().toString();
            int edadAnios = Integer.parseInt(regTxtRegistroPrimMascotaAnios.getText().toString().trim());
            int edadMeses = Integer.parseInt(regTxtRegistroPrimMascotaMeses.getText().toString().trim());
            Date fechaNacimiento = new Date(System.currentTimeMillis()); // Puede ajustarse según la lógica de la app

            // Intentar agregar la mascota a la base de datos
            return mascotaDAO.agregarMascota(idUsuario, nombre, tipo, raza, fechaNacimiento, null, edadAnios, edadMeses);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                // Mascota registrada exitosamente
                Toast.makeText(RegistroPrimeraMascotaActivity.this, "Mascota registrada exitosamente", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegistroPrimeraMascotaActivity.this, BienvenidaActivity.class);
                startActivity(intent);
            } else {
                // Falló el registro
                Toast.makeText(RegistroPrimeraMascotaActivity.this, "Error al registrar la mascota", Toast.LENGTH_SHORT).show();
            }
        }
    }


}