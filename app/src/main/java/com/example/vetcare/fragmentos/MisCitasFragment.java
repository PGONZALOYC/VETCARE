package com.example.vetcare.fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vetcare.R;
import com.example.vetcare.modelo.Cita;
import com.example.vetcare.modelo.Sede;
import com.example.vetcare.modelo.Usuario;
import com.example.vetcare.modelo.Veterinario;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MisCitasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MisCitasFragment extends Fragment {
    private TextView citLblFechaCita, citLblDetalleCita;

    boolean conexionExitosa = false;
    private ProgressDialog progressDialog;
    private Toast toastActual;

    ArrayList<Cita> citasList;
    Usuario usuarioPerfil;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MisCitasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MisCitasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MisCitasFragment newInstance(String param1, String param2) {
        MisCitasFragment fragment = new MisCitasFragment();
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
        View view = inflater.inflate(R.layout.fragment_mis_citas, container, false);
        //Inicializar vistas
        citLblFechaCita = view.findViewById(R.id.citLblFechaCita);
        citLblDetalleCita = view.findViewById(R.id.citLblDetalleCita);

        new MisCitasFragment.ConexionTask().execute();
        showLoadingDialog();
        // Ejecutar tareas en un hilo separado
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    freezeExecution();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //CODIGO DESPUES DEL CONGELAMIENTO
                            if (conexionExitosa) {

                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return view;
    }


    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    private class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            Usuario usuario = new Usuario();
            Cita cita = new Cita();
            int cnx = 0;
            // Obtener el correo del usuario desde SharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
            String correo = sharedPreferences.getString("correo", null); // Si no existe, será¡ null
            //Almacenar todas las variables necesarias antes del cnx 1
            usuarioPerfil = usuario.obtenerInformacionUsuario(correo);
            citasList = cita.obtenerCitasPorCorreo(usuarioPerfil.getCorreo());
            if (usuarioPerfil != null && citasList != null) {
                cnx = 1;

                // Mostrar las citas en el Log
                for (Cita c : citasList) {
                    Log.d("Cita", "ID Cita: " + c.getIdCita() +
                            ", Fecha: " + c.getFecha() +
                            ", Servicio: " + c.getServicio() +
                            ", Mascota: "+ c.getMascota() +
                            ", Sede ID: " + c.getSede()+
                            ", Veterinario: " + c.getVeterinario() +
                            ", Estado: " + c.getEstado());
                }
            }
            return cnx;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                hideLoadingDialog();
                conexionExitosa = true;
            } else {
                hideLoadingDialog();
                mostrarToast("Error: Conexion fallida");
            }
        }
    }

    //METODOS IMPLEMENTADOS PARA LA CARGA DESDE LA BASE DE DATOS
    //Congela los procesos mientras espera que conexionExitosa sea true para continuar con las posteriores instrucciones
    private void freezeExecution() throws InterruptedException {
        while (!conexionExitosa) {
            Thread.sleep(100); // Esperar un breve periodo antes de volver a comprobar
        }
    }

    //Dialogo de carga mientras espera al congelamiento -- mostrar
    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false); // No se puede cancelar tocando fuera del diÃ¡logo
        progressDialog.show();
    }

    //Dialogo de carga mientras espera al congelamiento -- ocultar
    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    //Método para desplegar Toasts sin esperar a que termine el anterior toast (lo reemplaza)
    private void mostrarToast(String message) {
        if (toastActual != null) {
            toastActual.cancel();
        }
        toastActual = Toast.makeText(this.getContext(), message, Toast.LENGTH_SHORT);
        toastActual.show();
    }
}
