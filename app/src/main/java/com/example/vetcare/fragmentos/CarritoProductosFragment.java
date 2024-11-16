package com.example.vetcare.fragmentos;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.vetcare.R;
import com.example.vetcare.modelo.Producto;
import com.example.vetcare.sqlite.Vetcare;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import android.widget.NumberPicker;

public class CarritoProductosFragment extends Fragment {
    View vista;

    Producto productoPorIngresar;
    ArrayList<Producto> carritoLista;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CarritoProductosFragment() {
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
    public static CarritoProductosFragment newInstance(String param1, String param2) {
        CarritoProductosFragment fragment = new CarritoProductosFragment();
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
        vista = inflater.inflate(R.layout.fragment_carrito_productos, container, false);

        Context context = getActivity();


        ImageView closeButton = vista.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> closeFragment());


        imprimirProductos();

        return vista;
    }

    private void imprimirProductos() {
        // Referencia al GridLayout
        GridLayout gridLayout = vista.findViewById(R.id.contenedorEtiquetas);

        carritoLista = obtenerListaEnSharedPreferences();

        // Fila 2: Encabezados de columnas
        addColumnHeader(gridLayout, "Imagen", 0);
        addColumnHeader(gridLayout, "Nombre", 1);
        addColumnHeader(gridLayout, "Cantidad", 2);
        addColumnHeader(gridLayout, "Precio", 3);

        // Fila 3: Producto 1
        addProduct(gridLayout, R.drawable.canbo, "Producto 1", 1, "$10.00", 1);
        addProduct(gridLayout, R.drawable.canbo, "Producto 1", 2, "$10.00", 2);
        addProduct(gridLayout, R.drawable.canbo, "Producto 1", 1, "$10.00", 3);
        addProduct(gridLayout, R.drawable.canbo, "Producto 1", 1, "$10.00", 4);

        // Fila 4: Producto 2
        addProduct(gridLayout, R.drawable.whiskas, "Producto 2", 2, "$20.00", 1);
        addProduct(gridLayout, R.drawable.whiskas, "Producto 2", 1, "$20.00", 2);
        addProduct(gridLayout, R.drawable.whiskas, "Producto 2", 2, "$20.00", 3);
        addProduct(gridLayout, R.drawable.whiskas, "Producto 2", 3, "$20.00", 4);

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

    // Método para agregar encabezados de columna
    private void addColumnHeader(GridLayout gridLayout, String text, int column) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setGravity(Gravity.CENTER);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new GridLayout.LayoutParams());
        gridLayout.addView(textView, new GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(column)));
    }

    // Método para agregar un producto
    private void addProduct(GridLayout gridLayout, int imageResId, String name, int quantity, String price, int row) {
        // Imagen del producto
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(imageResId);

        // Ajustar el tamaño de la imagen
        GridLayout.LayoutParams imageParams = new GridLayout.LayoutParams();
        imageParams.width = dpToPx(70);
        imageParams.height = dpToPx(70);
        imageParams.setMargins(10, 10, 10, 10);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageParams.rowSpec = GridLayout.spec(row);    // Fila donde se colocará la imagen
        imageParams.columnSpec = GridLayout.spec(0);   // Columna donde se colocará la imagen (ajustar si es necesario)
        // Añadir la imagen al GridLayout
        imageParams.setGravity(Gravity.CENTER);
        imageView.setLayoutParams(imageParams);
        gridLayout.addView(imageView);

        // Nombre del producto
        TextView nameTextView = new TextView(getContext());
        nameTextView.setText(name);
        nameTextView.setGravity(Gravity.CENTER);
        GridLayout.LayoutParams nameParams = new GridLayout.LayoutParams();
        nameParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameParams.setMargins(10, 30, 10, 10);
        nameTextView.setLayoutParams(nameParams);
        gridLayout.addView(nameTextView, new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(1)));

        // Cantidad del producto (NumberPicker)
        NumberPicker numberPicker = new NumberPicker(getContext());
        numberPicker.setMinValue(1); // Valor mínimo
        numberPicker.setMaxValue(10); // Valor máximo
        numberPicker.setValue(quantity); // Valor predeterminado
        numberPicker.setGravity(Gravity.CENTER);
        numberPicker.setLayoutParams(new GridLayout.LayoutParams());
        gridLayout.addView(numberPicker, new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(2)));

        // Precio del producto
        TextView priceTextView = new TextView(getContext());
        priceTextView.setText(price);
        priceTextView.setGravity(Gravity.CENTER);
        priceTextView.setLayoutParams(new GridLayout.LayoutParams());
        gridLayout.addView(priceTextView, new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(3)));
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }
}
