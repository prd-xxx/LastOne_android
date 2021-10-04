package xxx.prd.lastone.model;

import static xxx.prd.lastone.model.Game.ROW_NUM;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomPlayer implements IComPlayer {
    @Override
    public Operation chooseOperation(Game game) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Operation> opes = getValidOperations(game);
        int i = new Random().nextInt(opes.size());
        return opes.get(i);
    }

    private List<Operation> getValidOperations(Game game) {
        ArrayList<Operation> opes = new ArrayList<>();
        for(int row=0; row<ROW_NUM; row++) {
            for(int from=0; from<game.getColNum(row); from++) {
                for(int to=from; to<game.getColNum(row); to++) {
                    Operation ope = new Operation(row, from, to);
                    if(game.isValidOperation(ope)) {
                        opes.add(ope);
                    }
                }
            }
        }
        return opes;
    }
}
