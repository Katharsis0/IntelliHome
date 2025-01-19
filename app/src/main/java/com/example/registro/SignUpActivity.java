package com.example.registro;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    private Spinner spinnerNacionalidad;
    private Spinner spinnerPasatiempos;
    private EditText editFechaNacimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

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
}