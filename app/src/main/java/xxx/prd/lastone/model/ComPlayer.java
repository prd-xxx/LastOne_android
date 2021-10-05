package xxx.prd.lastone.model;


import xxx.prd.lastone.R;

public enum ComPlayer {
    WEAK("weak", ParityPoorPlayer.class, R.string.com_level_weak),
    NORMAL("normal", ParityWellPlayer.class, R.string.com_level_normal);

    private final String mPrefValue;
    private final Class<? extends IComPlayer> mComPlayerClass;
    private final int mNameId;
    public static final String PREF_KEY = "com_level";
    ComPlayer(String prefValue, Class<? extends IComPlayer> comPlayerClass, int nameId) {
        mPrefValue = prefValue;
        mComPlayerClass = comPlayerClass;
        mNameId = nameId;
    }
    public String getPrefValue() {
        return mPrefValue;
    }
    public Class<? extends IComPlayer> getComPlayerClass() {
        return mComPlayerClass;
    }
    public int getNameId() {
        return mNameId;
    }
    public static ComPlayer findByPrefValue(String prefValue) {
        for(ComPlayer comPlayer: ComPlayer.values()) {
            if(comPlayer.mPrefValue.equals(prefValue)) return comPlayer;
        }
        return defaultComPlayer();
    }
    public static ComPlayer defaultComPlayer() {
        return WEAK;
    }
}
