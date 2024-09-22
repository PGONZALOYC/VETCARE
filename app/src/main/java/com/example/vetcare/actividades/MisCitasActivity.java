package com.example.vetcare.actividades;

import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.vetcare.R;

public class MisCitasActivity extends AppCompatActivity implements View.OnClickListener {
    CalendarView calendMisCitas;
    TextView lblfechaCita,lblFechaProxima;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mis_citas);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        calendMisCitas = findViewById(R.id.calendMisCitas);
        lblfechaCita = findViewById(R.id.lblfechaCita);
    }
    public void onSelectedDayChange(CalendarView view, int month, int dayOfMonth) {
        String actualdate = month + "/" + dayOfMonth;
        lblfechaCita.setText(actualdate);
    }



    @Override
    public void onClick(View view) {

    }
}