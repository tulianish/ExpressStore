package mobile.computing.expressstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class ContactUsActivity extends AppCompatActivity {

    TextInputEditText name, subject, message;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        name = findViewById(R.id.name);
        subject = findViewById(R.id.subject);
        message = findViewById(R.id.feedbackMessage);

        setTitle(R.string.contact_us);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    public void submitFeedback(View view)
    {
        String nameData = name.getText().toString().trim();
        String subjectData = subject.getText().toString().trim();
        String messageData = message.getText().toString().trim();

        if(TextUtils.isEmpty(nameData))
        {
            name.setError("Please Enter Valid Name!");
            name.requestFocus();
        }
        else if(TextUtils.isEmpty(subjectData))
        {
            subject.setError("Please Enter Valid Subject!");
            subject.requestFocus();
        }
        else if(TextUtils.isEmpty(messageData))
        {
            message.setError("Please Enter Valid Feedback!");
            message.requestFocus();
        }
        else
        {
            Intent sendMail = new Intent(Intent.ACTION_SEND);
            sendMail.setType("plain/text");
            sendMail.putExtra(Intent.EXTRA_EMAIL, new String[]{"expressstoreapp@gmail.com"});
            sendMail.putExtra(Intent.EXTRA_SUBJECT, subjectData);
            sendMail.putExtra(Intent.EXTRA_TEXT, "Name: " + nameData + "\n\nFeedback:\n" + messageData);
            startActivity(Intent.createChooser(sendMail, "Send Feedback"));
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
