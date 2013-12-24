package robot.tnk47.raid;

public class RaidDamageBean {

    private int firstDamage = 0;

    private int minDamage = 0;

    private int totalDamage = 0;

    private String raidBattleId = null;

    public int getFirstDamage() {
        return this.firstDamage;
    }

    public void setFirstDamage(int firstDamage) {
        this.firstDamage = firstDamage;
    }

    public int getMinDamage() {
        return this.minDamage;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

    public int getTotalDamage() {
        return this.totalDamage;
    }

    public void setTotalDamage(int totalDamage) {
        this.totalDamage = totalDamage;
    }

    public String getRaidBattleId() {
        return this.raidBattleId;
    }

    public void setRaidBattleId(String raidBattleId) {
        this.raidBattleId = raidBattleId;
    }
}
