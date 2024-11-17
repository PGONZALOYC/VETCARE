package com.example.vetcare.actividades;

import static java.security.AccessController.doPrivilegedWithCombiner;
import static java.security.AccessController.getContext;

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
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;
import com.example.vetcare.fragmentos.AgregarMascotaFragment;
import com.example.vetcare.modelo.Mascota;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.util.ArrayList;

public class RegistroPrimeraMascotaActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int CAPTURE_IMAGE_REQUEST = 2;
    private Uri imageUri;
    EditText regTxtRegistroPrimMascotaNombre, regTxtRegistroPrimMascotaAnios, regTxtRegistroPrimMascotaMeses;
    Spinner regCboRegistroPrimMascotaTipo, regCboRegistroPrimMascotaRaza;
    ImageButton mascIconoRegistroPrimMascota,imageButtonSiguiente;
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
    byte[] imgPerfil = new byte[10];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registro_primera_mascota);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Infla el layout del fragmento
        mascIconoRegistroPrimMascota = vista.findViewById(R.id.agremascImgMascota);
        // Configuramos el click listener para el ImageButton
        mascIconoRegistroPrimMascota.setOnClickListener(v -> showImageSelectionDialog());
        regTxtRegistroPrimMascotaNombre = this.<EditText>findViewById(R.id.regTxtRegistroPrimMascotaNombre);
        regTxtRegistroPrimMascotaAnios = findViewById(R.id.regTxtRegistroPrimMascotaAnios);
        regTxtRegistroPrimMascotaMeses = findViewById(R.id.regTxtRegistroPrimMascotaMeses);
        regCboRegistroPrimMascotaTipo = findViewById(R.id.regCboRegistroPrimMascotaTipo);
        regCboRegistroPrimMascotaRaza = findViewById(R.id.regCboRegistroPrimMascotaRaza);
        mascIconoRegistroPrimMascota = findViewById(R.id.mascIconoRegistroPrimMascota);
        imageButtonSiguiente = findViewById(R.id.imageButtonSiguiente);

        imageButtonSiguiente.setOnClickListener(this);

        llenarTiposMascota();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.imageButtonSiguiente) {
            if(validarCampos()){
                new ConexionTask().execute();

                showLoadingDialog();

                // Ejecutar tareas en un hilo separado
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            freezeExecution();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //CODIGO DESPUES DEL CONGELAMIENTO
                                    if (conexionExitosa) {
                                        Toast.makeText(RegistroPrimeraMascotaActivity.this, "Mascota registrada", Toast.LENGTH_SHORT).show();
                                         Intent intent = new Intent(RegistroPrimeraMascotaActivity.this, BienvenidaActivity.class);
                                         startActivity(intent);

                                    }else{
                                        Toast.makeText(RegistroPrimeraMascotaActivity.this, "Error al registrar mascota", Toast.LENGTH_SHORT).show();
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
    }
    private void llenarTiposMascota() {
        String[] tipos = {"Seleccione el tipo de mascota", "Perro", "Gato"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(regTxtRegistroPrimMascotaMeses.getContext(), android.R.layout.simple_spinner_dropdown_item, tipos);
        regCboRegistroPrimMascotaTipo.setAdapter(adapterTipo);

        // Agregar un listener para detectar cambios en el tipo de mascota seleccionado
        regCboRegistroPrimMascotaTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String tipoSeleccionado = parentView.getItemAtPosition(position).toString();
                llenarRazasMascota(tipoSeleccionado); // Actualiza las razas según el tipo seleccionado
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Opcional: Puedes dejar el spinner de razas vacío si no hay selección
                regCboRegistroPrimMascotaRaza.setAdapter(new ArrayAdapter<>(RegistroPrimeraMascotaActivity.this, android.R.layout.simple_spinner_dropdown_item, new String[]{}));
            }
        });
    }

    private void llenarRazasMascota(String tipoSeleccionado) {
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
        ArrayAdapter<String> adapterRaza = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, razas);
        regCboRegistroPrimMascotaRaza.setAdapter(adapterRaza); // Aquí se corrige el Spinner correcto
    }

    private boolean validarCampos() {
        // Validar que los campos no estén vacíos
        if (regTxtRegistroPrimMascotaNombre.getText().toString().isEmpty()) {
            regTxtRegistroPrimMascotaNombre.setError("Este campo es obligatorio");
            return false;
        }
        if (regTxtRegistroPrimMascotaAnios.getText().toString().isEmpty()) {
            regTxtRegistroPrimMascotaAnios.setError("Este campo es obligatorio");
            return false;
        }
        if (regTxtRegistroPrimMascotaMeses.getText().toString().isEmpty()) {
            regTxtRegistroPrimMascotaMeses.setError("Este campo es obligatorio");
            return false;
        }
        if (regCboRegistroPrimMascotaTipo.getSelectedItemPosition() == 0) {
            mostrarToast("Por favor seleccione un tipo de mascota.");
            return false;
        }
        if (regCboRegistroPrimMascotaRaza.getSelectedItemPosition() == 0) {
            mostrarToast("Por favor seleccione una raza.");
            return false;
        }
        return true;
    }

    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    public class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {

            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            int cnx = 0;
            //Usuario usuarioDAO = new Usuario();
            Mascota mascotaDAO = new Mascota();
            SharedPreferences sharedPreferences = getSharedPreferences("Sistema", Context.MODE_PRIVATE);
            nombre = regTxtRegistroPrimMascotaNombre.getText().toString();
            tipo= regCboRegistroPrimMascotaTipo.getSelectedItem().toString();
            raza= regCboRegistroPrimMascotaRaza.getSelectedItem().toString();
            edadAnios = Integer.parseInt(regTxtRegistroPrimMascotaAnios.getText().toString());
            edadMeses = Integer.parseInt(regTxtRegistroPrimMascotaMeses.getText().toString());
            imgPerfil = convertirImagenABlobs(imageUri);
            if(mascotaDAO.agregarMascota(sharedPreferences.getInt("id_usuario", -1), nombre, tipo, raza, fechaNacimiento,imgPerfil,edadAnios,edadMeses)){
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
        SharedPreferences sharedPreferences = this.getSharedPreferences("Sistema", Context.MODE_PRIVATE);
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
        toastActual = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toastActual.show();
    }

    //Dialogo de carga mientras espera al congelamiento -- mostrar
    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this);
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

    // Método para mostrar el diálogo de selección de imagen
    private void showImageSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                // Manejo de la imagen seleccionada de la galería
                imageUri = data.getData();
                mascIconoRegistroPrimMascota.setImageURI(imageUri);  // Mostrar la imagen seleccionada
            } else if (requestCode == CAPTURE_IMAGE_REQUEST) {
                // Verificar si la imagen fue capturada correctamente
                if (data != null && data.getExtras() != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    if (photo != null) {
                        // Si tienes una imagen, convierte el Bitmap en URI y actualiza el ImageButton
                        imageUri = getImageUriFromBitmap(photo);  // Convierte el Bitmap en una URI válida
                        mascIconoRegistroPrimMascota.setImageURI(imageUri);  // Muestra la imagen en el ImageButton
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
        File file = new File(getCacheDir(), "captured_image.jpg");
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
            InputStream inputStream = getContentResolver().openInputStream(uri);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}