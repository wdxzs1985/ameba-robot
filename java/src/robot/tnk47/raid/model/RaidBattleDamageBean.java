package robot.tnk47.raid.model;

public class RaidBattleDamageBean {

    private String raidBattleId = null;

    private int damage = 0;

    private int minDamage = 0;

    public boolean isDamageEnough() {
        return this.damage >= this.minDamage;
    }

    public String getRaidBattleId() {
        return this.raidBattleId;
    }

    public void setRaidBattleId(String raidBattleId) {
        this.raidBattleId = raidBattleId;
    }

    public int getDamage() {
        return this.damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getMinDamage() {
        return this.minDamage;
    }

    public void setMinDamage(int minDamage) {
        this.minDamage = minDamage;
    }

}
