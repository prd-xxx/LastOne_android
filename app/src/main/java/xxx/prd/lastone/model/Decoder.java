package xxx.prd.lastone.model;

import static xxx.prd.lastone.model.Game.MAX_COL_NUM;
import static xxx.prd.lastone.model.Game.ROW_NUM;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Decoder {
    static Operation decodeToOperation(Game game, GameState afterState) {
        GameState beforeState = Encoder.encodeToState(game);
        int targetLen = -1;
        for (int len=MAX_COL_NUM; len>=1; len--) {
            int bn = beforeState.getNum(len);
            int an = afterState.getNum(len);
            if(bn < an)
                throwIllegalArgumentException(beforeState, afterState);
            if(bn > an) {
                targetLen = len;
                break;
            }
        }
        if(targetLen == -1) throwIllegalArgumentException(beforeState, afterState);
        int remainLen1, remainLen2;
        remainLen1 = remainLen2 = 0;
        for (int len=1; len<targetLen; len++) {
            int bn = beforeState.getNum(len);
            int an = afterState.getNum(len);
            if(bn > an) throwIllegalArgumentException(beforeState, afterState);
            if(bn < an) {
                if (an - bn >= 3) throwIllegalArgumentException(beforeState, afterState);
                if (an - bn == 2) {
                    if (remainLen1 != 0)  throwIllegalArgumentException(beforeState, afterState);
                    remainLen1 = remainLen2 = len;
                } else { // an - bn == 1
                    if (remainLen1 == 0) remainLen1 = len;
                    else remainLen2 = len;
                }
                if (remainLen2 != 0) break;
            }
        }

        Set<Operation> candidateSet = new HashSet<>();
        for (int i=0; i<ROW_NUM; i++) {
            if (game.getColNum(i) < targetLen) continue;
            int seq = 0;
            for(int j=0; j<game.getColNum(i); j++) {
                if(game.isAlive(i,j)) {
                    seq++;
                } else {
                    if(seq == targetLen) {
                        int l = j - targetLen;
                        int r = j - 1;
                        candidateSet.add(new Operation(i, l + remainLen1, r - remainLen2));
                        candidateSet.add(new Operation(i, l + remainLen2, r - remainLen1));
                    }
                    seq = 0;
                }
            }
            if(seq == targetLen) {
                int l = game.getColNum(i) - targetLen;
                int r = game.getColNum(i) - 1;
                candidateSet.add(new Operation(i, l + remainLen1, r - remainLen2));
                candidateSet.add(new Operation(i, l + remainLen2, r - remainLen1));
            }
        }
        if(candidateSet.size() == 0) throwIllegalArgumentException(beforeState, afterState);
        List<Operation> candidateList = new ArrayList<>(candidateSet);
        int i = new Random().nextInt(candidateList.size());
        return candidateList.get(i);
    }

    private static void throwIllegalArgumentException(GameState before, GameState after) {
        throw new IllegalArgumentException("before:" + before.toString() + ", after:" + after.toString());
    }
}
