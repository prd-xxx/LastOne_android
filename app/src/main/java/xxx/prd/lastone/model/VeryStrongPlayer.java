package xxx.prd.lastone.model;

public class VeryStrongPlayer implements IComPlayer {
    @Override
    public Operation chooseOperation(Game game) {
        if(game.getRemainPinNum() <= 16) {
            return new DpPlayer().chooseOperation(game);
        } else {
            return new ParityWellPlayer().chooseOperation(game);
        }
    }
}
