package com.android.telm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.android.telm.MainActivity.ip;

public class AddStudyActivity extends AppCompatActivity {



    private Button backAddStudyButton, addAnnotationAddStudyButton, applyStudyAddStudyButton;
    private EditText observationsMultiLineTextView;
    private TextView patientNameAddStudyTextView;
    String name, surname, pesel, token, observations, doctorsName, dateOfStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_study);
        backAddStudyButton = findViewById(R.id.backAddStudyButton);
        addAnnotationAddStudyButton = findViewById(R.id.addAnnotationAddStudyButton);
        applyStudyAddStudyButton = findViewById(R.id.applyStudyAddStudyButton);
        observationsMultiLineTextView = findViewById(R.id.observationsAddStudyEditText);
        patientNameAddStudyTextView = findViewById(R.id.patientNameAddStudyTextView);

        Intent intent = getIntent();
        name = intent.getStringExtra("name"); surname = intent.getStringExtra("surname");
        pesel = intent.getStringExtra("pesel"); token = intent.getStringExtra("token");
        doctorsName = intent.getStringExtra("doctorsName"); dateOfStudy=intent.getStringExtra("dateOfStudy");
        patientNameAddStudyTextView.setText(name + " " + surname);
        System.out.println(doctorsName);
        System.out.println(dateOfStudy);

        applyStudyAddStudyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToListOfStudiesForPatient();
            }
        });

        backAddStudyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToCurrentPatientData();
            }
        });
    }



    private void goBackToCurrentPatientData() {
        onBackPressed();
    }

    private void goToListOfStudiesForPatient() {
        observations = observationsMultiLineTextView.getText().toString();

        String URL = "http://"+ip+":8080/api/doctor/study/add";
        final JSONObject jsonBody = new JSONObject();
        try {
            if(!observations.isEmpty()) {
                jsonBody.put("patientPesel", pesel);
                jsonBody.put("observations", observations);
            } else {
                Toast.makeText(this, "Uzupełnij obserwacje", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBody.toString();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("VOLLEY", response.toString());
                        Intent intent = new Intent(getApplicationContext(),
                                PatientDataFromDocPointOfViewActivity.class);
                        intent.putExtra("observations", observations);
                        intent.putExtra("pesel", pesel);
                        intent.putExtra("name", name);
                        intent.putExtra("surname", surname);
                        intent.putExtra("token", token);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), "Dodano badanie pacjentowi "
                                + name + " " + surname, Toast.LENGTH_SHORT).show();
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Problem", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext()
                                ,"Błąd w zakładaniu kartoteki"
                                ,Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;

            }

            @Override
            public byte[] getBody() {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.start();
        requestQueue.add(request);
    }
}

