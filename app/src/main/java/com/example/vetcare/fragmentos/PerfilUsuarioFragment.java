package com.example.vetcare.fragmentos;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;
import com.example.vetcare.sqlite.Vetcare;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilUsuarioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilUsuarioFragment extends Fragment {

    EditText perTxtNombre,perTxtApellido,perTxtTelefono,perTxtCorreo;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilUsuarioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilUsuarioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilUsuarioFragment newInstance(String param1, String param2) {
        PerfilUsuarioFragment fragment = new PerfilUsuarioFragment();
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
        //return inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        // Inflate the layout for this fragment


        View vista = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        perTxtNombre = vista.findViewById(R.id.perTxtNombre);
        perTxtApellido= vista.findViewById(R.id.perTxtApellido);
        perTxtTelefono = vista.findViewById(R.id.perTxtTelefono);
        perTxtCorreo = vista.findViewById(R.id.perTxtCorreo);

        View infoMastoca = vista.findViewById(R.id.btnInfoMascota);
        View agreMascota= vista.findViewById(R.id.btnAgregarMascota);

        infoMastoca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = getActivity();
                ((Menu)activity).onClickMenu(5);
            }
        });
        agreMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = getActivity();
                ((Menu)activity).onClickMenu(7);
            }
        });

        //mostrarInformacion();

        // Obtener los datos del usuario
        Vetcare vt = new Vetcare(getContext());
        Cursor cursor = vt.obtenerDatosUsuario();
        if (cursor != null && cursor.moveToFirst()) {
            // Colocar los datos en los campos correspondientes
            perTxtCorreo.setText(cursor.getString(0));   // Correo
            perTxtNombre.setText(cursor.getString(1));  // Nombres
            perTxtApellido.setText(cursor.getString(2)); // Apellidos
            perTxtTelefono.setText(cursor.getString(3)); // Teléfono
        }
        cursor.close();

        return vista;
    }

    private void mostrarInformacion() {

    }
}