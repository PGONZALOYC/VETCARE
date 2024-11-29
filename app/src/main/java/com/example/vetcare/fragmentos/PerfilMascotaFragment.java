package com.example.vetcare.fragmentos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;
import com.example.vetcare.modelo.Mascota;
import com.example.vetcare.modelo.Usuario;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilMascotaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilMascotaFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private Uri imageUri;
    //ImageButton btnSubirImagen;
    ImageButton mascIconoMascota;
    EditText mascTxtNombreMascota,mascTxtEdadAniosMascota,mascTxtEdadMesesMascota;
    Spinner mascCboTipoMascota, mascCboRazaMascota;
    //ImageButton mascIconoEditar;
    boolean escribe = true;
    int mascotaID=-1;
    String nombreMascota="";
    int edadAnios=0;
    int edadMeses=0;
    String tipoRecuperado="";
    String razaRecuperada="";
    byte[] imgPerfil = new byte[1024];
    boolean conexionExitosa = false;
    private static Toast toastActual;
    private ProgressDialog progressDialog;
    View vista;
    List<Mascota> listaMascotas;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilMascotaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilMascotaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilMascotaFragment newInstance(String param1, String param2) {
        PerfilMascotaFragment fragment = new PerfilMascotaFragment();
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
        vista = inflater.inflate(R.layout.fragment_perfil_mascota, container, false);
        mascIconoMascota=vista.findViewById(R.id.mascIconoMascota);
        mascIconoMascota.setOnClickListener(v -> showImageSelectionDialog());
        mascTxtNombreMascota=vista.findViewById(R.id.mascTxtNombreMascota);
        mascTxtEdadAniosMascota=vista.findViewById(R.id.mascTxtEdadAniosMascota);
        mascTxtEdadMesesMascota=vista.findViewById(R.id.mascTxtEdadMesesMascota);
        mascCboTipoMascota=vista.findViewById(R.id.mascCboTipoMascota);
        mascCboRazaMascota=vista.findViewById(R.id.mascCboRazaMascota);

        View editarPerfilMascota=vista.findViewById(R.id.mascIconoEditar);

        // Obtener el correo del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        mascotaID = sharedPreferences.getInt("mascota_id", -1);

        // Verificar si se ha encontrado una mascota seleccionada
        if (mascotaID != -1) {
            // Recuperar y mostrar los datos de la mascota

            nombreMascota = sharedPreferences.getString("mascota_nombre", "Nombre no disponible");
            edadAnios = sharedPreferences.getInt("mascota_edadAños", 0);
            String edadAniosString= String.valueOf(edadAnios);
            edadMeses = sharedPreferences.getInt("mascota_edadMeses", 0);
            String edadMesesString= String.valueOf(edadMeses);
            tipoRecuperado = sharedPreferences.getString("mascota_tipo", "");
            razaRecuperada = sharedPreferences.getString("mascota_raza", "");
            mascTxtNombreMascota.setText(nombreMascota);
            mascTxtEdadAniosMascota.setText(edadAniosString);
            mascTxtEdadMesesMascota.setText(edadMesesString);

            // Llenar los Spinners y configurar los valores recuperados
            llenarTipoMascota(tipoRecuperado, razaRecuperada);

            // Mostrar la imagen de la mascota (si está guardada en Base64)
            String imagenBase64 = sharedPreferences.getString("mascota_imagen", "");
            if (!imagenBase64.isEmpty()) {
                byte[] imagenBytes = Base64.decode(imagenBase64, Base64.DEFAULT);
                Bitmap imagenBitmap = BitmapFactory.decodeByteArray(imagenBytes, 0, imagenBytes.length);
                mascIconoMascota.setImageBitmap(imagenBitmap);
//                // Guardamos la URI de la imagen (en caso de que no haya selección nueva)
//                imageUri = Uri.parse(imagenBase64);  // Usamos el Base64 como URI temporal

                // Guardamos la imagen como un Bitmap o URI temporalmente si es necesario
                imageUri = getImageUriFromBitmap(imagenBitmap);  // Convertimos el Bitmap a un URI
            } else {
                // Si no hay imagen, podrías poner una imagen por defecto o dejarlo vacío
                mascIconoMascota.setImageResource(R.drawable.user_mascota); // imagen por defecto
            }
        } else {
            // Si no se encuentra el ID de la mascota, mostrar un mensaje o manejar el error
            Log.e("PerfilMascota", "No se encontró la mascota seleccionada.");
        }
        camposEscritura(false);
        editarPerfilMascota.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(escribe){
                    camposEscritura(true);
                    escribe = false;
                }else{
                    // Si ya está en modo edición, guarda los cambios
                    nombreMascota = mascTxtNombreMascota.getText().toString();
                    edadAnios = Integer.parseInt(mascTxtEdadAniosMascota.getText().toString());
                    edadMeses = Integer.parseInt(mascTxtEdadMesesMascota.getText().toString());
                    tipoRecuperado = mascCboTipoMascota.getSelectedItem().toString();
                    razaRecuperada = mascCboRazaMascota.getSelectedItem().toString();
                    imgPerfil = convertirImagenABlobs(imageUri);

                    new PerfilMascotaFragment.ConexionTask().execute();

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

                                            Toast.makeText(getContext(), "Información de la mascota actualizada", Toast.LENGTH_SHORT).show();
                                            camposEscritura(false); // Deshabilita los campos
                                            escribe=true;
                                            Activity activity = getActivity();
                                            ((Menu) activity).onClickMenu(4);

                                        }else{
                                            Toast.makeText(getContext(), "Error al actualizar la información", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
            }
        });
        return vista;
    }

    private void camposEscritura(boolean esEscritura){
        mascTxtNombreMascota.setEnabled(esEscritura);
        mascTxtEdadAniosMascota.setEnabled(esEscritura);
        mascTxtEdadMesesMascota.setEnabled(esEscritura);
        mascCboTipoMascota.setEnabled(esEscritura);
        mascCboRazaMascota.setEnabled(esEscritura);
        mascIconoMascota.setClickable(esEscritura);


    }

    // Llenar el Spinner de tipo y configurar el de raza
    private void llenarTipoMascota(String tipoSeleccionado, String razaSeleccionada) {
        String[] tipos = {"Tipo de mascota", "Perro", "Gato"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, tipos);
        mascCboTipoMascota.setAdapter(adapterTipo);

        // Encontrar y seleccionar el tipo recuperado
        int tipoPosicion = obtenerPosicionSpinner(mascCboTipoMascota, tipoSeleccionado);
        mascCboTipoMascota.setSelection(tipoPosicion);

        // Listener para actualizar las razas dinámicamente según el tipo seleccionado
        mascCboTipoMascota.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String tipoActual = parentView.getItemAtPosition(position).toString();
                llenarRazaMascota(tipoActual, razaSeleccionada); // Actualizar razas según tipo
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Vaciar el Spinner de razas si no se selecciona un tipo
                llenarRazaMascota("", "");
            }
        });
    }

    // Llenar el Spinner de raza según el tipo seleccionado
    private void llenarRazaMascota(String tipoSeleccionado, String razaSeleccionada) {
        String[] razas;

        // Configurar las opciones de razas según el tipo seleccionado
        switch (tipoSeleccionado) {
            case "Perro":
                razas = new String[]{"Raza", "Labrador", "Bulldog", "Poodle", "Pastor Alemán", "Golden Retriever"};
                break;
            case "Gato":
                razas = new String[]{"Raza", "Siames", "Persa", "Maine Coon", "Bengalí", "Ragdoll"};
                break;
            default:
                razas = new String[]{"Raza"}; // Opción por defecto
                break;
        }

        // Configurar el Spinner de razas
        ArrayAdapter<String> adapterRaza = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, razas);
        mascCboRazaMascota.setAdapter(adapterRaza);

        // Seleccionar la raza recuperada (si existe)
        int razaPosicion = obtenerPosicionSpinner(mascCboRazaMascota, razaSeleccionada);
        mascCboRazaMascota.setSelection(razaPosicion);
    }

    // Método para obtener la posición de un valor en un Spinner
    private int obtenerPosicionSpinner(Spinner spinner, String valor) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(valor)) {
                return i;
            }
        }
        return 0; // Si no se encuentra, devolver la primera posición
    }

    // Método para mostrar el diálogo de selección de imagen
    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Seleccionar foto")
                .setItems(new CharSequence[]{"Seleccionar de Galería", "Tomar Foto con Cámara"}, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            openGallery();
                            break;
                        case 1:
                            openCamera();
                            break;
                    }
                })
                .show();
    }

    // Método para abrir la cámara
    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAPTURE_IMAGE_REQUEST);
    }

    // Método para abrir la galería
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
                // Manejo de la imagen seleccionada de la galería
                imageUri = data.getData();
                mascIconoMascota.setImageURI(imageUri);  // Mostrar la imagen seleccionada
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                // Verificar si la imagen fue capturada correctamente
                if (data != null && data.getExtras() != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    if (photo != null) {
                        // Si tienes una imagen, convierte el Bitmap en URI y actualiza el ImageButton
                        imageUri = getImageUriFromBitmap(photo);  // Convierte el Bitmap en una URI válida
                        mascIconoMascota.setImageURI(imageUri);  // Muestra la imagen en el ImageButton
                    }
                } else {
                    // Si la imagen no se captura correctamente, muestra un mensaje
                    mostrarToast("No se pudo capturar la imagen.");
                }
            }
        } else {
            mostrarToast("No se seleccionó ninguna imagen");
        }
    }

    // Método para convertir el Bitmap en una URI válida
    private Uri getImageUriFromBitmap(Bitmap bitmap) {
        // Guardar la imagen capturada en un archivo temporal
        File file = new File(getContext().getCacheDir(), "captured_image.jpg");
        try (FileOutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] convertirImagenABlobs(Uri uri) {
        try {
            // Abre el archivo de la imagen
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    public class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {

            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            int cnx = 0;
            Mascota mascotaDAO = new Mascota();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
            if(mascotaDAO.editarMascota(mascotaID, nombreMascota, tipoRecuperado, razaRecuperada, imgPerfil,edadAnios,edadMeses)){
                //listaMascotas = mascotaDAO.obtenerMascotasPorCorreo(sharedPreferences.getString("correo", null));
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
        progressDialog.setMessage("Editando informacion de la mascota...");
        progressDialog.setCancelable(false); // No se puede cancelar tocando fuera del diálogo
        progressDialog.show();
    }

    //Dialogo de carga mientras espera al congelamiento -- ocultar
    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

//    private void guardarMascotaSeleccionada(Mascota mascota) {
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // Almacenar cada atributo de la mascota
//        editor.putInt("mascota_id", mascota.getId_Mascota());
//        editor.putString("mascota_nombre", mascota.getNombre());
//        editor.putString("mascota_tipo", mascota.getTipo());
//        editor.putString("mascota_raza", mascota.getRaza());
//        editor.putInt("mascota_edadAños", mascota.getEdadAño());
//        editor.putInt("mascota_edadMeses", mascota.getEdadMeses());
//        String mascotaImagenBase64 = Base64.encodeToString(mascota.getImagen(), Base64.DEFAULT);
//        editor.putString("mascota_imagen", mascotaImagenBase64);
//
//
//        editor.apply();
//    }


}