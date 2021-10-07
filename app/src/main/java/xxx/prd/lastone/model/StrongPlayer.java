package xxx.prd.lastone.model;

public class StrongPlayer implements IComPlayer {
    @Override
    public Operation chooseOperation(Game game) {
        if(game.getRemainPinNum() <= 11) {
            return new DpPlayer().chooseOperation(game);
        } else {
            return new ParityWellPlayer().chooseOperation(game);
        }
    }
}
