package com.example.vetcare.fragmentos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.vetcare.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Arrays;
import java.util.List;

public class VerMapaFragment extends Fragment {

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            //LatLng sydney = new LatLng(-34, 151);
            // Lista de ubicaciones de veterinarias con nombre y coordenadas
            List<LatLng> veterinarias = Arrays.asList(
                    new LatLng(-12.008373520602117, -77.00542555276013), // Veterinaria 1
                    new LatLng(-12.08520932154498, -77.04306758220875), // Veterinaria 2
                    new LatLng(-12.128590799478914, -77.00416898220882),  // Veterinaria 3
                    new LatLng(-12.05450068657127, -77.0477690748604)  // Veterinaria 4
            );
            // Lista de nombres para las veterinarias
            List<String> nombres = Arrays.asList(
                    "Vetcare SJL",
                    "Vetcare Lince",
                    "Vetcare Miraflores",
                    "Vetcare Breña"
            );
            // Ícono personalizado
            BitmapDescriptor iconoPersonalizado = BitmapDescriptorFactory.fromResource(R.drawable.marcador_mapa);
            // Agregar marcadores para cada veterinaria
            for (int i = 0; i < veterinarias.size(); i++) {
                googleMap.addMarker(new MarkerOptions()
                        .position(veterinarias.get(i))
                        .title(nombres.get(i))
                        .icon(iconoPersonalizado));
            }

            // Mover la cámara a la primera ubicación
            if (!veterinarias.isEmpty()) {
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(veterinarias.get(0), 12f)); // Zoom nivel 12
            }
            //googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ver_mapa, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

    }
}