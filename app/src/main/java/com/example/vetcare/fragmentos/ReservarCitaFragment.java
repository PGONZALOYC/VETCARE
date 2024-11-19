package com.example.vetcare.fragmentos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;
import com.example.vetcare.modelo.Cita;
import com.example.vetcare.modelo.Mascota;
import com.example.vetcare.modelo.Sede;
import com.example.vetcare.modelo.Usuario;
import com.example.vetcare.modelo.Veterinario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

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

    ArrayList<Mascota> mascotasList;
    ArrayList<Veterinario> veterinariosList;
    ArrayList<Cita> citasList;
    ArrayList<Sede> sedesList;
    Cita nuevaCita;


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
        lblVeterinario= view.findViewById(R.id.resLblReservarVeterinario);
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
                llenarVeterinario();
                llenarHoras();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mascotasList = obtenerListaMascotaEnSharedPreferences();
        veterinariosList = obtenerListaVeterinariosEnSharedPreferences();
        citasList = obtenerListaCitasEnSharedPreferences();
        sedesList = obtenerListaSedesEnSharedPreferences();

        limitarCalendario();
        llenarMascotas();
        llenarServicios();
        llenarSede();
        llenarHoras();
        llenarVeterinario();

        btnReservarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                                        if (conexionExitosa) {
                                            mostrarToast(nuevaCita.getIdVeterinario()+"");
                                        }
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }

        });


        return view;
    }

    @Override
    public void onClick(View view) {

    }

    private void limitarCalendario() {
        Date now = new Date(); // Fecha y hora actuales

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setTimeZone(TimeZone.getTimeZone("America/Lima")); // Zona horaria de Perú

        try {
            // Convertir la fecha actual en milisegundos
            String today = sdf.format(now);
            Date parsedDate = sdf.parse(today);
            if (parsedDate != null) {
                calendReserva.setMinDate(parsedDate.getTime());
            }

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(now);
            calendar.add(Calendar.DAY_OF_MONTH, 7); // Añade un día
            String unaSemanaDespues = sdf.format(calendar.getTime());
            Date parsedDate2 = sdf.parse(unaSemanaDespues);
            if (parsedDate2 != null) {
                //mostrarToast(unaSemanaDespues);
                calendReserva.setMaxDate(parsedDate2.getTime());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean servicioRequiereVeterinario(@NonNull String servicio) {
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

    // Método para llenar el Spinner de mascotas con la lista de mascotas del usuario
    private void llenarMascotas() {
        ArrayList<String> nombresMascotas = new ArrayList<>();
        //nombresMascotas.add("--Seleccione Mascota--"); // Opción predeterminada

        for (Mascota mascota : mascotasList) {
            nombresMascotas.add(mascota.getNombre() +" - "+mascota.getTipo()); // Obtener el nombre de cada mascota
        }
        // Configurar el adapter del Spinner con la lista de nombres de mascotas
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombresMascotas);
        cboReservaMascota.setAdapter(adapter);
    }

    private void llenarServicios() {
        String[] servicios = {"Baño", "Corte", "Consulta Médica", "Castración", "Desparasitación"};
        cboReservaServicio.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, servicios));
    }

    private void llenarHoras() {
        ArrayList<String> horarios = new ArrayList<>();
        if(cboReservaVeterinario.getVisibility()==View.VISIBLE && cboReservaVeterinario.getSelectedItem()!=null){
            Collections.addAll(horarios,"9:00-10:30", "10:30-12:00", "12:00-13:30", "14:00-15:30", "15:30-17:00", "17:00-18:30");
            String formattedDate;
            Date now = new Date(); // Fecha y hora actuales

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setTimeZone(TimeZone.getTimeZone("America/Lima")); // Zona horaria de Perú

            String today = sdf.format(now);
            int id_vetSeleccionado = -1;
            for (Cita c : citasList) {
                for (int i = 0; i < veterinariosList.size(); i++){
                    if(cboReservaVeterinario.getSelectedItem() != null){
                        if(cboReservaVeterinario.getSelectedItem().toString() == veterinariosList.get(i).getNombre()){
                            id_vetSeleccionado = veterinariosList.get(i).getId_Veterinario();
                            break;
                        }
                    }

                }
                if(id_vetSeleccionado == c.getIdVeterinario()){
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(c.getFecha());
                    calendar.add(Calendar.DAY_OF_MONTH, 1); // Añade un día

                    // Obtener la nueva fecha con un día añadido
                    formattedDate = sdf.format(calendar.getTime());

                    if(formattedDate.equals(today)){
                        switch (c.getHoraInicio()){
                            case "9:00":
                                if(horarios.contains("9:00-10:30")){
                                    horarios.remove("9:00-10:30");
                                }
                                break;
                            case "10:30":
                                if(horarios.contains("10:30-12:00")){
                                    horarios.remove("10:30-12:00");
                                }
                                break;
                            case "12:00":
                                if(horarios.contains("12:00-13:30")){
                                    horarios.remove("12:00-13:30");
                                }
                                break;
                            case "14:00":
                                if(horarios.contains("14:00-15:30")){
                                    horarios.remove("14:00-15:30");
                                }
                                break;
                            case "15:30":
                                if(horarios.contains("15:30-17:00")){
                                    horarios.remove("15:30-17:00");
                                }
                                break;
                            case "17:00":
                                if(horarios.contains("17:00-18:30")){
                                    horarios.remove("17:00-18:30");
                                }
                                break;
                        }
                    }
                }

            }
        } else if(cboReservaVeterinario.getVisibility()==View.GONE){
            Collections.addAll(horarios,"9:00-10:30", "10:30-12:00", "12:00-13:30", "14:00-15:30", "15:30-17:00", "17:00-18:30");
        }
        cboReservaHora.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, horarios));
    }

    private void llenarVeterinario() {
        ArrayList<String> nombresVeterinarios = new ArrayList<>();
        int id_sedeSeleccionada = -1;
        for (Veterinario vet : veterinariosList) {
            for (int i = 0; i < sedesList.size(); i++){
                if(cboReservaSede.getSelectedItem().toString() == sedesList.get(i).getNombre()){
                    id_sedeSeleccionada = sedesList.get(i).getId_Sede();
                    break;
                }
            }
            if(id_sedeSeleccionada == vet.getId_Sede()){
                nombresVeterinarios.add(vet.getNombre()+" "+vet.getApellidos());
            }

        }
        cboReservaVeterinario.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombresVeterinarios));
        cboReservaVeterinario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long longitud) {
                llenarHoras();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void llenarSede() {
        ArrayList<String> nombresSedes = new ArrayList<>();

        for (Sede sede : sedesList) {
            nombresSedes.add(sede.getNombre());
        }
        cboReservaSede.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nombresSedes));
        cboReservaSede.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int posicion, long longitud) {
                llenarVeterinario();
                llenarHoras();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            int cnx = 0;
            Cita citaDAO = new Cita();

            int c_id_usuario;
            java.sql.Date c_fecha;
            int c_id_mascota = -1;
            String c_servicio;
            int c_id_veterinario = -1;
            String c_estado;
            int c_id_sede = -1;
            String c_horaInicio;

            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);

            c_id_usuario = sharedPreferences.getInt("id_usuario", -1);

            long fechaEnMilisegundos = calendReserva.getDate(); // Supongo que esto es un long
            Date fecha = new Date(fechaEnMilisegundos);
            java.sql.Date fechaSql = new java.sql.Date(fecha.getTime());

            c_fecha = fechaSql;

            for(Mascota v : mascotasList){
                if(cboReservaMascota.getSelectedItem().toString().equals(v.getNombre() +" - "+v.getTipo())){
                    c_id_mascota = v.getId_Mascota();
                    break;
                }
            }

            c_servicio = cboReservaServicio.getSelectedItem().toString();

            for(Veterinario v : veterinariosList){
                if(cboReservaVeterinario.getSelectedItem().toString().equals(v.getNombre()+" "+v.getApellidos())){
                    c_id_veterinario = v.getId_Veterinario();
                    break;
                }
            }

            c_estado = "Pendiente";

            for(Sede v : sedesList){
                if(cboReservaSede.getSelectedItem().toString().equals(v.getNombre())){
                    c_id_sede = v.getId_Sede();
                }
            }

            switch (cboReservaHora.getSelectedItem().toString()){
                case "9:00-10:30":
                    c_horaInicio = "9:00";
                    break;
                case "10:30-12:00":
                    c_horaInicio = "10:30";
                    break;
                case "12:00-13:30":
                    c_horaInicio = "12:00";
                    break;
                case "14:00-15:30":
                    c_horaInicio = "14:00";
                    break;
                case "15:30-17:00":
                    c_horaInicio = "15:30";
                    break;
                case "17:00-18:30":
                    c_horaInicio = "17:00";
                    break;
                default:
                    c_horaInicio = "";
            }

            nuevaCita = new Cita(c_id_usuario, c_id_mascota, c_id_sede, c_id_veterinario, c_servicio, c_horaInicio, c_fecha, c_estado);

            if(citaDAO.agregarCita(nuevaCita)){
                cnx = 1;
            }
            return cnx;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if (result == 1) {
                hideLoadingDialog();
                conexionExitosa = true;
            } else{
                hideLoadingDialog();
                mostrarToast("Error en la conexión "+"Mascota: "+nuevaCita.getIdMascota()+" Sede: "+nuevaCita.getIdSede()+" Veterinario: "+nuevaCita.getIdVeterinario()+" Servicio: "+nuevaCita.getServicio()+" Hora: "+nuevaCita.getHoraInicio()+" Fecha: "+nuevaCita.getFecha()+" Estado: "+nuevaCita.getEstado());
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

    //METODOS CONSULTORES DEL SHARED PREFERENCES
    public ArrayList<Mascota> obtenerListaMascotaEnSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);

        // Recuperar el JSON de SharedPreferences
        String json = sharedPreferences.getString("listaMascotas", null);

        if (json != null) {
            // Convertir el JSON de nuevo a ArrayList<Mascota>
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            TypeToken<ArrayList<Mascota>> typeToken = new TypeToken<ArrayList<Mascota>>() {};
            return gson.fromJson(json, typeToken.getType());
        } else {
            return new ArrayList<>();  // Retorna una lista vacía si no hay Mascota almacenados
        }
    }

    public ArrayList<Veterinario> obtenerListaVeterinariosEnSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);

        // Recuperar el JSON de SharedPreferences
        String json = sharedPreferences.getString("listaVeterinarios", null);

        if (json != null) {
            // Convertir el JSON de nuevo a ArrayList<Mascota>
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            TypeToken<ArrayList<Veterinario>> typeToken = new TypeToken<ArrayList<Veterinario>>() {};
            return gson.fromJson(json, typeToken.getType());
        } else {
            return new ArrayList<>();  // Retorna una lista vacía si no hay Mascota almacenados
        }
    }

    public ArrayList<Cita> obtenerListaCitasEnSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);

        // Recuperar el JSON de SharedPreferences
        String json = sharedPreferences.getString("listaCitasGenerales", null);

        if (json != null) {
            // Convertir el JSON de nuevo a ArrayList<Mascota>
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            TypeToken<ArrayList<Cita>> typeToken = new TypeToken<ArrayList<Cita>>() {};
            return gson.fromJson(json, typeToken.getType());
        } else {
            return new ArrayList<>();  // Retorna una lista vacía si no hay Mascota almacenados
        }
    }

    public ArrayList<Sede> obtenerListaSedesEnSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);

        // Recuperar el JSON de SharedPreferences
        String json = sharedPreferences.getString("listaSedes", null);

        if (json != null) {
            // Convertir el JSON de nuevo a ArrayList<Mascota>
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            TypeToken<ArrayList<Sede>> typeToken = new TypeToken<ArrayList<Sede>>() {};
            return gson.fromJson(json, typeToken.getType());
        } else {
            return new ArrayList<>();  // Retorna una lista vacía si no hay Mascota almacenados
        }
    }
}