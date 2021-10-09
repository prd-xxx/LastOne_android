package xxx.prd.lastone.model;

import xxx.prd.lastone.R;

public enum Placement {
    PYRAMID("pyramid", R.string.placement_pyramid),
    FLOWER("flower", R.string.placement_flower),
    RANDOM("random", R.string.placement_random);
    public static final String PREF_KEY = "placement";
    private String mPrefValue;
    private int mNameId;
    Placement(String prefKey, int nameId) {
        mPrefValue = prefKey;
        mNameId = nameId;
    }
    public String getPrefValue() {
        return mPrefValue;
    }
    public int getNameId() {
        return mNameId;
    }
    public String getSuffixForStats() {
        return this==PYRAMID ? "" : "_" + this.name();
    }
    public static Placement fromPrefValue(String prefValue) {
        for (Placement placement: Placement.values()) {
            if (placement.mPrefValue.equals(prefValue)) return placement;
        }
        return PYRAMID; //default
    }
}
