package robot.tnk47.raid.model;

public class RaidBattleModel {

    private boolean mine = false;
    private boolean raid = false;
    private boolean raidResult = false;
    private boolean helpEnable = false;
    private boolean feverEnable = false;

    private int raidBossType = 0;
    private int maxHp = 0;
    private int bossHpPercent = 0;
    private int deckAttack = 0;
    private int feverRate = 0;

    private String deckId = null;
    private int apCost = 0;
    private int maxAp = 0;
    private int apNow = 0;

    private final RaidItemModel apSmall = new RaidItemModel();
    private final RaidItemModel apFull = new RaidItemModel();
    private final RaidItemModel powerHalf = new RaidItemModel();
    private final RaidItemModel powerFull = new RaidItemModel();
    private final RaidItemModel specialAttack = new RaidItemModel();

    private boolean canAttack = false;
    private boolean isFullPower = false;
    private boolean isSpecialAttack = false;

    public int getCurrentHp() {
        return this.getMaxHp() * this.getBossHpPercent() / 100;
    }

    public int getAttackPoint() {
        return this.getDeckAttack() * (this.getFeverRate() + 100) / 100;
    }

    public boolean isFirstEntry() {
        return this.apCost == 0;
    }

    public boolean isLimitedBoss() {
        return this.raidBossType == 2;
    }

    public boolean hasAp() {
        return this.getApNow() >= this.getApCost();
    }

    public boolean isApFull() {
        return this.getApNow() == this.getMaxAp();
    }

    public boolean hasSpecialAttack() {
        return this.getSpecialAttack().getItemCount() > 0;
    }

    public boolean isBossHpMoreThanSpecialAttack() {
        return this.getCurrentHp() > this.getAttackPoint() * 15;
    }

    public boolean isBossHpMoreThanFullAttack() {
        return this.getCurrentHp() > this.getAttackPoint() * 5;
    }

    public int getDeckAttack() {
        return this.deckAttack;
    }

    public void setDeckAttack(int deckAttack) {
        this.deckAttack = deckAttack;
    }

    public int getFeverRate() {
        return this.feverRate;
    }

    public void setFeverRate(int feverRate) {
        this.feverRate = feverRate;
    }

    public boolean isMine() {
        return this.mine;
    }

    public void setMine(boolean mine) {
        this.mine = mine;
    }

    public int getMaxHp() {
        return this.maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public int getBossHpPercent() {
        return this.bossHpPercent;
    }

    public void setBossHpPercent(int bossHpPercent) {
        this.bossHpPercent = bossHpPercent;
    }

    public String getDeckId() {
        return this.deckId;
    }

    public void setDeckId(String deckId) {
        this.deckId = deckId;
    }

    public int getApCost() {
        return this.apCost;
    }

    public void setApCost(int apCost) {
        this.apCost = apCost;
    }

    public int getMaxAp() {
        return this.maxAp;
    }

    public void setMaxAp(int maxAp) {
        this.maxAp = maxAp;
    }

    public int getApNow() {
        return this.apNow;
    }

    public void setApNow(int apNow) {
        this.apNow = apNow;
    }

    public RaidItemModel getApSmall() {
        return this.apSmall;
    }

    public RaidItemModel getApFull() {
        return this.apFull;
    }

    public RaidItemModel getPowerHalf() {
        return this.powerHalf;
    }

    public RaidItemModel getPowerFull() {
        return this.powerFull;
    }

    public boolean isRaid() {
        return this.raid;
    }

    public void setRaid(boolean raid) {
        this.raid = raid;
    }

    public boolean isRaidResult() {
        return this.raidResult;
    }

    public void setRaidResult(boolean raidResult) {
        this.raidResult = raidResult;
    }

    public boolean isHelpEnable() {
        return this.helpEnable;
    }

    public void setHelpEnable(boolean helpEnable) {
        this.helpEnable = helpEnable;
    }

    public boolean isFeverEnable() {
        return this.feverEnable;
    }

    public void setFeverEnable(boolean feverEnable) {
        this.feverEnable = feverEnable;
    }

    public boolean isFullPower() {
        return this.isFullPower;
    }

    public void setFullPower(boolean isFullPower) {
        this.isFullPower = isFullPower;
    }

    public boolean isSpecialAttack() {
        return this.isSpecialAttack;
    }

    public void setSpecialAttack(boolean isSpecialAttack) {
        this.isSpecialAttack = isSpecialAttack;
    }

    public boolean isCanAttack() {
        return this.canAttack;
    }

    public void setCanAttack(boolean canAttack) {
        this.canAttack = canAttack;
    }

    public int getRaidBossType() {
        return this.raidBossType;
    }

    public void setRaidBossType(int raidBossType) {
        this.raidBossType = raidBossType;
    }

    public RaidItemModel getSpecialAttack() {
        return this.specialAttack;
    }
}
