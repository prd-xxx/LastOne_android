package xxx.prd.lastone;

import static xxx.prd.lastone.model.ComPlayer.PREF_KEY;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import xxx.prd.lastone.model.ComPlayer;
import xxx.prd.lastone.model.stats.StatsPreferences;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        StatsPreferences pref = new StatsPreferences(this);
        int comLevelOpenStage = pref.loadComLevelOpenStage();
        TextView hintText = (TextView) findViewById(R.id.hint_text);
        if (comLevelOpenStage == 0) {
            int winCount = pref.loadPyramidWinCount(ComPlayer.STRONG);
            String hint = getString(R.string.settings_hint, getString(R.string.com_level_strong), 3 - winCount);
            hintText.setText(hint);
        } else if (comLevelOpenStage == 1) {
            int winCount = pref.loadPyramidWinCount(ComPlayer.VERY_STRONG);
            String hint = getString(R.string.settings_hint, getString(R.string.com_level_very_strong), 3 - winCount);
            hintText.setText(hint);
        } else {
            hintText.setVisibility(View.GONE);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            StatsPreferences pref = new StatsPreferences(getActivity());
            int comLevelOpenStage = pref.loadComLevelOpenStage();
            if (comLevelOpenStage == 1) {
                ListPreference lp = (ListPreference) findPreference(PREF_KEY);
                lp.setEntries(R.array.com_level_entries_open1);
                lp.setEntryValues(R.array.com_level_values_open1);
            } else if (comLevelOpenStage == 2) {
                ListPreference lp = (ListPreference) findPreference(PREF_KEY);
                lp.setEntries(R.array.com_level_entries_open2);
                lp.setEntryValues(R.array.com_level_values_open2);
            }
        }
    }
}