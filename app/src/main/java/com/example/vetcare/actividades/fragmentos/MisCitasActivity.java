package com.example.vetcare.actividades.fragmentos;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;

import com.example.vetcare.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MisCitasActivity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MisCitasActivity extends Fragment implements View.OnClickListener{
    CalendarView calendMisCitas;
    TextView lblfechaCita,lblFechaProxima;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MisCitasActivity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MisCitasActivity.
     */
    // TODO: Rename and change types and number of parameters
    public static MisCitasActivity newInstance(String param1, String param2) {
        MisCitasActivity fragment = new MisCitasActivity();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mis_citas_activity, container, false);
        calendMisCitas = view.findViewById(R.id.calendMisCitas);
        lblfechaCita = view.findViewById(R.id.lblfechaCita);

        // Configuración del CalendarView
        calendMisCitas.setOnDateChangeListener((calendarView, year, month, dayOfMonth) -> {
            String actualDate = (month + 1) + "/" + dayOfMonth + "/" + year; // Añadir 1 al mes
            lblfechaCita.setText(actualDate);
        });

        return view;


    }


    @Override
    public void onClick(View view) {

    }
}
