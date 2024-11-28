package com.example.vetcare.adaptadores;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vetcare.R;
import com.example.vetcare.clases.VistaCita;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.ViewHolder> {
 private List<VistaCita> listaCitas;

 public CitaAdapter(List<VistaCita> listaCitas){
     this.listaCitas=listaCitas;
 }

    @NonNull
    @Override
    public CitaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
     View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mis_citas,parent,false);
     return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CitaAdapter.ViewHolder holder, int position) {
        VistaCita vistaCita= listaCitas.get(position);
        byte[] foto= vistaCita.getFoto();
        Bitmap bitmap= BitmapFactory.decodeByteArray(foto,0,foto.length);
        holder.imgFoto.setImageBitmap(bitmap);
        holder.lblMascota.setText(vistaCita.getNombreMascota());
        holder.lblServicio.setText(vistaCita.getServicio());
        holder.lblVeterinario.setText(vistaCita.getVeterinario());
        holder.lblSede.setText(vistaCita.getSede());
        Date fechaHora= vistaCita.getFecha();
        DateFormat formato= new SimpleDateFormat("dd-MM-yyyy");
        holder.lblFecha.setText(formato.format(fechaHora));
        holder.lblHora.setText(vistaCita.getHorario());
    }
    @Override
    public int getItemCount() {
        return listaCitas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardItemCita;
        ImageView imgFoto;
        TextView lblServicio, lblVeterinario, lblSede, lblFecha,lblHora, lblMascota;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardItemCita= itemView.findViewById(R.id.itemCardItemCita);
            imgFoto= itemView.findViewById(R.id.itemImgFoto);
            lblServicio=itemView.findViewById(R.id.itmLblServicio);
            lblVeterinario=itemView.findViewById(R.id.itemlblVeterinario);
            lblSede=itemView.findViewById(R.id.itemlblSede);
            lblFecha=itemView.findViewById(R.id.itemlblFecha);
            lblHora=itemView.findViewById(R.id.itemlblHora);
            lblMascota=itemView.findViewById(R.id.itemlblMascota);
        }

    }
}
