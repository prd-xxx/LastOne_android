package xxx.prd.lastone.model;

import static xxx.prd.lastone.model.Game.MAX_COL_NUM;

import java.util.ArrayList;
import java.util.List;

public class GameState {
    private int[] mHistogram;
    GameState(int[] histogram) {
        if(histogram.length != MAX_COL_NUM) throw new IllegalArgumentException("histogram length must be " + MAX_COL_NUM);
        mHistogram = histogram;
    }

    public int getNum(int size) {
        return mHistogram[size - 1];
    }

    public int[] getHistogram() {
        return mHistogram;
    }

    public List<GameState> getNextAvailableStates() {
        List<GameState> ret = new ArrayList<>();
        for(int size=1; size<=MAX_COL_NUM; size++) {
            if(getNum(size) == 0) continue;
            for(int rem1=0; rem1<size; rem1++) {
                for(int rem2=0; rem2<=rem1; rem2++) {
                    if (rem1 + rem2 >= size) break;
                    int[] histogram = mHistogram.clone();
                    histogram[size-1]--;
                    if (rem1 > 0) histogram[rem1-1]++;
                    if (rem2 > 0) histogram[rem2-1]++;
                    ret.add(new GameState(histogram));
                }
            }
        }
        return ret;
    }

    @Override
    public int hashCode() {
        final int B = 20;
        int hash = 0;
        int m = 1;
        for (int i=0; i<MAX_COL_NUM; i++) {
            hash += m * mHistogram[i];
            m *= B;
        }
        return hash;
    }
    @Override
    public boolean equals(Object another) {
        if (!(another instanceof GameState)) return false;
        GameState a = (GameState) another;
        for (int i=0; i<MAX_COL_NUM; i++) {
            if (this.mHistogram[i] != a.mHistogram[i]) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (int h: mHistogram) {
            sb.append(h).append(i == MAX_COL_NUM-1 ? '\n' : ',');
            i++;
        }
        return sb.toString();
    }
}
