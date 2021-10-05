package xxx.prd.lastone.model;

import static xxx.prd.lastone.model.Game.MAX_COL_NUM;

/**
 * サイズ2以上の塊がちょうど1個のとき、わざと負ける
 * それ以外は RandomPlayer
 */
public class ParityPoorPlayer implements IComPlayer {

    @Override
    public Operation chooseOperation(Game game) {
        GameState state = Encoder.encodeToState(game);
        int sum = 0;
        int whichSize = -1;
        for (int size=2; size<=MAX_COL_NUM; size++) {
            int n = state.getNum(size);
            if (n > 0) {
                sum += n;
                whichSize = size;
            }
        }
        if (sum != 1) return new RandomPlayer().chooseOperation(game);
        int[] histogram = state.getHistogram();
        histogram[whichSize - 1]--;
        if(histogram[0] % 2 == 1) histogram[0]++;
        return Decoder.decodeToOperation(game, new GameState(histogram));
    }
}
