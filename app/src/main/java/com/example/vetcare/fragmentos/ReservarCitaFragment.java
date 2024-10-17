package com.example.vetcare.fragmentos;

import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;

import com.example.vetcare.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservarCitaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservarCitaFragment extends Fragment implements View.OnClickListener{
    private CalendarView calendReserva;
    private Spinner cboReservaServicio, cboReservaHora, cboReservaSede;
    private Button btnReservarCita;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReservarCitaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReservarCitaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReservarCitaFragment newInstance(String param1, String param2) {
        ReservarCitaFragment fragment = new ReservarCitaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reservar_cita, container, false);

        // Inicializar vistas
        calendReserva = view.findViewById(R.id.calendReserva);
        cboReservaServicio = view.findViewById(R.id.resCboReservaServicio);
        cboReservaHora = view.findViewById(R.id.resCboReservaHora);
        cboReservaSede = view.findViewById(R.id.resCboReservaSede);
        btnReservarCita = view.findViewById(R.id.resBtnReservarCita);

        btnReservarCita.setOnClickListener(this);

        // Llenar los spinners
        llenarServicios();
        llenarHora();
        llenarSede();

         //Aplicar insets de la ventana
//        ViewCompat.setOnApplyWindowInsetsListener(view.findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });

        return view;
    }

    private void llenarServicios() {
        String[] servicios = {"--Seleccione Servicio--", "Baño", "Corte", "Consulta Médica", "Castración", "Desparasitación"};
        cboReservaServicio.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, servicios));
    }

    private void llenarHora() {
        List<String> horas = new ArrayList<>();
        horas.add("--Seleccione Hora--");

        for (int hora = 9; hora <= 18; hora++) {
            String horaStr = (hora < 10) ? "0" + hora : String.valueOf(hora);
            horas.add(horaStr + ":00");
            if (hora != 18) { // Agregar solo hasta las 16:30
                horas.add(horaStr + ":30");
            }
        }
        cboReservaHora.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, horas));
    }

    private void llenarSede() {
        String[] sedes = {"--Seleccione Sede--", "San Juan de Lurigancho", "Breña", "Chorrillos", "Los Olivos"};
        cboReservaSede.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, sedes));
    }

    @Override
    public void onClick(View view) {

    }
}