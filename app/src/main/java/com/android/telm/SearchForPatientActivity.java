package com.android.telm;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchForPatientActivity extends AppCompatActivity {

    private Button arrowBackSearchForPatientButton;
    private EditText searchForPatientEditText;
    RecyclerView recyclerView;
    RecyclerView.Adapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<Patient> patientList;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_patient);
        arrowBackSearchForPatientButton = findViewById(R.id.arrowBackSearchForPatientButton);
        searchForPatientEditText = findViewById(R.id.searchForPatientEditText);

        recyclerView = findViewById(R.id.patientsRecyclerView);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(SearchForPatientActivity.this);

        recyclerView.setLayoutManager(layoutManager);


        getLoadPatients();

    }

    private void getLoadPatients() {

        String URL = "http://192.168.99.1:8080/api/doctor/listpatients";
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                patientList = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    Patient patient = new Patient();
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        String name, surname, pesel;
                        name = jsonObject.getString("name");
                        surname = jsonObject.getString("surname");
                        pesel = jsonObject.getString("pesel");
                        patient.setName(name);
                        patient.setSurname(surname);
                        patient.setPesel(pesel);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    patientList.add(patient);
                }

                mAdapter = new PatientRecyclerAdapter(SearchForPatientActivity.this, patientList);
                recyclerView.setAdapter(mAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Volley Error: ", String.valueOf(error));
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                Intent intent = getIntent();
                token = intent.getStringExtra("token");
                headers.put("Authorization", "Bearer " + token);
                headers.put("Content-Type", "application/json");
                return headers;
            }

        };
        queue.add(jsonArrayRequest);
    }
}
