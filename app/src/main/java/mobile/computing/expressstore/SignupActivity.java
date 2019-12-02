package mobile.computing.expressstore;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scottyab.aescrypt.AESCrypt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class SignupActivity extends AppCompatActivity {

    private Button signup;
    private Button cancel;
    private EditText phonenumber,email,name;
    private EditText passwrd,cnfpasswrd;

    private static final String KEY = "1Hbfh667adfDEJ78";

    String encryptedpass = "";

    final String register =  "https://expressstorecsci.000webhostapp.com/registeruser.php?user_name=";
    String furl;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signup = findViewById(R.id.Submit);
        cancel = findViewById(R.id.Cancel);

        phonenumber = findViewById(R.id.UsrPhno);
        email = findViewById(R.id.UsrEmail);
        passwrd = findViewById(R.id.pass);
        cnfpasswrd = findViewById(R.id.cnfpass);
        name = findViewById(R.id.UsrName);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int flag = 1;

                if (name.getText().toString().trim().equals(""))
                {
                    name.setError("Name cannot be empty");
                    name.requestFocus();
                    flag = 0;
                }

                boolean emailsyntax = isEmailValid(email.getText().toString().trim());

                if (email.getText().toString().trim().equals(""))
                {
                    email.setError("Email cannot be empty");
                    email.requestFocus();
                    flag = 0;
                }
                else if (!emailsyntax)
                {
                    email.setError("Invalid EmailId");
                    email.requestFocus();
                    flag = 0;
                }

                boolean phonesyntax = validCellPhone(phonenumber.getText().toString().trim());

                if (phonenumber.getText().toString().trim().equals(""))
                {
                    phonenumber.setError("Phone Number cannot be empty");
                    phonenumber.requestFocus();
                    flag = 0;
                }
                else if (!phonesyntax)
                {
                    phonenumber.setError("Invalid Phone Number");
                    phonenumber.requestFocus();
                    flag = 0;
                }

                if (passwrd.getText().toString().trim().equals(""))
                {
                    passwrd.setError("Password Cannot be empty");
                    passwrd.requestFocus();
                    flag = 0;
                }
                else
                {
                    try
                    {
                        //Encryption
                        encryptedpass =  AESCrypt.encrypt(KEY,passwrd.getText().toString());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                if (flag == 1)
                {
                    furl = register.concat(name.getText().toString().trim()+"&user_ph="+phonenumber.getText().toString().trim()+"&user_email="+email.getText().toString().trim()+"&user_password="+encryptedpass);
                    registeruser();
                }

            }
        });

        cnfpasswrd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0 && passwrd.getText().toString().length() > 0)
                {
                    if (!cnfpasswrd.getText().toString().equals(passwrd.getText().toString()))
                    {
                        cnfpasswrd.setError("Does not match with Password");
                        cnfpasswrd.requestFocus();
                    }
                }
            }
        });
    }

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean validCellPhone(String number)
    {
        String MobilePattern = "[0-9]{10}";

        if(number.matches(MobilePattern))
        {
            return true;
        }
        return false;
    }


    private void registeruser(){
        StringRequest stringRequest=new StringRequest(furl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (response.equals("-1"))
                    {
                        Toasty.warning(SignupActivity.this,"Email Already Registered",Toast.LENGTH_SHORT, true).show();
                    }
                    else if(response.equals("1"))
                    {
                       Toasty.success(SignupActivity.this,"Registered Successfully",Toast.LENGTH_SHORT, true).show();
                       startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                    }
                    else
                    {
                        Toasty.error(SignupActivity.this,"Registration Failed",Toast.LENGTH_SHORT, true).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(SignupActivity.this,error.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
