package xxx.prd.lastone.model;

public class Operation {
    private int mRow;
    private int mFromCol;
    private int mToCol;
    public Operation(int row, int fromCol, int toCol) {
        mRow = row;
        mFromCol = fromCol;
        mToCol = toCol;
    }
    public int getRow() {
        return mRow;
    }
    public int getFromCol() {
        return mFromCol;
    }
    public int getToCol() {
        return mToCol;
    }
}
