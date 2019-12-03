package mobile.computing.expressstore;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONArray;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;


public class LoginActivity extends AppCompatActivity {

    private EditText email;
    private EditText pass;

    private Button btn;
    private Button btn_signup;
    private TextView forgetpass;

    String encryptedpass = "";
    private static final String KEY = "1Hbfh667adfDEJ78";

    String spass = "";
    String semail = "";
    int flag = 1;

    SharedPreferences mySharedPreferences;
    final static String userPref = "userdata";

    final String login =  "https://expressstorecsci.000webhostapp.com/getusercred.php?usr_email=";
    String furl;

    @Override
    public void onBackPressed() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory( Intent.CATEGORY_HOME );
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mySharedPreferences = getSharedPreferences(userPref, Context.MODE_PRIVATE);

        btn = findViewById(R.id.btn_SignIn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                flag = 1;
                email = findViewById(R.id.inp_Email);
                pass = findViewById(R.id.inp_Pass);

                semail = email.getText().toString();
                spass = pass.getText().toString();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (semail.equals(""))
                {
                    email.setError("Email Cannot be empty");
                    email.requestFocus();
                    flag = 0;
                }
                else if (!email.getText().toString().matches(emailPattern))
                {
                    email.setError("Invalid Email Address");
                    email.requestFocus();
                    flag = 0;
                }

                if (spass.equals(""))
                {
                    pass.setError("Password Cannot be empty");
                    email.requestFocus();
                    flag = 0;
                }

                if (flag == 1)
                {
                    try {
                        encryptedpass =  AESCrypt.encrypt(KEY,spass);
                        furl = login.concat(semail+"&usr_pass="+encryptedpass);
                        getuserid();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_signup = findViewById(R.id.btn_createAcc);

        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SignupActivity.class);
                startActivity(intent);
            }
        });

        forgetpass = findViewById(R.id.forgot_pass);

        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),forgotpassword.class);
                startActivity(intent);
            }
        });
    }


    private void getuserid(){
        StringRequest stringRequest=new StringRequest(furl, new Response.Listener<String>() {
            String apiid = "";
            String apiname = "";
            String apiphone = "";
            String apiemail = "";
            String apipass = "";

            @Override
            public void onResponse(String response) {
                try {
                        if (response.equals("-1"))
                        {
                            Toasty.error(LoginActivity.this,"Wrong Email or Password",Toast.LENGTH_SHORT, true).show();
                        }
                        else
                        {
                            JSONArray user = new JSONArray(response);
                            for(int i=0;i<user.length();i++) {
                                JSONObject order = user.getJSONObject(i);
                                apiid = order.getString("user_id");
                                apiname = order.getString("user_name");
                                apiphone = order.getString("user_phone");
                                apiemail = order.getString("user_email");
                                apipass = order.getString("user_password");
                            }

                            SharedPreferences.Editor editor = mySharedPreferences.edit();

                            editor.putString("customerID",apiid);
                            editor.putString("name",apiname);
                            editor.putString("number",apiphone);
                            editor.putString("email",apiemail);
                            editor.putString("password",apipass);

                            editor.apply();
                            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                        }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(LoginActivity.this,error.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
        });

        Volley.newRequestQueue(this).add(stringRequest);
    }

}