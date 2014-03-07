package robot.tnk47.raid.model;

public class RaidBattleDamageBean {

    private String raidBattleId = null;

    private long damage = 0;

    private long minDamage = 0;

    public boolean isDamageEnough() {
        return this.damage >= this.minDamage;
    }

    public String getRaidBattleId() {
        return this.raidBattleId;
    }

    public void setRaidBattleId(final String raidBattleId) {
        this.raidBattleId = raidBattleId;
    }

    public long getDamage() {
        return this.damage;
    }

    public void setDamage(final long damage) {
        this.damage = damage;
    }

    public long getMinDamage() {
        return this.minDamage;
    }

    public void setMinDamage(final long minDamage) {
        this.minDamage = minDamage;
    }

}
