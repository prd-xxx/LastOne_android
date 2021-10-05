package xxx.prd.lastone.model;

public class Game {
    public static int ROW_NUM = 6;
    public static int MAX_COL_NUM = 6;
    private int[] mColNums;
    private boolean mIsRedTurn = true;
    private int mRemainPinNum = 0;
    private int[] mRemainPinBits;

    public Game(int[] colNums) {
        if(colNums.length != ROW_NUM) throw new IllegalArgumentException("colNums length must be " + ROW_NUM);
        mColNums = colNums;
        mRemainPinBits = new int[ROW_NUM];
        for(int i=0; i<ROW_NUM; i++) {
            if(colNums[i] > MAX_COL_NUM) throw new IllegalArgumentException("colNum[" + i + "] is over limit " + MAX_COL_NUM);
            mRemainPinNum += colNums[i];
            mRemainPinBits[i] = (1 << colNums[i]) - 1;
        }
    }
    public int getColNum(int rowIndex) {
        return mColNums[rowIndex];
    }
    public boolean isRedTurn() {
        return mIsRedTurn;
    }
    public int getRemainPinNum() {
        return mRemainPinNum;
    }
    public void flipTurnManually() {
        mIsRedTurn = !mIsRedTurn;
    }
    public boolean isAlive(int rowIndex, int colIndex) {
        if(!(0 <= rowIndex && rowIndex < ROW_NUM)) throw new IllegalArgumentException("invalid rowIndex: " + rowIndex);
        if(!(0 <= colIndex && colIndex < mColNums[rowIndex])) throw new IllegalArgumentException("invalid colIndex: " + colIndex);
        return ((mRemainPinBits[rowIndex] >> colIndex) & 1) == 1;
    }
    public void erasePinAt(int rowIndex, int colIndex) {
        if(!isAlive(rowIndex, colIndex)) throw new IllegalStateException("pin (" + rowIndex + ", " + colIndex +") has already erased");
        mRemainPinBits[rowIndex] ^= (1 << colIndex);
        mRemainPinNum--;
    }
    public boolean isValidOperation(Operation ope) {
        for (int c=ope.getFromCol(); c<=ope.getToCol(); c++) {
            if (!isAlive(ope.getRow(), c)) return false;
        }
        return true;
    }
    public void doOperation(Operation ope) {
        for (int c=ope.getFromCol(); c<=ope.getToCol(); c++) {
            erasePinAt(ope.getRow(), c);
        }
        mIsRedTurn = !mIsRedTurn;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" \n");
        sb.append("turn:").append(mIsRedTurn ? "Red" : "Blue").append('\n')
                .append("remain:").append(mRemainPinNum).append('\n');
        for(int i=0; i<ROW_NUM; i++) {
            for(int j=0; j<mColNums[i]; j++) {
                sb.append(((mRemainPinBits[i]>>j) & 1) == 1 ? '|' : '+');
            }
            sb.append('\n');
        }
        sb.append(Encoder.encodeToState(this).toString()).append('\n');
        return sb.toString();
    }
}
