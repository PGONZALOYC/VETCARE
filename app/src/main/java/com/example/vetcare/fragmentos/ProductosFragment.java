package com.example.vetcare.fragmentos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vetcare.R;
import com.example.vetcare.clases.Menu;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProductosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductosFragment extends Fragment {

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
        View vista = inflater.inflate(R.layout.fragment_productos, container, false);
        TextView titulo = vista.findViewById(R.id.proLblProductosTitulo);

        Context context = getActivity();
        SharedPreferences sharedPreferences = context.getSharedPreferences("Sistema", Context.MODE_PRIVATE);
        String valor = sharedPreferences.getString("categoria", "null");
        titulo.setText(valor);
    /*
        View iconoComida1 = vista.findViewById(R.id.proIconoComidaCanbo);
        View iconoComida2 = vista.findViewById(R.id.proIconoComidaCanbo2);
        View iconoComida3 = vista.findViewById(R.id.proIconoComidaWhiskas);
        View iconoComida4 = vista.findViewById(R.id.proIconoComidaWhiskas2);


        View.OnClickListener listener = new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Activity activity = getActivity();
                ((Menu)activity).onClickMenu(8);
            }
        };
        iconoComida1.setOnClickListener(listener);
        iconoComida2.setOnClickListener(listener);
        iconoComida3.setOnClickListener(listener);
        iconoComida4.setOnClickListener(listener);*/

        // Referencia al GridLayout
        GridLayout contenedorEtiquetas = vista.findViewById(R.id.contenedorEtiquetas);

        // Agregar las CardViews
        agregarCard(contenedorEtiquetas, R.drawable.canbo, getString(R.string.proLblComidaCanbo));
        agregarCard(contenedorEtiquetas, R.drawable.whiskas, getString(R.string.proLblComidaWhiskas));
        agregarCard(contenedorEtiquetas, R.drawable.canbo, getString(R.string.proLblComidaCanbo));
        agregarCard(contenedorEtiquetas, R.drawable.whiskas, getString(R.string.proLblComidaWhiskas));

        return vista;
    }

// Método para crear una CardView y agregarla al GridLayout
private void agregarCard(GridLayout contenedor, int imageResId, String labelText) {
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

    imageView.setImageResource(imageResId);
    imageView.setContentDescription(labelText);

    // Crear TextView
    TextView textView = new TextView(this.getContext());
    textView.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dpToPx(50)
    ));
    textView.setText(labelText);
    textView.setTextSize(15);
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
}


