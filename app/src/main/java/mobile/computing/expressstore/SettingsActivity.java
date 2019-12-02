package mobile.computing.expressstore;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView emailData, nameData;
    final static String userPref = "userdata";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set title of Settings activity
        setTitle(R.string.settings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(userPref, Context.MODE_PRIVATE);

        // Initialization of TextView
        nameData = findViewById(R.id.nameData);
        emailData = findViewById(R.id.emailData);

        nameData.setText(sharedPreferences.getString("name", null));
        emailData.setText(sharedPreferences.getString("email", null));

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
        Toasty.info(this,"Email is copied to clipboard!", Toast.LENGTH_SHORT, true).show();
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
            // Opens Contact US Setting
            case R.id.contactSection:
                intent = new Intent(SettingsActivity.this, ContactUsActivity.class);
                startActivity(intent);
                break;
            // Opens App Info Setting
            case R.id.infoSection:
                intent = new Intent(SettingsActivity.this, InfoActivity.class);
                startActivity(intent);
                break;
            case R.id.logoutSection:
                sharedPreferences.edit().clear().apply();
                intent = new Intent(SettingsActivity.this, LoginActivity.class);
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
        item2.setVisible(false);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id)
        {
            case R.id.btn_home:
                startActivity(new Intent(SettingsActivity.this,HomeActivity.class));
                break;
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
