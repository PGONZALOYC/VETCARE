package com.example.vetcare.servicios;


import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vetcare.R;

import java.util.HashMap;


public class FloatingButtonService extends Service {

    private WindowManager windowManager;
    private View floatingView;
    private ImageView floatingButton;
    private final HashMap<TextView, Float> originalTextSizes = new HashMap<>();



    @Override
    public void onCreate() {
        super.onCreate();

        // Inflar la vista flotante desde un diseño XML
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_button_layout, null);

        //inicializa el boton flotante
        floatingButton = floatingView.findViewById(R.id.floating_button);


        // Configurar los parámetros para el WindowManager
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        // Posición inicial del botón flotante
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = 0;
        params.y = 100;

        // Inicializar el WindowManager y agregar la vista flotante
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        windowManager.addView(floatingView, params);

        // Configurar el botón flotante para arrastrarlo
        floatingButton = floatingView.findViewById(R.id.floating_button);

        floatingButton.setOnTouchListener(new View.OnTouchListener() {
            private int initialX, initialY;
            private float initialTouchX, initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Registrar las posiciones iniciales
                        initialX = params.x;
                        initialY = params.y;
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        // Actualizar la posición del botón
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);
                        windowManager.updateViewLayout(floatingView, params);
                        return true;

                    case MotionEvent.ACTION_UP:
                        // Detectar si es un clic (sin movimiento significativo)
                        if (Math.abs(event.getRawX() - initialTouchX) < 10 &&
                                Math.abs(event.getRawY() - initialTouchY) < 10) {
                            v.performClick(); // Simula el clic
                            return true;
                        }
                }
                return false; // Permite que otros listeners manejen el evento
            }
        });

        floatingButton.setOnClickListener(v -> {
            // Crear un PopupMenu
            PopupMenu popupMenu = new PopupMenu(FloatingButtonService.this, v);

            // Crear el menú dinámicamente
            popupMenu.getMenu().add(0, 1, 0, "Aumentar Texto");
            popupMenu.getMenu().add(0, 2, 0, "Restaurar Texto");
            popupMenu.getMenu().add(0, 3, 0, "Lectura en Voz Alta");
            popupMenu.getMenu().add(0, 4, 0, "Contraste Alto");

            // Manejar clics en las opciones del menú
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case 1:
                        enviarBroadcastAumentarTexto();
                        return true;
                    case 2:
                        enviarBroadcastRestaurarTexto();
                        return true;
                    case 3:
                        leerEnVozAlta();
                        return true;
                    case 4:
                        activarContraste();
                        return true;
                    default:
                        return false;
                }
            });

            // Mostrar el menú
            popupMenu.show();
        });


    }

    private void enviarBroadcastAumentarTexto() {
        Intent intent = new Intent("ACTUALIZAR_TEXTO");
        intent.putExtra("nuevoTamano", 20f);
        sendBroadcast(intent);
    }

    private void enviarBroadcastRestaurarTexto() {
        Intent intent = new Intent("RESTAURAR_TEXTO");
        sendBroadcast(intent);
    }

    private void leerEnVozAlta() {
        Toast.makeText(this, "Leyendo en voz alta", Toast.LENGTH_SHORT).show();
        // Implementar lógica para leer texto visible en la pantalla
    }

    private void activarContraste() {
        Toast.makeText(this, "Activando contraste alto", Toast.LENGTH_SHORT).show();
        // Implementar lógica para cambiar a un tema de alto contraste
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null; // Este servicio no requiere comunicación directa con el cliente
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("FloatingButtonService", "Servicio iniciado");
        return START_STICKY; // Asegura que el servicio persista hasta que sea eliminado
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d("FloatingButtonService", "onTaskRemoved Called");
        if (floatingView != null) {
            windowManager.removeView(floatingView);
        }
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Asegurarse de eliminar la vista al destruir el servicio
        Log.d("FloatingButtonService", "onDestroy called");
        if (floatingView != null) {
            windowManager.removeView(floatingView);
        }
    }







}
