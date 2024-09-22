package com.example.vetcare.actividades.clases;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetcare.R;
import com.example.vetcare.actividades.ReservaCitaActivity;

import java.util.List;

public class MascotaAdapter extends RecyclerView.Adapter<MascotaAdapter.MascotaViewHolder> {

    private Context context;
    private List<Mascota> listaMascotas;

    public MascotaAdapter(Context context, List<Mascota> listaMascotas) {
        this.context = context;
        this.listaMascotas = listaMascotas;
    }

    @NonNull
    @Override
    public MascotaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mascotas, parent, false);
        return new MascotaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MascotaViewHolder holder, int position) {
        Mascota mascota = listaMascotas.get(position);
        holder.nombre.setText(mascota.getNombre());
        holder.tipo.setText(mascota.getTipoMascota());
        holder.foto.setImageResource(mascota.getFotoResId());

        holder.botonSeleccionar.setOnClickListener(v -> {
            // Lógica para seleccionar la mascota y pasar a la siguiente actividad
            Intent intent = new Intent(context, ReservaCitaActivity.class);
            intent.putExtra("mascota_nombre", mascota.getNombre());
            intent.putExtra("mascota-tipo",mascota.getTipoMascota());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaMascotas.size();
    }

    public static class MascotaViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, tipo;
        ImageView foto;
        Button botonSeleccionar;

        public MascotaViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.lblNombreSelecMascota);
            tipo= itemView.findViewById(R.id.lblTipoSelecMascota);
            foto = itemView.findViewById(R.id.imgvFotoSelecMascota);
            botonSeleccionar = itemView.findViewById(R.id.btnSelecMascota);
        }
    }
}
