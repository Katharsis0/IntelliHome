package com.example.registro.ui.auth.registro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registro.R;

public class TerminosCondiciones extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_terminos_condiciones);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener el boton y configurar el listener
        Button button = findViewById(R.id.Continuar);
        button.setOnClickListener(v -> {
            // Redirigir a la actividad Continuar con Registro
            Intent intent2 = new Intent(TerminosCondiciones.this, SignUpActivity.class);
            startActivity(intent2);
        });

    }
}