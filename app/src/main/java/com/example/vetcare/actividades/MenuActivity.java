package com.example.vetcare.actividades;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;
import com.example.vetcare.fragmentos.AgregarMascotaFragment;
import com.example.vetcare.fragmentos.DetalleProductosFragment;
import com.example.vetcare.fragmentos.MisCitasFragment;
import com.example.vetcare.fragmentos.NosotrosFragment;
import com.example.vetcare.fragmentos.PerfilMascotaFragment;
import com.example.vetcare.fragmentos.PerfilUsuarioFragment;
import com.example.vetcare.fragmentos.CategProductosFragment;
import com.example.vetcare.fragmentos.ProductosFragment;
import com.example.vetcare.fragmentos.ReservarCitaFragment;
import com.example.vetcare.fragmentos.VerMapaFragment;

public class MenuActivity extends AppCompatActivity implements Menu {
    Fragment[] fragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fragments = new Fragment[14];

        fragments[0] = new ReservarCitaFragment();
        fragments[1] = new CategProductosFragment();
        fragments[2] = new MisCitasFragment();
        fragments[3] = new NosotrosFragment();
        fragments[4] = new PerfilUsuarioFragment();
        fragments[5] = new PerfilMascotaFragment();
        fragments[6] = new ProductosFragment();
        fragments[7] = new AgregarMascotaFragment();
        fragments[8] = new DetalleProductosFragment();
        fragments[9] = new VerMapaFragment();

        int id = getIntent().getIntExtra("id", -1);
        onClickMenu(id);
    }

    @Override
    public void onClickMenu(int id) {
        FragmentManager fr = getSupportFragmentManager();
        FragmentTransaction ft = fr.beginTransaction();
        ft.replace(R.id.menRelArea, fragments[id]);
        ft.commit();
    }


}