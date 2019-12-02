package mobile.computing.expressstore;

import android.os.Bundle;
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

public class forgotpassword extends AppCompatActivity {

    private Button SendEmail;
    private EditText email;
    String messageAfterDecrypt="";

    private static final String KEY = "1Hbfh667adfDEJ78";

    final String url = "https://expressstorecsci.000webhostapp.com/forgotpassword.php?user_email=";
    String furl;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);

        email = findViewById(R.id.Email);
        SendEmail = findViewById(R.id.Btn_sendemail);

        SendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                furl = url.concat(email.getText().toString().trim());
                StringRequest stringRequest = new StringRequest(furl, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try {

                            if (response.equals("-1"))
                                {
                                    Toast.makeText(forgotpassword.this,"Email not exist",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    //Decryption
                                    try {
                                        messageAfterDecrypt = AESCrypt.decrypt(KEY,response);
                                        //System.out.println(messageAfterDecrypt);
                                        Toast.makeText(getApplicationContext(),messageAfterDecrypt, Toast.LENGTH_LONG).show();
                                    }
                                    catch (Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                    /////////////////////Email sent logic
                                }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(forgotpassword.this, "error:"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                Volley.newRequestQueue(forgotpassword.this).add(stringRequest);
            }
        });

    }
}

