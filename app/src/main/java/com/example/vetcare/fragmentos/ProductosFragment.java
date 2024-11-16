package com.example.vetcare.fragmentos;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.widget.ImageButton;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vetcare.modelo.Producto;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.example.vetcare.R;
import com.example.vetcare.sqlite.Vetcare;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductosFragment extends Fragment {
    boolean conexionExitosa = false;
    private ProgressDialog progressDialog;
    private Toast toastActual;
    List<Producto> productos;

    View vista;
    String valor;
    Producto productoPorIngresar;
    ArrayList<Producto> carritoLista;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProductosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProdutosComidaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProductosFragment newInstance(String param1, String param2) {
        ProductosFragment fragment = new ProductosFragment();
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
        vista = inflater.inflate(R.layout.fragment_productos, container, false);
        TextView titulo = vista.findViewById(R.id.proLblProductosTitulo);


        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        valor = sharedPreferences.getString("categoria", "null");
        titulo.setText(valor);
        ImageView closeButton = vista.findViewById(R.id.closeButton);
        View iconoCarrito = vista.findViewById(R.id.proImagenCarrito);
        closeButton.setOnClickListener(v -> closeFragment());

        new ProductosFragment.ConexionTask().execute();

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
                            imprimirProductos();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        View.OnClickListener listener2 = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .add(R.id.menRelArea, new CarritoProductosFragment())
                        .commit();
            }
        };

        iconoCarrito.setOnClickListener(listener2);

        return vista;
    }


// Método para crear una CardView y agregarla al GridLayout
    private void agregarCard(GridLayout contenedor, Bitmap imageBitmap, String labelText, String labelPrecio, Producto producto) {
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
            dpToPx(50)
    ));
    textView.setText(labelText);
    textView.setTextSize(12);
    textView.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
    textView.setTextColor(getResources().getColor(R.color.titulo, null));
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        textView.setTypeface(getResources().getFont(R.font.poppins_medium));
    }

    // Crear TextView
        TextView textView2 = new TextView(this.getContext());
        textView2.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(50)
        ));
        textView2.setText(labelPrecio);
        textView2.setTextSize(20);
        textView2.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        textView2.setTextColor(getResources().getColor(R.color.titulo, null));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView2.setTypeface(getResources().getFont(R.font.poppins_medium));
        }

        // Crear el ImageButton
        ImageButton imageButton = new ImageButton(this.getContext());
// Definir parámetros de diseño (puedes ajustar el tamaño como necesites)
        LinearLayout.LayoutParams imageButtonParams = new LinearLayout.LayoutParams(
                dpToPx(60),   // Ancho del botón en dp
                dpToPx(50)    // Alto del botón en dp
        );
        imageButton.setLayoutParams(imageButtonParams);
        imageButtonParams.gravity = Gravity.CENTER;
        imageButton.setImageResource(R.drawable.ic_plus);  // Asegúrate de que la imagen esté en res/drawable
        imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
        imageButton.setId(R.id.agregar_producto_button);


        ColorStateList colorStateList = getResources().getColorStateList(R.color.agregar_producto_button, null);
        imageButton.setBackgroundTintList(colorStateList);


        // Añadir ImageView y TextView al LinearLayout
    linearLayout.addView(imageView);
    linearLayout.addView(textView);
        linearLayout.addView(textView2);

        // Añadir el botón debajo de los TextView
        linearLayout.addView(imageButton);

    // Añadir LinearLayout al CardView
    cardView.addView(linearLayout);

    // Añadir CardView al GridLayout
    contenedor.addView(cardView);

    imageButton.setOnClickListener(v -> clickBotonAgregar(imageButton, producto));

}

    private void clickBotonAgregar(ImageButton button, Producto producto) {
        button.setImageResource(R.drawable.ic_check);

        ColorStateList colorStateList = getResources().getColorStateList(R.color.check_producto_button, null);
        button.setBackgroundTintList(colorStateList);

        button.setClickable(false);

        productoPorIngresar = producto;
        insertarListaEnSharedPreferences();

    }

    private void imprimirProductos() {

        //Se creo la BD
        //objeto de la BD
        Vetcare vt = new Vetcare(this.getContext());

        // Validar credenciales en base de datos o lógica específica
        if (conexionExitosa) {
            // Referencia al GridLayout
            GridLayout contenedorEtiquetas = vista.findViewById(R.id.contenedorEtiquetas);
            for(int i=0; i < productos.size(); i++){
                agregarCard(contenedorEtiquetas, BitmapFactory.decodeByteArray(productos.get(i).getImagen(), 0, productos.get(i).getImagen().length), productos.get(i).getNombre(), "S/."+productos.get(i).getPrecio(), productos.get(i));
            }
        }

    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    // Clase interna para ejecutar la prueba de conexión en un hilo de fondo
    private class ConexionTask extends AsyncTask<Void, Void, Integer> {
        @Override
        protected Integer doInBackground(Void... voids) {
            //Instancia de usuario para usar su función loginUsuario (verificar Usuario.java)
            Producto producto = new Producto();
            int cnx = 0;
            int categoria = 0;
            switch (valor){
                case "Comida":
                    categoria = 1;
                    break;
                case "Higiene":
                    categoria = 2;
                    break;
                case "Juguetes":
                    categoria = 3;
                    break;
                case "Accesorios":
                    categoria = 4;
                    break;
            }
            //Almacenar todas las variables necesarias antes del cnx 1

            productos = producto.obtenerProductos(categoria);
            if(productos != null){
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
        progressDialog.setMessage("Cargando Productos...");
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

    private void closeFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }

    public void insertarListaEnSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Crear un objeto Gson con la configuración de @Expose
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        carritoLista = obtenerListaEnSharedPreferences();
        carritoLista.add(productoPorIngresar);
        String json = gson.toJson(carritoLista);

        // Guardar el JSON en SharedPreferences
        editor.putString("carrito", json);
        editor.apply();

        mostrarToast(carritoLista.get(carritoLista.size()-1).getNombre());
    }

    public ArrayList<Producto> obtenerListaEnSharedPreferences() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);

        // Recuperar el JSON de SharedPreferences
        String json = sharedPreferences.getString("carrito", null);

        if (json != null) {
            // Convertir el JSON de nuevo a ArrayList<Producto>
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            TypeToken<ArrayList<Producto>> typeToken = new TypeToken<ArrayList<Producto>>() {};
            return gson.fromJson(json, typeToken.getType());
        } else {
            return new ArrayList<>();  // Retorna una lista vacía si no hay productos almacenados
        }
    }
}


