package robot.tnk47.raid.model;

public class RaidModel {

    public static final int EMPTY = 0;

    private String raidId = null;

    private int apNow = 0;

    public String getRaidId() {
        return this.raidId;
    }

    public void setRaidId(String raidId) {
        this.raidId = raidId;
    }

    public int getApNow() {
        return this.apNow;
    }

    public void setApNow(int apNow) {
        this.apNow = apNow;
    }

    public boolean hasAp() {
        return this.apNow > EMPTY;
    }
}
