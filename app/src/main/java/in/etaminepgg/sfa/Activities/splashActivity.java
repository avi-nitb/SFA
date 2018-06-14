package in.etaminepgg.sfa.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.apache.commons.logging.Log;

import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.MySharedPrefrencesData;
import in.etaminepgg.sfa.Utilities.Utils;

public class splashActivity extends AppCompatActivity
{

    boolean moveToNextScreen = false;
    int SPLASH_TIMEOUT = 3000;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(splashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMEOUT);


     /*   SharedPreferences settings = getSharedPreferences(MySharedPrefrencesData.PREFS_NAME, 0);
        //Get "hasLoggedIn" value. If the value doesn't exist yet false is returned
        boolean hasLoggedIn = settings.getBoolean("hasLoggedIn", false);

        if (hasLoggedIn) {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Intent intent = new Intent(splashActivity.this, DashboardActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIMEOUT);

        } else {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    Intent intent = new Intent(splashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, SPLASH_TIMEOUT);
        }*/

    }

}
