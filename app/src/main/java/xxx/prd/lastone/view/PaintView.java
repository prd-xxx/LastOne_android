package xxx.prd.lastone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.MotionEventCompat;

import java.util.ArrayList;
import java.util.List;

import xxx.prd.lastone.GameActivity;
import xxx.prd.lastone.R;

public class PaintView extends View {
    private Paint mPaint;
    private Line mCurrentLine;
    private List<Line> mRedDefiniteLines;
    private List<Line> mBlueDefiniteLines;
    private GameActivity mActivity;

    public PaintView(Context context) {
        super(context);
        init();
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PaintView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        int width = (int) getResources().getDimension(R.dimen.margin_quarter);
        mPaint.setStrokeWidth(width);
        mCurrentLine = new Line();
        mRedDefiniteLines = new ArrayList<>();
        mBlueDefiniteLines = new ArrayList<>();
    }

    public void setActivity(GameActivity activity) {
        mActivity = activity;
    }

    public void drawByComOperation(int y, int l, int r) {
        y -= getOffsetY();
        mCurrentLine.setStart(l + 8, y);
        mCurrentLine.setEnd(r - 8, y);
        if(mActivity.isRedTurn()) {
            mRedDefiniteLines.add(mCurrentLine.clone());
        } else {
            mBlueDefiniteLines.add(mCurrentLine.clone());
        }
        mCurrentLine.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCurrentLine.setOffsetY(getOffsetY());
        mPaint.setColor(Color.RED);
        for (Line redLine: mRedDefiniteLines) {
            drawLine(canvas, redLine);
        }
        mPaint.setColor(Color.BLUE);
        for (Line blueLine: mBlueDefiniteLines) {
            drawLine(canvas, blueLine);
        }
        if (mCurrentLine.isDefinite()) {
            if (mActivity.isRedTurn()) mPaint.setColor(Color.RED);
            drawLine(canvas, mCurrentLine);
        }
    }
    private int getOffsetY() {
        int[] location = new int[2];
        getLocationInWindow(location);
        return location[1];
    }
    private void drawLine(Canvas canvas, Line line) {
        canvas.drawLine(line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(), mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        int x = (int)event.getX();
        int y = (int)event.getY();
        int action = MotionEventCompat.getActionMasked(event);
        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                startPaint(x, y);
                return true;
            case (MotionEvent.ACTION_MOVE) :
                moveEnd(x, y);
                return true;
            case (MotionEvent.ACTION_UP) :
                stopPaint(x, y);
                return true;
            case (MotionEvent.ACTION_CANCEL) :
            case (MotionEvent.ACTION_OUTSIDE) :
                return true;
            default :
                return super.onTouchEvent(event);
        }
    }
    private void startPaint(int x, int y) {
        mCurrentLine.setStart(x, y);
    }
    private void moveEnd(int x, int y) {
        mCurrentLine.setEnd(x, y);
        mActivity.onLineDrugging(mCurrentLine);
        invalidate();
    }
    private void stopPaint(int x, int y) {
        mCurrentLine.setEnd(x, y);
        if (mActivity.isValidLine(mCurrentLine)) {
            mActivity.onLineEnded(mCurrentLine);
            if(mActivity.isRedTurn()) {
                mBlueDefiniteLines.add(mCurrentLine.clone());
            } else {
                mRedDefiniteLines.add(mCurrentLine.clone());
            }
        } else {
            mActivity.clearSelecting();
        }
        mCurrentLine.clear();
        invalidate();
    }

}
