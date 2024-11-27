package com.example.vetcare.servicios;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.vetcare.R;

public class FloatingButtonService extends Service {

    private WindowManager windowManager;
    private View floatingView;

    @Override
    public void onCreate() {
        super.onCreate();

        // Inflar la vista flotante desde un diseño XML
        floatingView = LayoutInflater.from(this).inflate(R.layout.floating_button_layout, null);

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
        ImageView floatingButton = floatingView.findViewById(R.id.floating_button);
        floatingButton.setOnTouchListener(new View.OnTouchListener() {
            private int lastX, lastY;
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
                }
                return false;
            }
        });

        // Configurar el evento click del botón
        floatingButton.setOnClickListener(v -> {
            // Acción al hacer clic en el botón (ejemplo: mostrar un mensaje)
            Toast.makeText(FloatingButtonService.this, "Accesibilidad activada", Toast.LENGTH_SHORT).show();
        });
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
