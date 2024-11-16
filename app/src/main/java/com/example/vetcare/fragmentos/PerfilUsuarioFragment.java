package com.example.vetcare.fragmentos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.vetcare.R;
import com.example.vetcare.actividades.SesionActivity;
import com.example.vetcare.clases.Hash;
import com.example.vetcare.clases.Menu;
import com.example.vetcare.modelo.Mascota;
import com.example.vetcare.modelo.Producto;
import com.example.vetcare.modelo.Usuario;
import com.example.vetcare.sqlite.Vetcare;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilUsuarioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilUsuarioFragment extends Fragment {

    EditText perTxtNombre,perTxtApellido,perTxtTelefono,perTxtCorreo;
    boolean escribe = true;
    int userID=-1;
    String nombre="";
    String apellido="";
    String telefono="";
    String correo="";
    boolean conexionExitosa = false;
    private static Toast toastActual;
    private ProgressDialog progressDialog;
    View vista;
    ArrayList<Mascota> mascotaLista;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PerfilUsuarioFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PerfilUsuarioFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PerfilUsuarioFragment newInstance(String param1, String param2) {
        PerfilUsuarioFragment fragment = new PerfilUsuarioFragment();
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
        //return inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        // Inflate the layout for this fragment


        View vista = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        perTxtNombre = vista.findViewById(R.id.perTxtNombre);
        perTxtApellido= vista.findViewById(R.id.perTxtApellido);
        perTxtTelefono = vista.findViewById(R.id.perTxtTelefono);
        perTxtCorreo = vista.findViewById(R.id.perTxtCorreo);

        View editarPerfil = vista.findViewById(R.id.perIconoEditar);
        //View infoMastoca = vista.findViewById(R.id.btnInfoMascota);
        //View agreMascota= vista.findViewById(R.id.btnAgregarMascota);

        // Obtener el correo del usuario desde SharedPreferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        userID = sharedPreferences.getInt("id_usuario", -1);
        perTxtNombre.setText(sharedPreferences.getString("nombre", "null"));
        perTxtApellido.setText(sharedPreferences.getString("apellido", "null"));
        perTxtTelefono.setText(sharedPreferences.getString("telefono", "null"));
        perTxtCorreo.setText(sharedPreferences.getString("correo", "null"));
        camposEscritura(false);
        imprimirMascotas();
        new ConexionTask().execute();


        editarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(escribe){
                    camposEscritura(true);
                    escribe = false;
                }else{
                    Usuario usuarioNuevo = new Usuario();
                    // Si ya está en modo edición, guarda los cambios
                    nombre = perTxtNombre.getText().toString();
                    apellido = perTxtApellido.getText().toString();
                    telefono = perTxtTelefono.getText().toString();
                    correo = perTxtCorreo.getText().toString();

                    new ConexionTask().execute();

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

                                            Toast.makeText(getContext(), "Información actualizada", Toast.LENGTH_SHORT).show();
                                            camposEscritura(false); // Deshabilita los campos
                                            escribe=true;

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
//        infoMastoca.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Activity activity = getActivity();
//                ((Menu)activity).onClickMenu(5);
//            }
//        });
//        agreMascota.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Activity activity = getActivity();
//                ((Menu)activity).onClickMenu(7);
//            }
//        });

        LinearLayout closeButton = vista.findViewById(R.id.exit_button);
        closeButton.setOnClickListener(v -> closeFragment());

        return vista;
    }

    private void camposEscritura(boolean esEscritura){
        perTxtNombre.setEnabled(esEscritura);
        perTxtApellido.setEnabled(esEscritura);
        perTxtTelefono.setEnabled(esEscritura);
        perTxtCorreo.setEnabled(esEscritura);

    }

    private void closeFragment() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("correo");
        editor.remove("clave");
        editor.remove("nombre");
        editor.remove("apellido");
        editor.remove("telefono");
        editor.remove("id_usuario");

        editor.putBoolean("recuerda", false);

        editor.apply();

        requireActivity().finish();

        Intent intent = new Intent(requireContext(), SesionActivity.class);
        startActivity(intent);
    }

    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    public class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {

            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            int cnx = 0;
            Usuario usuarioDAO = new Usuario();
            //Mascota mascotaDAO = new Mascota();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
            //mascotaList = mascotaDAO.obtenerMascotasPorCorreo(sharedPreferences.getString("correo", "null"));
            //Cifrar la clave

            if(usuarioDAO.editarUsuario(userID, nombre, apellido, telefono, correo)){
                guardarCorreoEnSharedPreferences(userID, nombre, apellido, telefono, correo);

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
        progressDialog.setMessage("Editando informacion...");
        progressDialog.setCancelable(false); // No se puede cancelar tocando fuera del diálogo
        progressDialog.show();
    }

    //Dialogo de carga mientras espera al congelamiento -- ocultar
    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void guardarCorreoEnSharedPreferences(int id_usuario, String nombre, String apellido, String telefono, String correo) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id_usuario", id_usuario);
        editor.putString("nombre", nombre);
        editor.putString("apellido", apellido);
        editor.putString("telefono", telefono);
        editor.putString("correo", correo);
        editor.apply();
    }

    private void agregarCard(GridLayout contenedor, Bitmap imageBitmap, String labelText, int mascotaId) {
        // Crear LinearLayout principal
        LinearLayout linearLayout = new LinearLayout(this.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new GridLayout.LayoutParams());
        linearLayout.setGravity(Gravity.CENTER); // Centrar los elementos dentro del LinearLayout

        // Crear ImageButton
        ImageButton imageButton = new ImageButton(this.getContext());
        LinearLayout.LayoutParams imageButtonParams = new LinearLayout.LayoutParams(
                dpToPx(100), dpToPx(80));  // Tamaño de la imagen como en el XML
        imageButtonParams.setMargins(dpToPx(10), dpToPx(10), dpToPx(10), dpToPx(10));
        imageButton.setLayoutParams(imageButtonParams);
        imageButton.setImageBitmap(imageBitmap);  // Establecer la imagen
        imageButton.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageButton.setBackgroundResource(R.color.fondoTransparente);  // Fondo transparente

        // Agregar acción al ImageButton
        imageButton.setOnClickListener(v -> {
            // Aquí puedes agregar la lógica para abrir el perfil de la mascota
            // Por ejemplo, usando el ID de la mascota o su nombre
            // startActivity(new Intent(getContext(), PerfilMascotaActivity.class));
        });

        // Crear TextView para el nombre de la mascota
        TextView textView = new TextView(this.getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setText(labelText);  // Nombre de la mascota
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(getResources().getColor(R.color.titulo, null));  // Color del texto
        textView.setTextSize(12);  // Tamaño del texto
        textView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.poppins_medium));

        // Agregar ImageButton y TextView al LinearLayout
        linearLayout.addView(imageButton);
        linearLayout.addView(textView);

        // Crear GridLayout.LayoutParams para colocar el LinearLayout dentro del GridLayout
        GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
        gridParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
        gridParams.height = GridLayout.LayoutParams.WRAP_CONTENT;

        // Agregar el LinearLayout al GridLayout
        contenedor.addView(linearLayout, gridParams);
    }
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
    private void imprimirMascotas() {
        // Crear instancia de la base de datos
        Vetcare vt = new Vetcare(this.getContext());

        // Referencia al GridLayout
        GridLayout contenedorMascotas = vista.findViewById(R.id.contenedorMascotas);
        mascotaLista=obtenerListaMascotaEnSharedPreferences();
        // Iterar por la lista de mascotas
        for (int i = 0; i < mascotaLista.size(); i++) {
            Mascota mascota = mascotaLista.get(i);

            // Validar y obtener el Bitmap de la imagen de la mascota
            Bitmap imagenBitmap;
            try {
                imagenBitmap = BitmapFactory.decodeByteArray(
                        mascota.getImagen(), 0, mascota.getImagen().length
                );
            } catch (Exception e) {
                // Usar una imagen predeterminada en caso de error
                imagenBitmap = BitmapFactory.decodeResource(
                        getResources(), R.drawable.user_mascota
                );
            }

            // Agregar tarjeta al contenedor
            agregarCard(contenedorMascotas, imagenBitmap, mascota.getNombre(), mascota.getId_Mascota());
        }
    }

    public ArrayList<Mascota> obtenerListaMascotaEnSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);

        // Recuperar el JSON de SharedPreferences
        String json = sharedPreferences.getString("listaMascotas", null);

        if (json != null) {
            // Convertir el JSON de nuevo a ArrayList<Mascota>
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            TypeToken<ArrayList<Producto>> typeToken = new TypeToken<ArrayList<Producto>>() {};
            return gson.fromJson(json, typeToken.getType());
        } else {
            return new ArrayList<>();  // Retorna una lista vacía si no hay productos almacenados
        }
    }



}