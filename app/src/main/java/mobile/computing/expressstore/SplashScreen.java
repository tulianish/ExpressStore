package mobile.computing.expressstore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Hide ActionBar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        // Get Handler to display splash-screen for 500 milliseconds
        handler=new Handler();
        // Create new thread for delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreen.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        },500);

    }
}
