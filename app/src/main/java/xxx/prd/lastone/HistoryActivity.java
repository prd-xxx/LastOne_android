package xxx.prd.lastone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xxx.prd.lastone.model.ComPlayer;
import xxx.prd.lastone.view.PageIndicatorView;

public class HistoryActivity extends AppCompatActivity {
    private PageIndicatorView mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        final ViewPager2 viewPager = (ViewPager2) findViewById(R.id.hisotry_viewpager);
        viewPager.setAdapter(new PagerAdapter(this));
        viewPager.setCurrentItem(0);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                Log.d("page selected: ", ""+position);
                mIndicator.moveTo(position);
            }
        });

        mIndicator = (PageIndicatorView) findViewById(R.id.history_indicator);
        mIndicator.init(5, 0, pageIndex -> {
            viewPager.setCurrentItem(pageIndex);
        });
    }

    private class PagerAdapter extends RecyclerView.Adapter {

        private Context mContext;
        private TextView mComKindText;
        public PagerAdapter(Context context) {
            mContext = context;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.view_hisory_pager, parent, false);
            mComKindText = (TextView) view.findViewById(R.id.com_player_kind);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ComPlayer comPlayer = ComPlayer.values()[position];
            mComKindText.setText(comPlayer.getNameId());
        }

        @Override
        public int getItemCount() {
            return ComPlayer.values().length;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}