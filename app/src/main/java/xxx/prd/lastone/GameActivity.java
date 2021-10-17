package xxx.prd.lastone;

import static xxx.prd.lastone.model.ComPlayer.PREF_KEY;
import static xxx.prd.lastone.model.Game.ROW_NUM;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import xxx.prd.lastone.model.ComPlayer;
import xxx.prd.lastone.model.Game;
import xxx.prd.lastone.model.IComPlayer;
import xxx.prd.lastone.model.Operation;
import xxx.prd.lastone.model.Placement;
import xxx.prd.lastone.model.stats.StatsPreferences;
import xxx.prd.lastone.view.Line;
import xxx.prd.lastone.view.PaintView;
import xxx.prd.lastone.view.Pin;
import xxx.prd.lastone.view.PinState;

public class GameActivity extends Activity {

    static final String INTENT_EXTRA_MODE = "MODE";
    static final int MODE_ONE_PLAYER = 1;
    static final int MODE_TWO_PLAYERS = 2;
    private static final int[] SOUNDS =
            new int[] {R.raw.pin1, R.raw.pin2, R.raw.pin3, R.raw.pin4, R.raw.pin5, R.raw.pin6};
    private int mMode;
    private PaintView mPaintView;
    private List<Pin>[] mPins;
    private Game mGame;
    private IComPlayer mComPlayer = null;
    private Handler mHandler;
    private ProgressBar mProgressBar;
    private TextView mRemainPinsTextView;
    private boolean mAreYouFirst;
    private ComPlayer mComPlayerEnum;
    private MediaPlayer mBgmPlayer;
    private SoundPool mSoundPool;
    private int[] mSoundIds;
    private SharedPreferences mSharedPref;
    private Placement mPlacement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mPaintView = (PaintView) findViewById(R.id.paint_view);
        mPaintView.setActivity(this);

