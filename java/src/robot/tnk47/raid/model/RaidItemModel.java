package robot.tnk47.raid.model;

public class RaidItemModel {

    private int itemCount = 0;

    private int recovery = 0;

    private int useCount = 0;

    public int getItemCount() {
        return this.itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getRecovery() {
        return this.recovery;
    }

    public void setRecovery(int recovery) {
        this.recovery = recovery;
    }

    public int getUseCount() {
        return this.useCount;
    }

    public void setUseCount(int useCount) {
        this.useCount = useCount;
    }

}
