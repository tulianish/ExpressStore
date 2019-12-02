package mobile.computing.expressstore;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.paypal.android.sdk.payments.LoginActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class EditProfileActivity extends AppCompatActivity {

    TextInputEditText name, email, number, password, confirmPassword;
    String nameData, numberData, passwordData, emailData, customerID;
    Button cancelButton, saveButton;

    SharedPreferences sharedPreferences;
    final static String userPref = "userdata";

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Set title for activity
        setTitle(R.string.editProfile);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.edit_name_edit);
        email= findViewById(R.id.edit_email_edit);
        number = findViewById(R.id.edit_number_edit);
        password = findViewById(R.id.edit_password_edit);
        confirmPassword = findViewById(R.id.edit_password_confirm_edit);

        cancelButton = findViewById(R.id.edit_cancelButton);
        saveButton = findViewById(R.id.edit_saveButton);

        sharedPreferences = getSharedPreferences(userPref, Context.MODE_PRIVATE);

        if(sharedPreferences.contains("customerID"))
        {
            customerID = sharedPreferences.getString("customerID", null);
            nameData = sharedPreferences.getString("name", null);
            numberData = sharedPreferences.getString("number", null);
            emailData = sharedPreferences.getString("email", null);
            passwordData = sharedPreferences.getString("password", null);
            name.setText(nameData);
            email.setText(emailData);
            number.setText(numberData);
        }
        else
        {
            Toasty.info(this,"Login First", Toast.LENGTH_SHORT, true).show();
            Intent intent = new Intent(EditProfileActivity.this, LoginActivity.class);
        }

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    giveAlert();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateData())
                {
                    if(compareData())
                    {
                        setNewData();
                    }
                    else {
                        EditProfileActivity.this.finish();
                    }
                }
            }
        });

    }

    public boolean validateData()
    {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$";

        if(name.getText().toString().trim().equals(""))
        {
            name.setError("Name cannot be empty!");
            name.requestFocus();
            return false;
        }
        if(number.getText().toString().trim().equals(""))
        {
            number.setError("Number cannot be empty!");
            number.requestFocus();
            return false;
        }
        if(password.getText().toString().trim().equals(""))
        {
            password.setError("Password cannot be empty!");
            password.requestFocus();
            return false;
        }

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password.getText().toString().trim());
        if(!matcher.matches())
        {
            password.setError("Enter password that contains at least one uppercase, " +
                    "one lowercase, one digit, one special character and has minimum length of 6");
            password.requestFocus();
            return false;
        }

        if (password.getText().toString().equals(confirmPassword.getText().toString()))
        {
            confirmPassword.setError("Password does not match!");
            confirmPassword.requestFocus();
            return false;
        }

        return true;

    }

    public boolean compareData()
    {
        if(!name.getText().toString().trim().equals(nameData)
                || !number.getText().toString().trim().equals(numberData)
                || !password.getText().toString().trim().equals(passwordData))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public void setNewData()
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString("name", name.getText().toString().trim());
        editor.putString("number", number.getText().toString().trim());
        editor.putString("password", confirmPassword.getText().toString().trim());

        editor.apply(); // commit changes


        StringRequest stringRequest=new StringRequest(Request.Method.POST,"https://expressstorecsci.000webhostapp.com/update__user_details.php",
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response.equals("0"))
                {
                    Toasty.error(EditProfileActivity.this,"Server Error! Please try again.",Toast.LENGTH_SHORT, true).show();
                }
                Toasty.success(EditProfileActivity.this,"Profile Updated!",Toast.LENGTH_SHORT, true).show();

            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(EditProfileActivity.this,error.getMessage(),Toast.LENGTH_SHORT, true).show();
            }
            })
        {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  args = new HashMap<>();
                args.put("user_id",customerID);
                args.put("user_name",nameData);
                args.put("user_ph",numberData);
                args.put("user_email",emailData);
                args.put("user_password",passwordData);

                return args;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        getMenuInflater().inflate(R.menu.action_menu,menu);
        MenuItem item = menu.findItem(R.id.btn_cart);
        item.setVisible(false);
        MenuItem item1 = menu.findItem(R.id.btn_settings);
        item1.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home)
        {
            this.finish();
        }else if(id == R.id.btn_home) {
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
        }


            return super.onOptionsItemSelected(item);
    }

    public void giveAlert()
    {
        if(compareData())
        {
            new AlertDialog.Builder(EditProfileActivity.this)
                .setMessage("Do you want to save changes?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (validateData())
                        {
                            setNewData();
                            EditProfileActivity.this.finish();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditProfileActivity.this.finish();
                    }
                })
                .setCancelable(false)
                .show();
        }
    }


}
