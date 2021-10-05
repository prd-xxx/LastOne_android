package xxx.prd.lastone.view;

public class Line {
    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;
    private int mOffsetY;
    private final int UNDEFINED = -1;
    public Line() {
        mStartX = mStartY = mEndX = mEndY = mOffsetY = UNDEFINED;
    }
    private Line(int startX, int startY, int endX, int endY, int offsetY) {
        mStartX = startX;
        mStartY = startY;
        mEndX = endX;
        mEndY = endY;
        mOffsetY = offsetY;
    }
    public Line clone() {
        return new Line(mStartX, mStartY, mEndX, mEndY, mOffsetY);
    }
    public void setStart(int x, int y) {
        mStartX = x;
        mStartY = y;
    }
    public void setEnd(int x, int y) {
        mEndX = x;
        mEndY = y;
    }
    public void setOffsetY(int offsetY) {
        mOffsetY = offsetY;
    }
    public boolean isDefinite() {
        return mStartX != UNDEFINED && mEndX != UNDEFINED;
    }
    public int getStartX() {
        return mStartX;
    }
    public int getStartY() {
        return mStartY;
    }
    public int getEndX() {
        return mEndX;
    }
    public int getEndY() {
        return mEndY;
    }
    public void clear() {
        mStartX = mEndX = UNDEFINED;
    }
    public boolean acrossPin(Pin pin) {
        if (!isDefinite() || mStartX == mEndX) return false;
        int l = Math.min(mStartX,mEndX);
        int r = Math.max(mStartX,mEndX);
        int pinX = pin.getCenterX();
        if (r < pinX || pinX < l) return false;
        int y;
        if (mStartX < mEndX)
            y = mStartY + (mEndY - mStartY) * (pinX - mStartX) / (mEndX - mStartX);
        else
            y = mEndY + (mStartY - mEndY) * (pinX - mEndX) / (mStartX - mEndX);
        y += mOffsetY;
        return pin.getTopBound() <= y && y <= pin.getBottomBound();
    }
}
