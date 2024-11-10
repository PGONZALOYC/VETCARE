package com.example.vetcare.fragmentos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CategProductosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CategProductosFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CategProductosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProductosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CategProductosFragment newInstance(String param1, String param2) {
        CategProductosFragment fragment = new CategProductosFragment();
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
        View vista = inflater.inflate(R.layout.fragment_categproductos, container, false);
        View iconoComida = vista.findViewById(R.id.proIconoComida);
        View iconoHigiene = vista.findViewById(R.id.proIconoHigiene);
        View iconoJuguetes = vista.findViewById(R.id.proIconoJuguete);
        View iconoAccesorios = vista.findViewById(R.id.proIconoAccesorios);
        View vista2 = inflater.inflate(R.layout.fragment_productos, container, false);
        TextView titulo = vista2.findViewById(R.id.proLblProductosTitulo);

        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(view.equals(iconoComida)){
                    editor.putString("categoria", "Comida");
                    editor.apply();
                } else{
                    if (view.equals(iconoHigiene)){
                        editor.putString("categoria", "Higiene");
                        editor.apply();
                    } else{
                        if (view.equals(iconoJuguetes)){
                            editor.putString("categoria", "Juguetes");
                            editor.apply();
                        } else{
                            if (view.equals(iconoAccesorios)){
                                editor.putString("categoria", "Accesorios");
                                editor.apply();
                            }
                        }
                    }
                }
                openFragment();
            }
        };
        iconoComida.setOnClickListener(listener);
        iconoHigiene.setOnClickListener(listener);
        iconoJuguetes.setOnClickListener(listener);
        iconoAccesorios.setOnClickListener(listener);

        return vista;
    }

    private void openFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .add(R.id.menRelArea, new ProductosFragment())
                .commit();
    }
}