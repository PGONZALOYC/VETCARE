package com.example.vetcare.fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vetcare.R;
import com.example.vetcare.modelo.Cita;
import com.example.vetcare.modelo.Mascota;
import com.example.vetcare.modelo.Usuario;
import com.example.vetcare.modelo.Veterinario;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReservarCitaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReservarCitaFragment extends Fragment implements View.OnClickListener{
    private CalendarView calendReserva;
    private Spinner cboReservaMascota, cboReservaServicio, cboReservaVeterinario, cboReservaHora, cboReservaSede;
    private Button btnReservarCita;
    private TextView lblVeterinario;
    private boolean estadoVeterinario=false;

    boolean conexionExitosa = false;
    private ProgressDialog progressDialog;
    private Toast toastActual;

    Usuario usuarioPerfil;
    List<Mascota> mascotasPerfil;
    List<Veterinario> veterinariosList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ReservarCitaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReservarCitaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReservarCitaFragment newInstance(String param1, String param2) {
        ReservarCitaFragment fragment = new ReservarCitaFragment();
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
        View view = inflater.inflate(R.layout.fragment_reservar_cita, container, false);

        // Inicializar vistas
        calendReserva = view.findViewById(R.id.calendReserva);
        cboReservaMascota= view.findViewById(R.id.resCboReservaMascota);
        cboReservaServicio = view.findViewById(R.id.resCboReservaServicio);
        cboReservaHora = view.findViewById(R.id.resCboReservaHora);
        cboReservaSede = view.findViewById(R.id.resCboReservaSede);
        btnReservarCita = view.findViewById(R.id.resBtnReservarCita);
        lblVeterinario= view.findViewById(R.id.lblReservarVeterinario);
        cboReservaVeterinario =view.findViewById(R.id.resCboReservaVeterinario);
        btnReservarCita.setOnClickListener(this);

        cboReservaServicio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long longitud) {
                String servicioSeleccionado = adapterView.getItemAtPosition(posicion).toString();
                if (servicioRequiereVeterinario(servicioSeleccionado)) {
                    estadoVeterinario=true;
                    mostrarSpinnerVeterinario(estadoVeterinario);
                } else {
                    mostrarSpinnerVeterinario(false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new ReservarCitaFragment.ConexionTask().execute();
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

                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Llenar los spinners estáticos
        llenarServicios();
        llenarHora();
        llenarSede();

        return view;
    }

    @Override
    public void onClick(View view) {

    }









    private boolean servicioRequiereVeterinario(String servicio) {
        return servicio.equals("Consulta Médica") || servicio.equals("Castración") || servicio.equals("Desparasitación");
    }

    private void mostrarSpinnerVeterinario(boolean mostrar) {
        if (mostrar){
            lblVeterinario.setVisibility(View.VISIBLE);
            cboReservaVeterinario.setVisibility(View.VISIBLE);
        }else {
            lblVeterinario.setVisibility(View.GONE);
            cboReservaVeterinario.setVisibility(View.GONE);
        }
    }

    private void llenarServicios() {
        String[] servicios = {"--Seleccione Servicio--", "Baño", "Corte", "Consulta Médica", "Castración", "Desparasitación"};
        cboReservaServicio.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, servicios));
    }

    private void llenarHora() {
        List<String> horas = new ArrayList<>();
        horas.add("--Seleccione Hora--");

        for (int hora = 9; hora <= 18; hora++) {
            String horaStr = (hora < 10) ? "0" + hora : String.valueOf(hora);
            horas.add(horaStr + ":00");
            if (hora != 18) { // Agregar solo hasta las 16:30
                horas.add(horaStr + ":30");
            }
        }
        cboReservaHora.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, horas));
    }

    private void llenarSede() {
        String[] sedes = {"--Seleccione Sede--", "San Juan de Lurigancho", "Breña", "Chorrillos", "Los Olivos"};
        cboReservaSede.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, sedes));
    }

    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    private class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            Usuario usuario = new Usuario();
            Mascota mascota = new Mascota();
            Veterinario veterinario= new Veterinario();
            int cnx = 0;
            // Obtener el correo del usuario desde SharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
            String correo = sharedPreferences.getString("correo", null); // Si no existe, será¡ null
            //Almacenar todas las variables necesarias antes del cnx 1
            usuarioPerfil = usuario.obtenerInformacionUsuario(correo);
            mascotasPerfil = mascota.obtenerMascotasPorCorreo(correo);
            veterinariosList= veterinario.obtenerVeterinarios();
            if(usuario != null && mascota != null){
                cnx = 1;
            }
            return cnx;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                hideLoadingDialog();
                conexionExitosa = true;
                llenarMascotas(mascotasPerfil);
                llenarVeterinarios(veterinariosList);
            } else{
                hideLoadingDialog();
                mostrarToast("Error: Conexion fallida");
            }
        }
    }

    // Método para llenar el Spinner de mascotas con la lista de mascotas del usuario
    private void llenarMascotas(List<Mascota> mascotas) {
        List<String> nombresMascotas = new ArrayList<>();
        nombresMascotas.add("--Seleccione Mascota--"); // Opción predeterminada

        for (Mascota mascota : mascotas) {
            nombresMascotas.add(mascota.getNombre() +" - "+mascota.getTipo()); // Obtener el nombre de cada mascota
        }
        // Configurar el adapter del Spinner con la lista de nombres de mascotas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombresMascotas);
        cboReservaMascota.setAdapter(adapter);
    }


    // Método para llenar el Spinner de mascotas con la lista de Veterinarios
    private void llenarVeterinarios(List<Veterinario> veterinariosList) {
        List<String> nombresVeterinarios = new ArrayList<>();
        nombresVeterinarios.add("--Seleccione Veterinario--"); // Opción predeterminada

        for (Veterinario veterinario : veterinariosList) {
            nombresVeterinarios.add(veterinario.getNombre() +" "+ veterinario.getApellidos()); // Obtener el nombre de cada Veterinario
        }
        // Configurar el adapter del Spinner con la lista de nombres de mascotas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombresVeterinarios);
        cboReservaVeterinario.setAdapter(adapter);
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