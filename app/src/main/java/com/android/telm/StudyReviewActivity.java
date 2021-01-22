package com.android.telm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StudyReviewActivity extends AppCompatActivity {

    private Button backReviewStudyButton;
    private TextView patientNameReviewStudyTextView, studyDateReviewStudyTextView,
            doctorNameReviewStudyTextView, observationsReviewStudyTextView;
    String token, doctorWholeName, namePatient, surnamePatient, peselPatient, observations, dateOfStudy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_review);
        backReviewStudyButton = findViewById(R.id.backReviewStudyButton);
        patientNameReviewStudyTextView = findViewById(R.id.patientNameReviewStudyTextView);
        studyDateReviewStudyTextView = findViewById(R.id.studyDateReviewStudyTextView);
        doctorNameReviewStudyTextView = findViewById(R.id.doctorNameReviewStudyTextView);
        observationsReviewStudyTextView = findViewById(R.id.observationsReviewStudyTextView);

        Intent intent = getIntent();
        token = intent.getStringExtra("token"); doctorWholeName = intent.getStringExtra("doctorName");
        observations = intent.getStringExtra("observations");
        dateOfStudy = intent.getStringExtra("date");
        namePatient = intent.getStringExtra("name");
        surnamePatient = intent.getStringExtra("surname");
        peselPatient = intent.getStringExtra("peselPatient");

        observationsReviewStudyTextView.setText(observations);
        doctorNameReviewStudyTextView.setText(doctorWholeName);
        studyDateReviewStudyTextView.setText(dateOfStudy);
        patientNameReviewStudyTextView.setText(namePatient + " " + surnamePatient);

        backReviewStudyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBackToPatientsCardPatientAccount();
            }
        });
    }

    private void goBackToPatientsCardPatientAccount() {
        onBackPressed();
    }

}
