package xxx.prd.lastone.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import xxx.prd.lastone.R;

public class PageIndicatorView extends FrameLayout {

    final RadioGroup mIndicator;
    final Context mContext;
    Listener mListener;

    public PageIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_page_indicator, this);
        mIndicator = (RadioGroup) findViewById(R.id.indicator_dots);
    }

    public void init(int pageNum, int initPage, Listener listener) {
        mListener = listener;
        for (int i = 0; i < pageNum; i++) {
            addNewDot(mIndicator, i);
        }
        mIndicator.check(initPage);
        mIndicator.setOnCheckedChangeListener((radioGroup, i) -> mListener.onSelect(i));
    }

    public void moveTo(int pageIndex){
        mIndicator.check(pageIndex);
    }

    private void addNewDot(RadioGroup indicator, int index) {
        RadioButton dot = new RadioButton(mContext);
        dot.setPadding(2, 0, 2, 0);
        dot.setButtonDrawable(R.drawable.page_indicator_dot);
        dot.setId(index);
        indicator.addView(dot);
    }

    public interface Listener {
        void onSelect(int pageIndex);
    }
}