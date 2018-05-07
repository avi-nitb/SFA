package in.etaminepgg.sfa.Activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;
import in.etaminepgg.sfa.R;
import in.etaminepgg.sfa.Utilities.Constants;
import in.etaminepgg.sfa.Utilities.ConstantsA;
import in.etaminepgg.sfa.Utilities.DbUtils;
import in.etaminepgg.sfa.Utilities.MyDb;

import static in.etaminepgg.sfa.Utilities.Constants.TBL_SKU;
import static in.etaminepgg.sfa.Utilities.Constants.dbFileFullPath;

public class SkuVideoActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sku_video);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle((CharSequence) "Sku Videos");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        VideoView vidView = (VideoView) findViewById(R.id.myVideo);

        vidView.setVideoURI(Uri.parse(getIntent().getStringExtra(ConstantsA.KEY_SKU_ID)));

        vidView.start();
        MediaController vidControl = new MediaController(this);
        vidControl.setAnchorView(vidView);
        vidView.setMediaController(vidControl);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