        mMode = getIntent().getIntExtra(INTENT_EXTRA_MODE, MODE_TWO_PLAYERS);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        if(mMode == MODE_ONE_PLAYER) {
            String level = mSharedPref.getString(PREF_KEY, ComPlayer.defaultComPlayer().getPrefValue());
            mComPlayerEnum = ComPlayer.findByPrefValue(level);
            try {
                mComPlayer = mComPlayerEnum.getComPlayerClass().newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            TextView comLevel = (TextView) findViewById(R.id.opponent_level);
            comLevel.setText(getString(mComPlayerEnum.getNameId()));
            Log.d("com player", mComPlayerEnum.toString());
        } else {
            findViewById(R.id.opponent_color).setVisibility(View.INVISIBLE);
            findViewById(R.id.opponent).setVisibility(View.INVISIBLE);
        }
        mHandler = new Handler();
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mRemainPinsTextView = (TextView) findViewById(R.id.pin_remain);

        String gamePlacement = mSharedPref.getString(Placement.PREF_KEY, Placement.PYRAMID.getPrefValue());
        mPlacement = Placement.fromPrefValue(gamePlacement);
        mGame = Game.newGame(mPlacement);
        mPins = new List[ROW_NUM];
        for (int i=0; i<ROW_NUM; i++) {
            mPins[i] = new ArrayList<>();
            addPins(i, mGame.getColNum(i));
        }
        TextView remainPins = (TextView) findViewById(R.id.pin_remain);
        remainPins.setText(mGame.getRemainPinNum() + "");

        if (mMode == MODE_ONE_PLAYER) {
            showTurnSelectDialog();
        }
        Log.d("mode", mMode + "");
        Log.d("game", mGame.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        boolean play = mSharedPref.getBoolean("sound", true);
        if(play) {
            mBgmPlayer = MediaPlayer.create(this, R.raw.lastone_game);
            mBgmPlayer.setLooping(true);
            mBgmPlayer.start();

            AudioAttributes attr = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            mSoundPool = new SoundPool.Builder()
                    .setAudioAttributes(attr)
                    .setMaxStreams(6)
                    .build();
            mSoundIds = new int[SOUNDS.length];
            for (int i=0; i<SOUNDS.length; i++) {
                mSoundIds[i] = mSoundPool.load(this, SOUNDS[i], 1);
            }
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (mBgmPlayer != null) {
            mBgmPlayer.release();
            mBgmPlayer = null;
        }
        if (mSoundPool != null) {
            mSoundPool.release();
            mSoundPool = null;
        }
    }

    private void addPins(int rowIndex, int pinNum) {
        PinsRowLayout pinsLayout = new PinsRowLayout(this, rowIndex);
        for (int i=0; i<pinNum; i++) {
            Pin pin = new Pin(this);
            pinsLayout.addView(pin);
            mPins[rowIndex].add(pin);
        }
        LinearLayout parent = (LinearLayout) findViewById(R.id.game_linearlayout);
        parent.addView(pinsLayout);
    }

    private void playPinSound(int pinNum) {
        if(mSoundPool != null) {
            mSoundPool.play(mSoundIds[pinNum - 1], 1, 1, 1, 0, 1);
        }
    }

    public void onLineDrugging(Line line) {
        for (int i=0; i<ROW_NUM; i++) {
            for (Pin pin: mPins[i]) {
                if (pin.isAlive()) {
                    if (line.acrossPin(pin)) {
                        pin.setPinState(PinState.SELECTING);
                    } else {
                        pin.setPinState(PinState.ALIVE);
                    }
                }
            }
        }
    }
    public void onLineEnded(Line line) {
        int erasePin = 0;
        for (int i=0; i<ROW_NUM; i++) {
            int j = 0;
            for (Pin pin: mPins[i]) {
                if (line.acrossPin(pin)) {
                    mGame.erasePinAt(i, j);
                    erasePin++;
                    if(mGame.isRedTurn()) {
                        pin.setPinState(PinState.GOT_BY_RED);
                    } else {
                        pin.setPinState(PinState.GOT_BY_BLUE);
                    }
                } else if (pin.isAlive()){
                    pin.setPinState(PinState.ALIVE);
                }
                j++;
            }
        }
        playPinSound(erasePin);
        mRemainPinsTextView.setText(mGame.getRemainPinNum() + "");

        mGame.flipTurnManually();
        if (mGame.getRemainPinNum() == 0) {
            showGameEndDialog();
        } else {
            flipTurn();
            if (mMode == MODE_ONE_PLAYER) {
                operateComTurn();
            }
        }
        Log.d("game", mGame.toString());
    }
    private void flipTurn() {
        TextView whoseTurn = (TextView) findViewById(R.id.whose_turn);
        if (mGame.isRedTurn()) {
            int playerStrId = mMode == MODE_ONE_PLAYER && mAreYouFirst ? R.string.you : R.string.red;
            whoseTurn.setText(playerStrId);
            whoseTurn.setTextColor(Color.RED);
        } else {
            int playerStrId = mMode == MODE_ONE_PLAYER && !mAreYouFirst ? R.string.you : R.string.blue;
            whoseTurn.setText(playerStrId);
            whoseTurn.setTextColor(Color.BLUE);
        }
    }
    private void drawComOperation(Operation ope) {
        playPinSound(ope.getToCol() - ope.getFromCol() + 1);
        for(int col=ope.getFromCol(); col<=ope.getToCol(); col++) {
            mPins[ope.getRow()].get(col).setPinState(mGame.isRedTurn() ? PinState.GOT_BY_RED : PinState.GOT_BY_BLUE);
        }
        int y = mPins[ope.getRow()].get(0).getCenterY();
        int l = mPins[ope.getRow()].get(ope.getFromCol()).getLeftBound();
        int r = mPins[ope.getRow()].get(ope.getToCol()).getRightBound();
        mPaintView.drawByComOperation(y, l, r);
    }
    private void operateComTurn() {
        mProgressBar.setVisibility(View.VISIBLE);
        mHandler.post(() -> {
            Operation comOpe = mComPlayer.chooseOperation(mGame);
            drawComOperation(comOpe);
            mGame.doOperation(comOpe);
            mRemainPinsTextView.setText(mGame.getRemainPinNum() + "");
            if (mGame.getRemainPinNum() == 0) {
                showGameEndDialog();
            } else {
                flipTurn();
            }
            mProgressBar.setVisibility(View.GONE);
            mPaintView.invalidate();
        });
    }

    public boolean isValidLine(Line line) {
        int selectedRow = -1;
        for (int i=0; i<ROW_NUM; i++) {
            for (Pin pin: mPins[i]) {
                if (line.acrossPin(pin)) {
                    if(!pin.isAlive()) {
                        Toast.makeText(this, R.string.toast_cant_draw, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    if(selectedRow == -1) {
                        selectedRow = i;
                    } else if(selectedRow != i) {
                        Toast.makeText(this, R.string.toast_cant_draw, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
            }
        }
        return selectedRow != -1;
    }
    public void clearSelecting() {
        for (int i=0; i<ROW_NUM; i++) {
            for (Pin pin: mPins[i]) {
                if (pin.isSelecting()) pin.setPinState(PinState.ALIVE);
            }
        }
    }
    public boolean isRedTurn() {
        return mGame.isRedTurn();
    }

    private void showTurnSelectDialog() {
        TextView opponentColor = (TextView) findViewById(R.id.opponent_color);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_turn_select, null);
        //バックキー等でダイアログが閉じられた場合は先攻として扱う
        DialogInterface.OnDismissListener dismissListener = (dialog) -> {
            if(opponentColor.getCurrentTextColor() == Color.RED) return;
            if(opponentColor.getCurrentTextColor() == Color.BLUE) return;
            mAreYouFirst = true;
            opponentColor.setTextColor(Color.BLUE);
            TextView whoseTurn = (TextView) findViewById(R.id.whose_turn);
            whoseTurn.setText(R.string.you);
            whoseTurn.invalidate();
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = builder.setView(dialogView)
                .setOnDismissListener(dismissListener)
                .create();
        Button firstButton = (Button) dialogView.findViewById(R.id.first_button);
        firstButton.setOnClickListener(view -> {
            mAreYouFirst = true;
            opponentColor.setTextColor(Color.BLUE);
            TextView whoseTurn = (TextView) findViewById(R.id.whose_turn);
            whoseTurn.setText(R.string.you);
            whoseTurn.invalidate();
            dialog.dismiss();
        });
        Button secondButton = (Button) dialogView.findViewById(R.id.second_button);
        secondButton.setOnClickListener(view -> {
            mAreYouFirst = false;
            opponentColor.setTextColor(Color.RED);
            operateComTurn();
            dialog.dismiss();
        });
        dialog.show();
    }

    private void showGameEndDialog() {
        if (mBgmPlayer != null) {
            mBgmPlayer.stop();
            mBgmPlayer.release();
            boolean isWin = mMode == MODE_TWO_PLAYERS || mGame.isRedTurn() == mAreYouFirst;
            mBgmPlayer = MediaPlayer.create(this, isWin ? R.raw.game_win : R.raw.game_lose);
            mBgmPlayer.start();
        }
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_game_end, null);
        Button retryButton = (Button) dialogView.findViewById(R.id.retry_button);
        retryButton.setOnClickListener(view -> {
            Intent intent = new Intent(GameActivity.this, GameActivity.class);
            intent.putExtra(INTENT_EXTRA_MODE, mMode);
            startActivity(intent);
        });
        Button quitButton = (Button) dialogView.findViewById(R.id.quit_button);
        quitButton.setOnClickListener(view -> finish());
        ImageButton tweetButton = (ImageButton) dialogView.findViewById(R.id.tweet_button);
        tweetButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getTweetResultText()));
            startActivity(intent);
        });
        DialogInterface.OnDismissListener dismissListener = (dialog) -> {
            finish();
        };
        TextView dialogTitle = (TextView) dialogView.findViewById(R.id.dialog_title);

        if(mMode == MODE_ONE_PLAYER) {
            boolean isWin = mGame.isRedTurn() == mAreYouFirst;
            StatsPreferences pref = new StatsPreferences(this);
            pref.addRecentHistory(mComPlayerEnum, mPlacement, isWin);
            if (isWin) pref.incrementWinCount(mComPlayerEnum, mPlacement);
            int msgId = isWin ? R.string.you_win : R.string.you_lose;
            dialogTitle.setText(getString(msgId));
        } else {
            String winColor = getString(mGame.isRedTurn() ? R.string.red : R.string.blue);
            dialogTitle.setText(winColor + getString(R.string.wins));
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView)
                .setOnDismissListener(dismissListener)
                .show();
    }
    private String getTweetResultText() {
        boolean isWin = mGame.isRedTurn() == mAreYouFirst;
        StringBuilder url = new StringBuilder();
        String tweetText;
        if(mMode == MODE_ONE_PLAYER) {
            String level = getString(mComPlayerEnum.getNameId());
            String placement = getString(mPlacement.getNameId());
            tweetText = getString(isWin ? R.string.tweet_1player_win : R.string.tweet_1player_lose, placement, level);
        } else {
            String winColor = getString(mGame.isRedTurn() ? R.string.red : R.string.blue);
            tweetText = winColor + getString(R.string.wins);
        }
        url.append("https://twitter.com/share?text=").append(tweetText)
                .append("&hashtags=").append("ラストワン,LastOne_App")
                .append("&url=").append("https://play.google.com/store/apps/details?id=xxx.prd.lastone");
        return url.toString();
    }

    private class PinsRowLayout extends LinearLayout {
        public PinsRowLayout(Context context, int rowIndex) {
            super(context);
            int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
            int MP = ViewGroup.LayoutParams.MATCH_PARENT;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MP,WC);
            int verticalMargin = (int) getResources().getDimension(R.dimen.pin_vertical_margin);
            lp.setMargins(0,verticalMargin,0,0);
            setLayoutParams(lp);
            setGravity(Gravity.CENTER_HORIZONTAL);
            setTag(R.id.TAG_ROW_INDEX, rowIndex);
        }
    }

}