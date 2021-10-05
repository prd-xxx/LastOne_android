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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import xxx.prd.lastone.view.Line;
import xxx.prd.lastone.view.PaintView;
import xxx.prd.lastone.view.Pin;
import xxx.prd.lastone.view.PinState;

public class GameActivity extends Activity {

    static final String INTENT_EXTRA_MODE = "MODE";
    static final int MODE_ONE_PLAYER = 1;
    static final int MODE_TWO_PLAYERS = 2;
    private int mMode;
    private PaintView mPaintView;
    private List<Pin>[] mPins;
    private Game mGame;
    private IComPlayer mComPlayer = null;
    private Handler mHandler;
    private ProgressBar mProgressBar;
    private TextView mRemainPinsTextView;
    private boolean mAreYouFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        mPaintView = (PaintView) findViewById(R.id.paint_view);
        mPaintView.setActivity(this);

        mMode = getIntent().getIntExtra(INTENT_EXTRA_MODE, MODE_TWO_PLAYERS);
        if(mMode == MODE_ONE_PLAYER) {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
            String level = pref.getString(PREF_KEY, ComPlayer.defaultComPlayer().getPrefValue());
            ComPlayer comPlayer = ComPlayer.findByPrefValue(level);
            try {
                mComPlayer = comPlayer.getComPlayerClass().newInstance();
            } catch (IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
            TextView comLevel = (TextView) findViewById(R.id.opponent_level);
            comLevel.setText(getString(comPlayer.getNameId()));
            Log.d("com player", comPlayer.toString());
        } else {
            findViewById(R.id.opponent_color).setVisibility(View.INVISIBLE);
            findViewById(R.id.opponent).setVisibility(View.INVISIBLE);
        }
        mHandler = new Handler();
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mRemainPinsTextView = (TextView) findViewById(R.id.pin_remain);

        mGame = new Game(new int[]{1,2,3,4,5,6});
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
        for (int i=0; i<ROW_NUM; i++) {
            int j = 0;
            for (Pin pin: mPins[i]) {
                if (line.acrossPin(pin)) {
                    mGame.erasePinAt(i, j);
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
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            TextView opponentColor = (TextView) findViewById(R.id.opponent_color);
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    mAreYouFirst = false;
                    opponentColor.setTextColor(Color.RED);
                    operateComTurn();
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    mAreYouFirst = true;
                    opponentColor.setTextColor(Color.BLUE);
                    TextView whoseTurn = (TextView) findViewById(R.id.whose_turn);
                    whoseTurn.setText(R.string.you);
                    whoseTurn.invalidate();
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.turn_select))
                .setPositiveButton(R.string.second, dialogClickListener)
                .setNegativeButton(R.string.first, dialogClickListener)
                .show();
    }

    private void showGameEndDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    Intent intent = new Intent(GameActivity.this, GameActivity.class);
                    intent.putExtra(INTENT_EXTRA_MODE, mMode);
                    startActivity(intent);
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    finish();
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if(mMode == MODE_ONE_PLAYER) {
            int msgId = mGame.isRedTurn() ^ mAreYouFirst ? R.string.you_lose : R.string.you_win;
            builder.setMessage(getString(msgId))
                    .setPositiveButton(R.string.retry, dialogClickListener)
                    .setNegativeButton(R.string.quit, dialogClickListener)
                    .show();

        } else {
            String winColor = getString(mGame.isRedTurn() ? R.string.red : R.string.blue);
            builder.setMessage(winColor + getString(R.string.wins))
                    .setPositiveButton(R.string.retry, dialogClickListener)
                    .setNegativeButton(R.string.quit, dialogClickListener)
                    .show();

        }
    }

    private class PinsRowLayout extends LinearLayout {
        public PinsRowLayout(Context context, int rowIndex) {
            super(context);
            int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
            int MP = ViewGroup.LayoutParams.MATCH_PARENT;
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(MP,WC);
            lp.setMargins(0,16,0,0);
            setLayoutParams(lp);
            setGravity(Gravity.CENTER_HORIZONTAL);
            setTag(R.id.TAG_ROW_INDEX, rowIndex);
        }
    }

}