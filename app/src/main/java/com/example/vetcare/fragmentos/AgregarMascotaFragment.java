package com.example.vetcare.fragmentos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;
import com.example.vetcare.modelo.Mascota;
import com.example.vetcare.modelo.Usuario;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.sql.Date;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AgregarMascotaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AgregarMascotaFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private Uri imageUri;
    EditText edtNombre, edtEdadAnios, edtEdadMeses;
    Spinner spinnerTipoMascota, spinnerRazaMascota;
    Button btnAgregarMascota;
    ImageButton btnSubirImagen;
    boolean conexionExitosa = false;
    private static Toast toastActual;
    private ProgressDialog progressDialog;
    View vista;
    int userID=-1;
    String nombre="";
    Date fechaNacimiento = new Date(System.currentTimeMillis());
    String tipo="";
    String raza="";
    int edadAnios=0;
    int edadMeses=0;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AgregarMascotaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AgregarMascotaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AgregarMascotaFragment newInstance(String param1, String param2) {
        AgregarMascotaFragment fragment = new AgregarMascotaFragment();
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
        //return inflater.inflate(R.layout.fragment_agregar_mascota, container, false);

        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_agregar_mascota, container, false);
        // Infla el layout del fragmento
        btnSubirImagen = vista.findViewById(R.id.agremascImgMascota);
        // Configuramos el click listener para el ImageButton
        btnSubirImagen.setOnClickListener(v -> showImageSelectionDialog());
        edtNombre = vista.findViewById(R.id.agremasEdtNombreMascota);
        edtEdadAnios = vista.findViewById(R.id.agremasEdtEdadAnios);
        edtEdadMeses = vista.findViewById(R.id.agremasEdtEdadMeses);
        spinnerTipoMascota = vista.findViewById(R.id.agremascSpinnerTipoMascota);
        spinnerRazaMascota = vista.findViewById(R.id.agremascSpinnerRazaMascota);
        //btnSubirImagen = vista.findViewById(R.id.agremascBtnSubirImagen);
        btnAgregarMascota = vista.findViewById(R.id.agremascBtnAgregarMascota);
        llenarTipoMascota();
        llenarTipoMascota();
        // Obtener el correo del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);


        btnAgregarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new AgregarMascotaFragment.ConexionTask().execute();

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
                                        Toast.makeText(getContext(), "Mascota registrada", Toast.LENGTH_SHORT).show();
                                        Activity activity = getActivity();
                                        ((Menu) activity).onClickMenu(4);

                                    }else{
                                        Toast.makeText(getContext(), "Error al registrar mascota", Toast.LENGTH_SHORT).show();
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
        return vista;
    }

    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Seleccionar foto")
                .setItems(new CharSequence[] {"Seleccionar de Galería", "Tomar Foto con Cámara"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            // Opción 1: Seleccionar desde la galería
                            openGallery();
                            break;
                        case 1:
                            // Opción 2: Tomar foto con la cámara
                            openCamera();
                            break;
                    }
                })
                .show();
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // Si es desde la galería
                imageUri = data.getData();
                // Puedes cargar la imagen en un ImageView, por ejemplo:
                btnSubirImagen.setImageURI(imageUri);
            } else if (requestCode == CAPTURE_IMAGE_REQUEST && data != null) {
                // Si es desde la cámara
                imageUri = data.getData();
                // Puedes cargar la imagen en un ImageView, por ejemplo:
                btnSubirImagen.setImageURI(imageUri);
            }
        } else {
            Toast.makeText(getContext(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show();
        }
    }



    //    private void llenarTipoMascota() {
//        String[] tipos = {"--Seleccione el tipo de mascota--", "Perro", "Gato", "Otro"};
//        spinnerTipoMascota.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, tipos));
//
//    }
//
//    private void llenarTipoRaza() {
//        String[] raza = {};
//        spinnerRazaMascota.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, tipos));
//
//    }
    private void llenarTipoMascota() {
        String[] tipos = {"Seleccione el tipo de mascota", "Perro", "Gato"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, tipos);
        spinnerTipoMascota.setAdapter(adapterTipo);

        // Agregar un listener para detectar cambios en el tipo de mascota seleccionado
        spinnerTipoMascota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String tipoSeleccionado = parentView.getItemAtPosition(position).toString();
                llenarTipoRaza(tipoSeleccionado); // Actualiza las razas según el tipo seleccionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Opcional: Puedes dejar el spinner de razas vacío si no hay selección
                spinnerRazaMascota.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, new String[]{}));
            }
        });
    }

    private void llenarTipoRaza(String tipoSeleccionado) {
        String[] razas;

        // Definir las razas según el tipo de mascota
        switch (tipoSeleccionado) {
            case "Perro":
                razas = new String[]{"Seleccione la raza", "Labrador", "Bulldog", "Poodle", "Pastor Alemán", "Golden Retriever"};
                break;
            case "Gato":
                razas = new String[]{"Seleccione la raza", "Siames", "Persa", "Maine Coon", "Bengalí", "Ragdoll"};
                break;
            default:
                razas = new String[]{"Seleccione la raza"};
                break;
        }

        // Actualizar el spinner de razas con las razas correspondientes
        ArrayAdapter<String> adapterRaza = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, razas);
        spinnerRazaMascota.setAdapter(adapterRaza);
    }

    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    public class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {

            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            int cnx = 0;
            //Usuario usuarioDAO = new Usuario();
            Mascota mascotaDAO = new Mascota();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
            nombre = edtNombre.getText().toString();
            tipo= spinnerTipoMascota.getSelectedItem().toString();
            raza= spinnerRazaMascota.getSelectedItem().toString();
            edadAnios = Integer.parseInt(edtEdadAnios.getText().toString());
            edadMeses = Integer.parseInt(edtEdadMeses.getText().toString());

            if(mascotaDAO.agregarMascota(sharedPreferences.getInt("id_usuario", -1), edtNombre.getText().toString(), tipo, raza, fechaNacimiento,null,edadAnios,edadMeses)){
                insertarMascotasEnSharedPreferences(mascotaDAO.obtenerMascotasPorCorreo(sharedPreferences.getString("correo", null)));
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
                mostrarToast("Error: Credenciales incorrectas");
            }
        }


    }

    public void insertarMascotasEnSharedPreferences(ArrayList<Mascota> lista) {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Crear un objeto Gson con la configuración de @Expose
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        String json = gson.toJson(lista);

        // Guardar el JSON en SharedPreferences
        editor.putString("listaMascotas", json);
        editor.apply();
    }
    //Congela los procesos mientras espera que conexionExitosa sea true para continuar con las posteriores instrucciones
    private void freezeExecution() throws InterruptedException {
        while (!conexionExitosa) {
            Thread.sleep(100); // Esperar un breve periodo antes de volver a comprobar
        }
    }

    //Método para desplegar Toasts sin esperar a que termine el anterior toast (lo reemplaza)
    private void mostrarToast(String message) {
        if (toastActual != null) {
            toastActual.cancel();
        }
        toastActual = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        toastActual.show();
    }

    //Dialogo de carga mientras espera al congelamiento -- mostrar
    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Registrando mascota...");
        progressDialog.setCancelable(false); // No se puede cancelar tocando fuera del diálogo
        progressDialog.show();
    }

    //Dialogo de carga mientras espera al congelamiento -- ocultar
    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

}