package com.example.vetcare.fragmentos;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.vetcare.R;
import com.example.vetcare.modelo.Usuario;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PerfilMascotaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PerfilMascotaFragment extends Fragment {
    ImageView mascIconoMascota;
    EditText mascTxtNombreMascota,mascTxtEdadMascota,mascTxtRazaMascota;
    Spinner mascCboTipoMascota;
    boolean conexionExitosa = false;
    private static Toast toastActual;
    private ProgressDialog progressDialog;
    View vista;
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
        View vista = inflater.inflate(R.layout.fragment_perfil_mascota, container, false);
        mascIconoMascota=vista.findViewById(R.id.mascIconoMascota);
        mascTxtNombreMascota=vista.findViewById(R.id.mascTxtNombreMascota);
        mascTxtEdadMascota=vista.findViewById(R.id.mascTxtEdadMascota);
        mascCboTipoMascota=vista.findViewById(R.id.mascCboTipoMascota);
        mascTxtRazaMascota=vista.findViewById(R.id.mascTxtRazaMascota);


        // Llenar los spinners
        llenarTipoMascota();

        return vista;
    }

    private void llenarTipoMascota() {
        String[] servicios = {"--Seleccione Tipo--", "Perro", "Gato", "Otro"};
        mascCboTipoMascota.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, servicios));
    }

    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    public class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {

            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            int cnx = 0;
            Usuario usuarioDAO = new Usuario();

//            if(usuarioDAO.editarUsuario(userID, nombre, apellido, telefono, correo)){
//                guardarCorreoEnSharedPreferences(userID, nombre, apellido, telefono, correo);
//                cnx = 1;
//            }
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
}