package xxx.prd.lastone.model;

import static xxx.prd.lastone.model.Game.MAX_COL_NUM;
import static xxx.prd.lastone.model.Game.ROW_NUM;

public class Encoder {
    static GameState encodeToState(Game game) {
        int[] histogram = new int[MAX_COL_NUM];
        for(int i=0; i<ROW_NUM; i++) {
            int seq = 0;
            for(int j=0; j<game.getColNum(i); j++) {
                if(game.isAlive(i,j)) {
                    seq++;
                } else {
                    if(seq > 0) histogram[seq - 1]++;
                    seq = 0;
                }
            }
            if(seq > 0) histogram[seq - 1]++;
        }
        return new GameState(histogram);
    }
}
