package com.example.fitkit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ContactActivity extends AppCompatActivity {
    EditText contactName, contactMessage;
    Button contactSendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contactName = (EditText) findViewById(R.id.contactName);
        contactMessage = (EditText) findViewById(R.id.contactMessage);
        contactSendBtn = findViewById(R.id.contactSendBtn);
    }

    public void sendEmail(View view) {
        String name = contactName.getText().toString();
        String message = contactMessage.getText().toString();
        String email = "Dear FitKit team,\n\n" + message +
                "\n\nSincerely,\n" + name;

        Log.i("CONTACT", email);
        String[] TO = {"csis401temp@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "FitKit Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, email);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending email...", "");
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ContactActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}