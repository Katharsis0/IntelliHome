package com.example.prueba2;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private EditText inputField;
    private Button sendButton;
    private String serverAddress = "10.0.2.2"; // Cambia a la IP de tu servidor
    private int serverPort = 1717;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inputField = findViewById(R.id.inputField);
        sendButton = findViewById(R.id.sendButton);

        // Permitir operaciones de red en el hilo principal
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitNetwork().build());

        sendButton.setOnClickListener(v -> {
            String userMessage = inputField.getText().toString();
            if (!userMessage.isEmpty()) {
                new Thread(() -> sendMessageToServer(userMessage)).start();
            } else {
                Toast.makeText(this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendMessageToServer(String userMessage) {
        try (Socket clientSocket = new Socket(serverAddress, serverPort)) {
            // Crear flujos de entrada y salida
            OutputStream output = clientSocket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true); // Para enviar mensajes al servidor

            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // Formatear el mensaje con el prefijo requerido
            String formattedMessage = "1." + userMessage;

            // Enviar mensaje al servidor
            writer.println(formattedMessage);

            // Leer respuesta del servidor
            String serverResponse = reader.readLine();

            // Mostrar respuesta en la interfaz de usuario
            runOnUiThread(() -> Toast.makeText(this, "Respuesta del servidor: " + serverResponse, Toast.LENGTH_LONG).show());
        } catch (Exception e) {
            e.printStackTrace();
            runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
