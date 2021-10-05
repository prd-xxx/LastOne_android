package xxx.prd.lastone.view;

import android.content.Context;
import android.view.ViewGroup;

public class Pin extends androidx.appcompat.widget.AppCompatImageView {
    private final int[] mLocation;
    private final Context mContext;
    private boolean mGotLocation = false;
    private PinState mState;

    public Pin(Context context) {
        super(context);
        mLocation = new int[2];
        mContext = context;
        setPinState(PinState.ALIVE);
        int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
        setLayoutParams(new ViewGroup.LayoutParams(WC,WC));
        setPaddingRelative(63,0,63,0);
    }

    public void setPinState(PinState pinState) {
        mState = pinState;
        setImageDrawable(mContext.getDrawable(mState.getDrawableId()));
    }

    private void getLocationIfNotYet() {
        if (!mGotLocation){
            getLocationOnScreen(mLocation);
            mGotLocation = true;
        }
    }
    public int getCenterX() {
        getLocationIfNotYet();
        return mLocation[0] + getWidth() / 2;
    }
    public int getCenterY() {
        getLocationIfNotYet();
        return mLocation[1] + getHeight() / 2;
    }
    public int getLeftBound() {
        getLocationIfNotYet();
        return mLocation[0];
    }
    public int getRightBound() {
        getLocationIfNotYet();
        return mLocation[0] + getWidth();
    }
    public int getTopBound() {
        getLocationIfNotYet();
        return mLocation[1];
    }
    public int getBottomBound() {
        getLocationIfNotYet();
        return mLocation[1] + getHeight();
    }
    public boolean isAlive() {
        return mState.isAlive();
    }
    public boolean isSelecting() {
        return mState == PinState.SELECTING;
    }
}
