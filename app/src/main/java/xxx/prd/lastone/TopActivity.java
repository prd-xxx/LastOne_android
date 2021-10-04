package xxx.prd.lastone;

import static xxx.prd.lastone.GameActivity.INTENT_EXTRA_MODE;
import static xxx.prd.lastone.GameActivity.MODE_ONE_PLAYER;
import static xxx.prd.lastone.GameActivity.MODE_TWO_PLAYERS;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TopActivity extends Activity {

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

        TextView versionName = (TextView) findViewById(R.id.version_name);
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            versionName.setText(version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
