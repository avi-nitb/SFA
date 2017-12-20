package in.etaminepgg.sfa.Utilities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import java.util.Random;

import in.etaminepgg.sfa.Activities.LoginActivity;

public class MyUi{

    public static void popUp( String msg )
    {
        Toast.makeText(LoginActivity.baseContext, msg,
                Toast.LENGTH_LONG).show();
    }


    static void popUpQuick( String msg )
    {
        Toast.makeText(LoginActivity.baseContext, msg,
                Toast.LENGTH_SHORT).show();
    }


    static void popupOk( String msg )
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(LoginActivity.baseContext);
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

    public static void Sleep( int delay )
    {
        try {
            Thread.sleep(delay);
        }
        catch( Exception e )
        {
            popUp( "Unable to create time delay. Pleaes contact Etamine Support.");
        }


    }

    public static int genSessionId()
    {
        int min = 100000;
        int max = 999999;
        Random r = new Random();
        int ret = r.nextInt( max-min + 1 ) + min;
        return( ret );
    }

}
