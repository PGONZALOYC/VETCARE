package com.example.vetcare.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetcare.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReservaCitaActivity extends AppCompatActivity implements View.OnClickListener {
    CalendarView calendReserva;
    Spinner spinReservServicio, spinReservHora, spinReservSede;
    Button btnReservarCita;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reserva_cita);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        calendReserva = findViewById(R.id.calendReserva);
        spinReservServicio=findViewById(R.id.spinReservServicio);
        spinReservHora=findViewById(R.id.spinReservHora);
        spinReservSede=findViewById(R.id.spinReservSede);
        btnReservarCita=findViewById(R.id.btnReservarCita);

        btnReservarCita.setOnClickListener(this);

        llenarServicios();
        llenarHora();
        llenarSede();
    }
    private void llenarServicios(){
        String[] distritos = {"--Seleccione Servicio--", "Baño",
                "Corte", "Consulta Médica", "Castración", "Desparasitación"};
        spinReservServicio.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                distritos));
    }
    private void llenarHora(){
        List<String> horas = new ArrayList<>();
        horas.add("--Seleccione Hora--");

        // Generar horas desde las 7:30 hasta las 17:00
        for (int hora = 9; hora <= 18; hora++) {
            String horaStr = (hora < 10) ? "0" + hora : String.valueOf(hora);
            horas.add(horaStr + ":00");
            if (hora != 18) { // Agregar solo hasta las 16:30
                horas.add(horaStr + ":30");
            }
        }
        spinReservHora.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, horas));
    }
    private void llenarSede(){
        String[] distritos = {"--Seleccione Sede--", "San Juan de Lurigancho",
                "Breña", "Chorrillos", "Los Olivos"};
        spinReservSede.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item,
                distritos));
    }

    @Override
    public void onClick(View view) {

    }
}