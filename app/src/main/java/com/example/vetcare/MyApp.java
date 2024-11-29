package com.example.vetcare;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;

import java.util.HashMap;

public class MyApp extends Application {

    private Activity currentActivity; // Para rastrear la actividad visible
    private final HashMap<View, Float> originalTextSizes = new HashMap<>(); // Almacena los tamaños originales


    private final BroadcastReceiver globalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null && currentActivity != null) {
                ViewGroup rootView = currentActivity.findViewById(android.R.id.content); // Raíz de la actividad
                if (rootView != null) {
                    if ("ACTUALIZAR_TEXTO".equals(action)) {
                        float nuevoTamano = intent.getFloatExtra("nuevoTamano", 16f);
                        aplicarTamanoTexto(rootView, nuevoTamano);
                    } else if ("RESTAURAR_TEXTO".equals(action)) {
                        restaurarTamanoOriginal(rootView);
                    }
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        // Registrar el BroadcastReceiver global
        IntentFilter filter = new IntentFilter();
        filter.addAction("ACTUALIZAR_TEXTO");
        filter.addAction("RESTAURAR_TEXTO");
        registerReceiver(globalReceiver, filter);

        // Registrar el rastreador de actividad
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                currentActivity = activity; // Guardar la actividad visible
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (currentActivity == activity) {
                    currentActivity = null; // Limpiar si la actividad deja de estar visible
                }
            }

            @Override public void onActivityCreated(Activity activity, android.os.Bundle savedInstanceState) {}
            @Override public void onActivityStarted(Activity activity) {}
            @Override public void onActivityStopped(Activity activity) {}
            @Override public void onActivitySaveInstanceState(Activity activity, android.os.Bundle outState) {}
            @Override public void onActivityDestroyed(Activity activity) {}
        });
    }

    private void aplicarTamanoTexto(ViewGroup root, float tamanoTexto) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);

            if (child instanceof TextView || child instanceof Button) {
                TextView textView = (TextView) child;

                // Almacena el tamaño original una sola vez
                if (!originalTextSizes.containsKey(child)) {
                    originalTextSizes.put(child, textView.getTextSize() / getResources().getDisplayMetrics().scaledDensity);
                }

                // Recuperar el tamaño original almacenado
                float originalSize = originalTextSizes.get(child);

                // Solo aplica el tamaño si es mayor o igual al tamaño original
                if (tamanoTexto >= originalSize) {
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, tamanoTexto);
                } else {
                    // Mantén el tamaño original si el nuevo tamaño es más pequeño
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, originalSize);
                }
            } else if (child instanceof ViewGroup) {
                aplicarTamanoTexto((ViewGroup) child, tamanoTexto); // Recurre a los hijos
            }
        }
    }

    private void restaurarTamanoOriginal(ViewGroup root) {
        for (int i = 0; i < root.getChildCount(); i++) {
            View child = root.getChildAt(i);

            if (child instanceof TextView || child instanceof Button) {
                TextView textView = (TextView) child;

                // Verificar si el tamaño original está almacenado
                if (originalTextSizes.containsKey(child)) {
                    float originalSizeSp = originalTextSizes.get(child);
                    // Restaurar el tamaño original en SP
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, originalSizeSp);
                }
            } else if (child instanceof ViewGroup) {
                restaurarTamanoOriginal((ViewGroup) child); // Recurre a los hijos
            }
        }
    }





}
