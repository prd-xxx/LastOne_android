package xxx.prd.lastone.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * このゲームは true/false の dp で解析できる (true:必勝, false:必敗)
 * true ならば、必敗局面の1つを選択する
 * false ならば、RandomMinimumPlayer を使う
 */
public class DpPlayer implements IComPlayer {
    private Map<GameState, Boolean> mMap = new HashMap<>();

    @Override
    public Operation chooseOperation(Game game) {
        GameState state = Encoder.encodeToState(game);
        boolean isWin = calcIsWinState(state);
        Log.d("dp", "state " + (isWin ? "win" : "lose") );
        if(isWin) {
            List<GameState> candidates = new ArrayList<>();
            for (GameState nextState: state.getNextAvailableStates()) {
                if(!calcIsWinState(nextState)) {
                    candidates.add(nextState);
                }
            }
            int i = new Random().nextInt(candidates.size());
            return Decoder.decodeToOperation(game, candidates.get(i));
        } else {
            return new RandomMinimumPlayer().chooseOperation(game);
        }
    }

    private boolean calcIsWinState(GameState state) {
        if (mMap.containsKey(state)) return mMap.get(state);
        List<GameState> nextStates = state.getNextAvailableStates();
        if(nextStates.isEmpty()) return true;
        boolean ret = false;
        for (GameState nextState: nextStates) {
            if (!calcIsWinState(nextState)) {
                ret = true;
                break;
            }
        }
        mMap.put(state, ret);
        return ret;
    }
}
