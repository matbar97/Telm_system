package com.android.telm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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


public class MainActivity extends AppCompatActivity {
    public final static String ip = "192.168.1.11";
    private Button loginButton, registerButton;
    private EditText editTextLogin, editTextPasswordLogin;
    public static final int[] WEIGHTS = new int[]{1, 3, 7, 9, 1, 3, 7, 9, 1, 3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPasswordLogin = findViewById(R.id.editTextPassword);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAccountCreation();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    sendPostData();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }


    private void goToAccountCreation() {
        Intent intent = new Intent(this, CreatePatientAccountActivity.class);
        startActivity(intent);
    }

    public void sendPostData() throws JSONException {
        String URL = "http://"+ip+":8080/auth/signin";
        final JSONObject jsonBody = new JSONObject();
        final String username = editTextLogin.getText().toString();
        final String pwd = editTextPasswordLogin.getText().toString();

        jsonBody.put("username", username);
        jsonBody.put("password", pwd);

        final String requestBody = jsonBody.toString();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("VOLLEY", response.toString());
                        try {
                            String token = response.getString("token");
                            if(username.contains("admin")) {
                                Intent intent = new Intent(getApplicationContext(), AdminMainMenuActivity.class);
                                intent.putExtra("token", token);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Witaj " + username, Toast.LENGTH_SHORT).show();
                            }
                            else if (username.contains("doctor")){
                                Intent intent = new Intent(getApplicationContext(), DoctorsMainMenuActivity.class);
                                intent.putExtra("token", token);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Witaj " + username, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Intent intent = new Intent(getApplicationContext(), PatientsMenuActivity.class);
                                intent.putExtra("token", token);
                                startActivity(intent);
                                Toast.makeText(getApplicationContext(), "Witaj " + username, Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d("Problem", "Error: " + error.getMessage());
                        Toast.makeText(getApplicationContext()
                                ,"Nie ma Cię w systemie. Zarejestruj się lub wpisz właściwe dane"
                                ,Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", pwd);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
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


    public final static boolean isChecksumValid(String pesel) {
        if (pesel == null) return false;

        if (pesel.length() != 11) return false;

        int checksum = 0;
        for (int i = 0; i < 10; i++)
            checksum += Character.getNumericValue(pesel.charAt(i)) * WEIGHTS[i];

        checksum %= 10;
        checksum = 10 - checksum;
        checksum %= 10;

        int expectedChecksum = Character.getNumericValue(pesel.charAt(10));

        return checksum == expectedChecksum;

    }

    public static String capitalize(String str) {
        if(str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String replace(String str, int index, char replace){
        if(str==null){
            return str;
        }else if(index<0 || index>=str.length()){
            return str;
        }
        char[] chars = str.toCharArray();
        chars[index] = replace;
        return String.valueOf(chars);
    }
}

