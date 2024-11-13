package com.example.vetcare.fragmentos;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;
import com.example.vetcare.modelo.Mascota;
import com.example.vetcare.modelo.Producto;
import com.example.vetcare.modelo.Usuario;
import com.example.vetcare.sqlite.Vetcare;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilUsuarioFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilUsuarioFragment extends Fragment {

    EditText perTxtNombre,perTxtApellido,perTxtTelefono,perTxtCorreo;
    Button perBtnEditar;
    boolean conexionExitosa = false;
    private ProgressDialog progressDialog;
    private Toast toastActual;
    boolean campoLectura= true;
    View vista;
    Usuario usuarioPerfil;
    List<Mascota> mascotasPerfil;
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
        View vista = inflater.inflate(R.layout.fragment_perfil_usuario, container, false);

        perTxtNombre = vista.findViewById(R.id.perTxtNombre);
        perTxtApellido= vista.findViewById(R.id.perTxtApellido);
        perTxtTelefono = vista.findViewById(R.id.perTxtTelefono);
        perTxtCorreo = vista.findViewById(R.id.perTxtCorreo);

        new PerfilUsuarioFragment.ConexionTask().execute();
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
                            mostrarInformacionUsuario();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        View editarPerfil = vista.findViewById(R.id.perBtnEditar);
//        View infoMastoca = vista.findViewById(R.id.btnInfoMascota);
//        View agreMascota= vista.findViewById(R.id.btnAgregarMascota);

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

        return vista;
    }

    private void agregarCard(GridLayout contenedor, Bitmap imageBitmap, String labelText) {
        // Crear CardView
        CardView cardView = new CardView(this.getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = GridLayout.LayoutParams.MATCH_PARENT;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        cardView.setLayoutParams(params);
        cardView.setRadius(10);
        cardView.setCardElevation(5);
        cardView.setUseCompatPadding(true);

        // Crear LinearLayout
        LinearLayout linearLayout = new LinearLayout(this.getContext());
        LinearLayout.LayoutParams linearparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(linearparams);

        // Crear ImageView
        ImageView imageView = new ImageView(this.getContext());
        LinearLayout.LayoutParams imaparams = new LinearLayout.LayoutParams(dpToPx(150), dpToPx(160));
        // Luego, establece un margen de 10dp
        int marginInPx = dpToPx(10);
        imaparams.setMargins(marginInPx, marginInPx, marginInPx, marginInPx);

        // Finalmente, aplica los parámetros a la ImageView
        imageView.setLayoutParams(imaparams);

        imageView.setImageBitmap(imageBitmap);
        imageView.setContentDescription(labelText);

        // Crear TextView
        TextView textView = new TextView(this.getContext());
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(10)
        ));
        textView.setText(labelText);
        textView.setTextSize(10);
        textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        textView.setTextColor(getResources().getColor(R.color.titulo, null));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setTypeface(getResources().getFont(R.font.poppins_medium));
        }

        // Añadir ImageView y TextView al LinearLayout
        linearLayout.addView(imageView);
        linearLayout.addView(textView);

        // Añadir LinearLayout al CardView
        cardView.addView(linearLayout);

        // Añadir CardView al GridLayout
        contenedor.addView(cardView);

    }
    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void deshabilitarCampos(){
        perTxtNombre.setFocusable(false);
        perTxtNombre.setClickable(false);
        perTxtApellido.setFocusable(false);
        perTxtApellido.setClickable(false);
        perTxtTelefono.setFocusable(false);
        perTxtTelefono.setClickable(false);
        perTxtCorreo.setFocusable(false);
        perTxtCorreo.setClickable(false);
    }
    private void mostrarInformacionUsuario(){
        Vetcare vt = new Vetcare(this.getContext());

        if(conexionExitosa){

            perTxtNombre.setText(usuarioPerfil.getNombres());
            perTxtApellido.setText(usuarioPerfil.getApellidos());
            perTxtTelefono.setText(usuarioPerfil.getTelefono());
            perTxtCorreo.setText(usuarioPerfil.getCorreo());

            GridLayout contenedorMisMascotas = vista.findViewById(R.id.contenedorMisMascotas);
            for(int i=0; i < mascotasPerfil.size(); i++){
                agregarCard(contenedorMisMascotas, BitmapFactory.decodeByteArray(mascotasPerfil.get(i).getImagen(), 0, mascotasPerfil.get(i).getImagen().length), mascotasPerfil.get(i).getNombre());
            }
            if(campoLectura){
                deshabilitarCampos();
            }else {
                //Aca viene para editar la informacion del usuario

            }

        }
    }


    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    private class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            Usuario usuario = new Usuario();
            Mascota mascota = new Mascota();
            int cnx = 0;
            // Obtener el correo del usuario desde SharedPreferences
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("CorreoGuardado", Context.MODE_PRIVATE);
            String correo = sharedPreferences.getString("correo_usuario", null); // Si no existe, será null
            //Almacenar todas las variables necesarias antes del cnx 1

            usuarioPerfil = usuario.obtenerInformacionUsuario(correo);
            mascotasPerfil = mascota.obtenerMascotasPorCorreo(correo);
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
            } else{
                hideLoadingDialog();
                mostrarToast("Error: Conexion fallida");
            }
        }
    }

    //Congela los procesos mientras espera que conexionExitosa sea true para continuar con las posteriores instrucciones
    private void freezeExecution() throws InterruptedException {
        while (!conexionExitosa) {
            Thread.sleep(100); // Esperar un breve periodo antes de volver a comprobar
        }
    }

    //Dialogo de carga mientras espera al congelamiento -- mostrar
    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setMessage("Cargando Perfil...");
        progressDialog.setCancelable(false); // No se puede cancelar tocando fuera del diálogo
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