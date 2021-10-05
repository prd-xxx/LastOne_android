package xxx.prd.lastone.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * このゲームは 0/1 の grundy数 (というかdp)で表せる (1:必勝, 0:必敗)
 * grundy数が 1ならば、必敗局面の1つを選択する
 * grundy数が 0ならば、RandomMinimumPlayer を使う
 */
public class GrundyPlayer implements IComPlayer {
    private Map<GameState, Integer> mMap = new HashMap<>();

    @Override
    public Operation chooseOperation(Game game) {
        GameState state = Encoder.encodeToState(game);
        int grundy = calcGrundy(state);
        Log.d("grundy", "grundy = " + grundy);
        if(grundy == 1) {
            List<GameState> candidates = new ArrayList<>();
            for (GameState nextState: state.getNextAvailableStates()) {
                if(calcGrundy(nextState) == 0) {
                    candidates.add(nextState);
                }
            }
            int i = new Random().nextInt(candidates.size());
            return Decoder.decodeToOperation(game, candidates.get(i));
        } else {
            return new RandomPlayer().chooseOperation(game);
        }
    }

    private int calcGrundy(GameState state) {
        if (mMap.containsKey(state)) return mMap.get(state);
        List<GameState> nextStates = state.getNextAvailableStates();
        if(nextStates.isEmpty()) return 1;
        int ret = 0;
        for (GameState nextState: nextStates) {
            if (calcGrundy(nextState) == 0) {
                ret = 1;
                break;
            }
        }
        mMap.put(state, ret);
        return ret;
    }
}
