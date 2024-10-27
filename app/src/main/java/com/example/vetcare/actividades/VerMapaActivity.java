package com.example.vetcare.actividades;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.vetcare.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class VerMapaActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_LOCATION = 1;
    private MapView mapView;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_mapa);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Configuración del MapView
        mapView = findViewById(R.id.map); // Asegúrate de tener este ID en el XML
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Configurar controlador del mapa para centrarlo en una ubicación específica
        IMapController mapController = mapView.getController();
        mapController.setZoom(15);
        //Coodernada inicial
        GeoPoint startPoint = new GeoPoint(-12.0464, -77.0428);
        mapController.setCenter(startPoint);

        agregarUbicacion(-12.041524624060779, -77.00026887850325, "Vetcare", "057, El Agustino 15003");
        agregarUbicacion(-12.025087098841313, -77.00257098369009, "Vetcare", "Los Chasquis 792, Lima 15401");
        agregarUbicacion(-12.085208337346723, -77.04306864770136, "Vetcare", "Jirón Brigadier Mateo Pumacahua 2345, Lince 15073");
        agregarUbicacion(-12.054505634524197, -77.04776879975842, "Vetcare", "Jr. Jorge Chávez 454, Breña 15082");
        agregarUbicacion(-12.112815932371852, -77.04627090612688, "Vetcare", "Av. Mariscal La Mar 835, Miraflores 15074");
        agregarUbicacion(-12.131956625086582, -77.0181125469388, "Vetcare", "Av. República de Panamá 6584, Barranco 15047");


    }

    // Método para añadir un marcador personalizado
    private void agregarUbicacion(double latitud, double longitud, String titulo, String descripcion) {
        GeoPoint punto = new GeoPoint(latitud, longitud);
        Marker marcador = new Marker(mapView);
        marcador.setPosition(punto);
        marcador.setTitle(titulo);
        marcador.setSnippet(descripcion);

        // Convertir el icono a Drawable
        Drawable iconoPersonalizado = getResources().getDrawable(R.drawable.marcador_mapa, null);
        // Asignar el Drawable
        marcador.setIcon(iconoPersonalizado);

        // Añadir el marcador al mapa
        mapView.getOverlays().add(marcador);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }
}