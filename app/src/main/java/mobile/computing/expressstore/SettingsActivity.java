package mobile.computing.expressstore;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    TextView emailData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set title of Settings activity
        setTitle(R.string.settings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Initialization of TextView with email data
        emailData = findViewById(R.id.emailData);

    }

    public void copyEmail(View view)
    {
        /*
         * Displays Toast with "Email is copied to clipboard!" message
         * Saves email in device's clipboard with label "email"
         */

        String email = emailData.getText().toString();
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("email", email);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(this,"Email is copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    public void openOtherSettings(View view)
    {
        /*
         * Activate when any option in setting is selected/clicked
         * Uses switch to match ID
         * Open new activity using intent when ID is matched
         */
        Intent intent;
        switch (view.getId())
        {
            // Opens Edit Profile Setting
            case R.id.editSection:
                intent = new Intent(SettingsActivity.this, EditProfileActivity.class);
                startActivity(intent);
                break;
            // Opens FAQ Setting
            /*case R.id.faqSection:
                intent = new Intent();
                startActivity(intent);
                break;*/
            // Opens Contact US Setting
            case R.id.contactSection:
                intent = new Intent(SettingsActivity.this, ContactUsActivity.class);
                startActivity(intent);
                break;
            // Opens Privacy Policy Setting
            /*case R.id.policySection:
                intent = new Intent(SettingsActivity.this, null);
                startActivity(intent);
                break;*/
            // Opens App Info Setting
            case R.id.infoSection:
                intent = new Intent(SettingsActivity.this, InfoActivity.class);
                startActivity(intent);
                break;
        }
    }

    // create an action bar button
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem item = menu.findItem(R.id.btn_home);
        item.setVisible(true);
        MenuItem item1 = menu.findItem(R.id.btn_settings);
        item1.setVisible(false);
        MenuItem item2 = menu.findItem(R.id.btn_cart);
        item1.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.btn_home:
                Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                break;
            case android.R.id.home:
                Toast.makeText(this, "back", Toast.LENGTH_SHORT).show();
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
