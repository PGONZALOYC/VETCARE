package com.example.vetcare.fragmentos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NosotrosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NosotrosFragment extends Fragment implements View.OnClickListener {

//    private static final int PERMISSION_REQUEST_LOCATION = 1;
//    private static final int PERMISSION_CODE_LOCATION_PERMISSION = 1;
//    private MapView mapView;
//    private FusedLocationProviderClient fusedLocationClient;

    Button nosBtnEncuentranos;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NosotrosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NosotrosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NosotrosFragment newInstance(String param1, String param2) {
        NosotrosFragment fragment = new NosotrosFragment();
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
        //return inflater.inflate(R.layout.fragment_nosotros, container, false);
        View vista = inflater.inflate(R.layout.fragment_nosotros, container, false);

        nosBtnEncuentranos = vista.findViewById(R.id.nosBtnEncuentranos);
        //nosBtnEncuentranos.setOnClickListener(this);
        nosBtnEncuentranos.setOnClickListener(this);
        return vista;
    }


    @Override
    public void onClick(View v) {
        if(v.getId()== R.id.nosBtnEncuentranos){
//            Intent intent = new Intent(getActivity(), VerMapaActivity.class);
//            startActivity(intent);
            Activity activity = getActivity();
            ((Menu)activity).onClickMenu(9);
        }
    }
}