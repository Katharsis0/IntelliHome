package com.example.registro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private Spinner spinnerNacionalidad;
    private Spinner spinnerPasatiempos;
    private EditText editFechaNacimiento;

    private ImageButton fondoCamara;
    private Uri photoUri;
    private File photoFile;

    private final ActivityResultLauncher<String[]> multiplePermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                for (Boolean granted : permissions.values()) {
                    allGranted = allGranted && granted;
                }
                if (allGranted) {
                    showImagePickerDialog();
                }
            }
    );

    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    fondoCamara.setImageURI(photoUri);
                }
            }
    );

    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri selectedImage = result.getData().getData();
                    fondoCamara.setImageURI(selectedImage);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Initialize ImageButton
        fondoCamara = findViewById(R.id.fondoCamara);
        fondoCamara.setOnClickListener(v -> checkPermissionsAndShowPicker());

        //Initialize spinners
        spinnerNacionalidad = findViewById(R.id.spinnerNacionalidad);
        spinnerPasatiempos = findViewById(R.id.spinnerPasatiempos);

        //Initialize birth date field
        editFechaNacimiento = findViewById(R.id.editFechaNacimiento);
        setupDatePicker();

        //Set up nacionalidad spinner
        ArrayAdapter<CharSequence> nacionalidadAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.nacionalidades,
                android.R.layout.simple_spinner_item
        );
        nacionalidadAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerNacionalidad.setAdapter(nacionalidadAdapter);

        //Set up pasatiempos spinner
        ArrayAdapter<CharSequence> pasatiemposAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.pasatiempos,
                android.R.layout.simple_spinner_item
        );
        pasatiemposAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPasatiempos.setAdapter(pasatiemposAdapter);

        spinnerNacionalidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { //Ignore first item (prompt)
                    String selectedNationality = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Handle case where nothing is selected
            }
        });

        spinnerPasatiempos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { //Ignore first item (prompt)
                    String selectedHobby = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupDatePicker() {
        // Prevent keyboard from showing up
        editFechaNacimiento.setInputType(InputType.TYPE_NULL);

        editFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        SignUpActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                // Format the date as dd/mm/yyyy
                                String selectedDate = String.format(Locale.getDefault(),
                                        "%02d/%02d/%d",
                                        dayOfMonth,
                                        monthOfYear + 1,
                                        year);
                                editFechaNacimiento.setText(selectedDate);
                            }
                        },
                        year,
                        month,
                        day
                );

                // Set max date to current date
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

                // Set minimum date (optional, e.g., 100 years ago)
                Calendar minDate = Calendar.getInstance();
                minDate.add(Calendar.YEAR, -100);
                datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());

                datePickerDialog.show();
            }
        });
    }

    private void checkPermissionsAndShowPicker() {
        String[] permissions = {
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        multiplePermissionLauncher.launch(permissions);
    }

    private void showImagePickerDialog() {
        String[] options = {"Tomar foto", "Elegir de galería", "Cancelar"};

        new AlertDialog.Builder(this)
                .setTitle("Seleccionar foto de perfil")
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            openCamera();
                            break;
                        case 1:
                            openGallery();
                            break;
                        case 2:
                            dialog.dismiss();
                            break;
                    }
                })
                .show();
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    photoUri = FileProvider.getUriForFile(this,
                            "com.example.registro.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    cameraLauncher.launch(takePictureIntent);
                }
            } catch (IOException ex) {
                // Handle error
            }
        }
    }

    private void openGallery() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(pickPhoto);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss",
                java.util.Locale.getDefault()).format(new java.util.Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }
}
