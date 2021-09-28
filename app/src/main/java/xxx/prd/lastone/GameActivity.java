package xxx.prd.lastone;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class GameActivity extends Activity {

    private final int ROW_NUM = 6;
    private List<Pin>[] mPins;
    private boolean mIsRedTurn = true;
    private int mRemainPins = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        PaintView paintView = (PaintView) findViewById(R.id.paint_view);
        paintView.setActivity(this);

        mPins = new List[ROW_NUM];
        for (int i=0; i<ROW_NUM; i++) {
            mPins[i] = new ArrayList<>();
            addPins(i, i+1);
        }
        TextView remainPins = (TextView) findViewById(R.id.pin_remain);
        remainPins.setText(mRemainPins + "");
    }

    private void addPins(int rowIndex, int pinNum) {
        PinsRowLayout pinsLayout = new PinsRowLayout(this, rowIndex);
        for (int i=0; i<pinNum; i++) {
            Pin pin = new Pin(this);
            pinsLayout.addView(pin);
            mPins[rowIndex].add(pin);
            mRemainPins++;
        }
        LinearLayout parent = (LinearLayout) findViewById(R.id.game_linearlayout);
        parent.addView(pinsLayout);
    }

    public void onLineDrugging(Line line) {
        for (int i=0; i<ROW_NUM; i++) {
            int j = 0;
            for (Pin pin: mPins[i]) {
                if (pin.isAlive()) {
                    if (line.acrossPin(pin)) {
                        pin.setPinState(PinState.SELECTING);
                    } else {
                        pin.setPinState(PinState.ALIVE);
                    }
                }
                j++;
            }
        }
    }
    public void onLineEnded(Line line) {
        for (int i=0; i<ROW_NUM; i++) {
            int j = 0;
            for (Pin pin: mPins[i]) {
                if (line.acrossPin(pin)) {
                    if(mIsRedTurn) {
                        pin.setPinState(PinState.GOT_BY_RED);
                    } else {
                        pin.setPinState(PinState.GOT_BY_BLUE);
                    }
                    mRemainPins--;
                } else if (pin.isAlive()){
                    pin.setPinState(PinState.ALIVE);
                }
                j++;
            }
        }
        TextView remainPins = (TextView) findViewById(R.id.pin_remain);
        remainPins.setText(mRemainPins + "");
        if (mRemainPins == 0) {
            showGameEndDialog();
        } else {
            flipTurn();
        }
    }
    private void flipTurn() {
        mIsRedTurn = !mIsRedTurn;
        TextView whoseTurn = (TextView) findViewById(R.id.whose_turn);
        if (mIsRedTurn) {
            whoseTurn.setText(R.string.red);
            whoseTurn.setTextColor(Color.RED);
        } else {
            whoseTurn.setText(R.string.blue);
            whoseTurn.setTextColor(Color.BLUE);
        }
    }
    public boolean isValidLine(Line line) {
        int selectedRow = -1;
        for (int i=0; i<ROW_NUM; i++) {
            int j = 0;
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
                j++;
            }
        }
        return selectedRow != -1;
    }
    public void clearSelecting() {
        for (int i=0; i<ROW_NUM; i++) {
            int j = 0;
            for (Pin pin: mPins[i]) {
                if (pin.isSelecting()) pin.setPinState(PinState.ALIVE);
            }
        }
    }
    private void showGameEndDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        finish();
                        Intent intent = new Intent(GameActivity.this, GameActivity.class);
                        startActivity(intent);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        finish();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String winColor = getString(mIsRedTurn ? R.string.blue : R.string.red);
        builder.setMessage(winColor + getString(R.string.wins))
                .setPositiveButton(R.string.retry, dialogClickListener)
                .setNegativeButton(R.string.quit, dialogClickListener)
                .show();
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