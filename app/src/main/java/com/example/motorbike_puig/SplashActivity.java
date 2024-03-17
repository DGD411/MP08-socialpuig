package com.example.motorbike_puig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getSupportActionBar().hide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Obtener la referencia a la ImageView en tu layout
        ImageView imageView = findViewById(R.id.imageView);

        // Cargar la animación de pulso desde el recurso XML
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse_animation);

        // Asignar la animación a la ImageView
        imageView.setImageResource(R.mipmap.ic_launcher_foreground);
        imageView.startAnimation(pulseAnimation);

        // Agregar un retraso antes de cambiar a MainActivity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Terminar la actividad actual para evitar volver a ella desde el MainActivity
            }
        }, 4000); // Tiempo de espera en milisegundos (4 segundos en este caso)
    }
}
