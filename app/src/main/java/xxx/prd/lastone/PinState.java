package xxx.prd.lastone;

public enum PinState {
    ALIVE(R.drawable.pin_alive, true),
    SELECTING(R.drawable.pin_selecting, true),
    GOT_BY_RED(R.drawable.pin_red, false),
    GOT_BY_BLUE(R.drawable.pin_blue, false);
    private final int mDrawableId;
    private final boolean mIsAlive;
    PinState(int drawableId, boolean isAlive) {
        mDrawableId = drawableId;
        mIsAlive = isAlive;
    }
    public int getDrawableId() {
        return mDrawableId;
    }
    public boolean isAlive() {
        return mIsAlive;
    }
}
