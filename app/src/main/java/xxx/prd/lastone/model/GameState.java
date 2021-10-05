package xxx.prd.lastone.model;

import static xxx.prd.lastone.model.Game.MAX_COL_NUM;

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
