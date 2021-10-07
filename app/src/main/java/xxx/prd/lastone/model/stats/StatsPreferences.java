package xxx.prd.lastone.model.stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import xxx.prd.lastone.model.ComPlayer;
import xxx.prd.lastone.model.Placement;

public class StatsPreferences {
    private static final String PREF_NAME = "stats";
    private static final String PREFIX_WIN_COUNT = "win_count_";
    private static final String PREFIX_HISTORY = "history_";
    private static final String KEY_COM_LEVEL_OPEN = "com_level_open";
    private static final int HISTORY_LENGTH = 30;
    private final Context mContext;
    public StatsPreferences(Context context) {
        mContext = context;
    }
    private SharedPreferences getSharedPreferences() {
        return mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    private void saveInt(String key, int value) {
        getSharedPreferences().edit().putInt(key, value).apply();
    }
    private void saveString(String key, String value) {
        getSharedPreferences().edit().putString(key, value).apply();
    }
    private int loadInt(String key, int defValue) {
        return getSharedPreferences().getInt(key, defValue);
    }
    private String loadString(String key, String defValue) {
        return getSharedPreferences().getString(key, defValue);
    }
    public void incrementWinCount(ComPlayer comPlayer, Placement placement) {
        String key = PREFIX_WIN_COUNT + comPlayer.toString() + placement.getSuffixForStats();
        int c = loadInt(key, 0) + 1;
        saveInt(key, c);
        Log.d("save", "key=" + key + ", c=" + c);
        if(placement == Placement.PYRAMID) {
            if (comPlayer == ComPlayer.STRONG && c == 3) {
                saveInt(KEY_COM_LEVEL_OPEN, 1);
            } else if (comPlayer == ComPlayer.VERY_STRONG && c == 3) {
                saveInt(KEY_COM_LEVEL_OPEN, 2);
            }
        }
    }
    public int loadComLevelOpenStage() {
        return loadInt(KEY_COM_LEVEL_OPEN, 0);
    }
    public int loadPyramidWinCount(ComPlayer comPlayer) {
        String keyPyramid = PREFIX_WIN_COUNT + comPlayer.toString() + Placement.PYRAMID.getSuffixForStats();
        return loadInt(keyPyramid, 0);
    }
    public void addRecentHistory(ComPlayer comPlayer, Placement placement, boolean isWin) {
        String key = PREFIX_HISTORY + comPlayer.toString() + placement.getSuffixForStats();
        String history = loadString(key, "");
        history += (isWin ? "o" : "x");
        if (history.length() > HISTORY_LENGTH) history = history.substring(1);
        Log.d("save", "key=" + key + ", history=" + history);
        saveString(key, history);
    }

}
