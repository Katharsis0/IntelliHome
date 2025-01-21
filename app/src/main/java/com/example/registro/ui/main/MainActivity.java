package com.example.registro.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.registro.R;
import com.example.registro.ui.auth.login.OlvidoContrasena;
import com.example.registro.ui.auth.login.SecondActivity;
import com.example.registro.ui.auth.registro.SignUpActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ImageButton googleBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajuste para las barras del sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Obtener el TextView y configurar el listener
        TextView textView = findViewById(R.id.textView2);
        textView.setOnClickListener(v -> {
            // Redirigir a la actividad OlvidoContrasena
            Intent intent = new Intent(MainActivity.this, OlvidoContrasena.class);
            startActivity(intent);
        });

        // Botón de Registrarse
        Button buttonRegistrarse = findViewById(R.id.Button1);
        buttonRegistrarse.setOnClickListener(v -> {
            // Redirigir a la actividad de registro
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });


        //-------------------
        //LOG IN CON GOOGLE
        //---------------------

        googleBtn = findViewById(R.id.googleBtn);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();   /* Llama a método */
            }
        });

    }

    void signIn(){
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent,1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateUpToSecondActivity();
            } catch(ApiException e) {
                Toast.makeText(this, "Algo salió mal", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void navigateUpToSecondActivity(){
        finish();
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivity(intent);
    }
}
