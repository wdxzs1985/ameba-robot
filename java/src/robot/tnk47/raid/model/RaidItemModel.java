package robot.tnk47.raid.model;

public class RaidItemModel {

    private int itemCount = 0;

    private int regenValue = 0;

    private int useCount = 0;

    public int getItemCount() {
        return this.itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getUseCount() {
        return this.useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

    public int getRegenValue() {
        return this.regenValue;
    }

    public void setRegenValue(int regenValue) {
        this.regenValue = regenValue;
    }

}
