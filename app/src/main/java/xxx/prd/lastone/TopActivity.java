package xxx.prd.lastone;

import static xxx.prd.lastone.GameActivity.INTENT_EXTRA_MODE;
import static xxx.prd.lastone.GameActivity.MODE_ONE_PLAYER;
import static xxx.prd.lastone.GameActivity.MODE_TWO_PLAYERS;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;

import androidx.preference.PreferenceManager;

public class TopActivity extends Activity {
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);

        findViewById(R.id.one_player_button).setOnClickListener(view -> {
            Intent intent = new Intent(TopActivity.this, GameActivity.class);
            intent.putExtra(INTENT_EXTRA_MODE, MODE_ONE_PLAYER);
            startActivity(intent);
        });
        findViewById(R.id.two_players_button).setOnClickListener(view -> {
            Intent intent = new Intent(TopActivity.this, GameActivity.class);
            intent.putExtra(INTENT_EXTRA_MODE, MODE_TWO_PLAYERS);
            startActivity(intent);
        });
        findViewById(R.id.settings_button).setOnClickListener(view -> {
            Intent intent = new Intent(TopActivity.this, SettingsActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.history_button).setOnClickListener(view -> {
            Intent intent = new Intent(TopActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        TextView versionName = (TextView) findViewById(R.id.version_name);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionName.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean play = pref.getBoolean("sound", true);
        if(play) {
            mMediaPlayer = MediaPlayer.create(this, R.raw.lastone_system);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
