package xxx.prd.lastone.model;

public class Operation {
    private final int mRow;
    private final int mFromCol;
    private final int mToCol;
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

    @Override
    public boolean equals(Object another) {
        if (!(another instanceof Operation)) return false;
        Operation a = (Operation) another;
        return this.mRow==a.mRow && this.mFromCol==a.mFromCol && this.mToCol==a.mToCol;
    }
}
