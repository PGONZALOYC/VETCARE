package com.example.vetcare.fragmentos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    double precioTotal = 0;

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

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista = inflater.inflate(R.layout.fragment_carrito_productos, container, false);

        Context context = getActivity();


        ImageView closeButton = vista.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> closeFragment());


        imprimirProductos();

        TextView txtTotal = vista.findViewById(R.id.carTxtTotal);
        txtTotal.setText("S/"+String.format("%.2f", precioTotal));

        return vista;
    }

    private void imprimirProductos() {
        precioTotal = 0;

        // Referencia al GridLayout
        GridLayout gridLayout = vista.findViewById(R.id.contenedorEtiquetas);

        // Fila 1: Encabezados de columnas
        addColumnHeader(gridLayout, "", 0);
        addColumnHeader(gridLayout, "Imagen", 1);
        addColumnHeader(gridLayout, "Nombre", 2);
        addColumnHeader(gridLayout, "Cantidad", 3);
        addColumnHeader(gridLayout, "Precio", 4);


        carritoLista = obtenerListaEnSharedPreferences();

        for(int i=0; i<carritoLista.size(); i++){
            addProduct(gridLayout, BitmapFactory.decodeByteArray(carritoLista.get(i).getImagen(), 0, carritoLista.get(i).getImagen().length), carritoLista.get(i).getNombre(), 1, carritoLista.get(i).getPrecio(), i+1, R.drawable.ic_delete, carritoLista.get(i));
        }

        if(carritoLista.isEmpty()){
            TextView textView = new TextView(getContext());
            textView.setText("No tienes productos agregados aún ):");
            textView.setTypeface(null, Typeface.BOLD);

            GridLayout.LayoutParams textParams = new GridLayout.LayoutParams();
            textParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            textParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            textParams.setMargins(30, 30, 10, 10);
            textParams.rowSpec = GridLayout.spec(1);
            textParams.columnSpec = GridLayout.spec(0, 4);
            textParams.setGravity(Gravity.CENTER);
            textView.setLayoutParams(textParams);

            gridLayout.addView(textView);
        }

    }

    private void closeFragment() {
        requireActivity().getSupportFragmentManager().beginTransaction()
                .remove(this)
                .commit();
    }

    public void eliminarListaEnSharedPreferences(Producto producto) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Crear un objeto Gson con la configuración de @Expose
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        carritoLista = obtenerListaEnSharedPreferences();
        //carritoLista.remove(producto);
        for(int i=0; i<carritoLista.size(); i++){
            if(carritoLista.get(i).getId_Producto() == producto.getId_Producto()){
                carritoLista.remove(i);
                break;
            }
        }
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
        textView.setTypeface(null, Typeface.BOLD);

        GridLayout.LayoutParams textParams = new GridLayout.LayoutParams();
        textParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        textParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        textParams.setMargins(0, 10, 50, 10);
        textParams.rowSpec = GridLayout.spec(0);
        textParams.columnSpec = GridLayout.spec(column);
        textParams.setGravity(Gravity.CENTER);
        textView.setLayoutParams(textParams);

        gridLayout.addView(textView);
    }

    // Método para agregar un producto
    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void addProduct(GridLayout gridLayout, Bitmap imageBitmap, String name, int quantity, double price, int row, int deleteResId, Producto producto) {
        // Imagen del producto
        ImageView imageView = new ImageView(getContext());
        imageView.setImageBitmap(imageBitmap);

        // Ajustar el tamaño de la imagen
        GridLayout.LayoutParams imageParams = new GridLayout.LayoutParams();
        imageParams.width = dpToPx(70);
        imageParams.height = dpToPx(70);
        imageParams.setMargins(10, 10, 10, 10);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageParams.rowSpec = GridLayout.spec(row);
        imageParams.columnSpec = GridLayout.spec(1);
        // Añadir la imagen al GridLayout
        imageParams.setGravity(Gravity.CENTER);
        imageView.setLayoutParams(imageParams);
        gridLayout.addView(imageView);

        // Nombre del producto
        TextView nameTextView = new TextView(getContext());
        nameTextView.setText(name);
        GridLayout.LayoutParams nameParams = new GridLayout.LayoutParams();
        nameParams.width = dpToPx(60);
        nameParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        nameParams.setMargins(10, 10, 10, 10);
        nameParams.rowSpec = GridLayout.spec(row);
        nameParams.columnSpec = GridLayout.spec(2);
        nameParams.setGravity(Gravity.CENTER);
        nameTextView.setLayoutParams(nameParams);
        gridLayout.addView(nameTextView);

        // Precio del producto
        TextView priceTextView = new TextView(getContext());
        priceTextView.setText("S/"+(String.format("%.2f", price)));
        GridLayout.LayoutParams priceParams = new GridLayout.LayoutParams();
        priceParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
        priceParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        priceParams.setMargins(10, 30, 10, 10);
        priceParams.rowSpec = GridLayout.spec(row);
        priceParams.columnSpec = GridLayout.spec(4);
        priceParams.setGravity(Gravity.CENTER);
        priceTextView.setLayoutParams(priceParams);

        // Cantidad del producto (NumberPicker)
        NumberPicker numberPicker = new NumberPicker(getContext());
        numberPicker.setMinValue(1); // Valor mínimo
        numberPicker.setMaxValue(100); // Valor máximo
        numberPicker.setValue(quantity); // Valor predeterminado
        GridLayout.LayoutParams numberParams = new GridLayout.LayoutParams();
        numberParams.width = dpToPx(30);
        numberParams.height = dpToPx(70);
        numberParams.setMargins(30, 10, 10, 10);
        numberParams.rowSpec = GridLayout.spec(row);
        numberParams.columnSpec = GridLayout.spec(3);
        numberParams.setGravity(Gravity.CENTER);
        numberPicker.setLayoutParams(numberParams);
        numberPicker.setWrapSelectorWheel(false);

        // Configurar el OnValueChangedListener
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                priceTextView.setText("S/"+(String.format("%.2f", price*newVal)));

                precioTotal -= price*oldVal;
                precioTotal += price*newVal;

                TextView txtTotal = vista.findViewById(R.id.carTxtTotal);
                txtTotal.setText("S/"+String.format("%.2f", precioTotal));
            }

        });

        precioTotal += price;
        TextView txtTotal = vista.findViewById(R.id.carTxtTotal);
        txtTotal.setText(Double.toString(precioTotal));
        // Agregar al GridLayout
        gridLayout.addView(numberPicker);

        gridLayout.addView(priceTextView);

        //Boton eliminar
        ImageView deleteView = new ImageView(getContext());
        deleteView.setImageResource(deleteResId);

        // Ajustar el tamaño de la imagen
        GridLayout.LayoutParams deleteParams = new GridLayout.LayoutParams();
        deleteParams.width = dpToPx(30);
        deleteParams.height = dpToPx(30);
        deleteParams.setMargins(0, 10, 0, 10);
        deleteView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        deleteParams.rowSpec = GridLayout.spec(row);
        deleteParams.columnSpec = GridLayout.spec(0);
        // Añadir la imagen al GridLayout
        deleteParams.setGravity(Gravity.CENTER);
        deleteView.setLayoutParams(deleteParams);

        // Configurar el OnValueChangedListener
        deleteView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                eliminarListaEnSharedPreferences(producto);
                gridLayout.removeAllViews();
                imprimirProductos();
            }

        });

        gridLayout.addView(deleteView);


    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }


}
