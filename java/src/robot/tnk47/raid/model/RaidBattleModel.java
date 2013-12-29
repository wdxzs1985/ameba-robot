package robot.tnk47.raid.model;

public class RaidBattleModel {

    public static final int NORMAL_BOSS = 0;
    public static final int SPEED_BOSS = 1;
    public static final int LIMIT_BOSS = 2;
    public static final int EMPTY = 0;
    public static final int NO_COST = 0;
    public static final int HUNDRED = 100;
    public static final int FULL_ATTACK = 5;
    public static final int SPECIAL_ATTACK = 12;

    // private boolean mine = false;
    private boolean raid = false;
    private boolean raidResult = false;
    private boolean helpEnable = false;
    private boolean feverEnable = false;

    private int raidBossType = 0;
    private long maxHp = 0;
    private long minDamage = 0;
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

    public boolean isHpFull() {
        return this.getBossHpPercent() == RaidBattleModel.HUNDRED;
    }

    public long getCurrentHp() {
        return this.getMaxHp() * this.getBossHpPercent()
               / RaidBattleModel.HUNDRED;
    }

    public long getAttackPoint() {
        return this.getDeckAttack() * (this.getFeverRate() + RaidBattleModel.HUNDRED)
               / RaidBattleModel.HUNDRED;
    }

    public boolean canSpecialAttack() {
        final long totalAttack = this.getAttackPoint() * RaidBattleModel.SPECIAL_ATTACK;
        final boolean isCurrentHpEnough = this.getCurrentHp() > totalAttack;
        final boolean isMinDamageEnough = this.getMinDamage() > totalAttack;
        return isCurrentHpEnough && isMinDamageEnough;
    }

    public boolean canFullAttack() {
        final long totalAttack = this.getAttackPoint() * RaidBattleModel.FULL_ATTACK;
        final boolean isCurrentHpEnough = this.getCurrentHp() > totalAttack;
        final boolean isMinDamageEnough = this.getMinDamage() > totalAttack;
        return isCurrentHpEnough && isMinDamageEnough;
    }

    public boolean isNoCost() {
        return this.apCost == RaidBattleModel.NO_COST;
    }

    public boolean isLimitedBoss() {
        return this.raidBossType == RaidBattleModel.LIMIT_BOSS;
    }

    public boolean isSpeedBoss() {
        return this.raidBossType == RaidBattleModel.SPEED_BOSS;
    }

    public boolean isApEnough() {
        return this.getApNow() >= this.getApCost();
    }

    public boolean isApFull() {
        return this.getApNow() == this.getMaxAp();
    }

    public boolean hasSpecialAttack() {
        return this.getSpecialAttack().getItemCount() > RaidBattleModel.EMPTY;
    }

    public long getDeckAttack() {
        return this.deckAttack;
    }

    public void setDeckAttack(final int deckAttack) {
        this.deckAttack = deckAttack;
    }

    public int getFeverRate() {
        return this.feverRate;
    }

    public void setFeverRate(final int feverRate) {
        this.feverRate = feverRate;
    }

    public long getMaxHp() {
        return this.maxHp;
    }

    public void setMaxHp(final long maxHp) {
        this.maxHp = maxHp;
    }

    public int getBossHpPercent() {
        return this.bossHpPercent;
    }

    public void setBossHpPercent(final int bossHpPercent) {
        this.bossHpPercent = bossHpPercent;
    }

    public String getDeckId() {
        return this.deckId;
    }

    public void setDeckId(final String deckId) {
        this.deckId = deckId;
    }

    public int getApCost() {
        return this.apCost;
    }

    public void setApCost(final int apCost) {
        this.apCost = apCost;
    }

    public int getMaxAp() {
        return this.maxAp;
    }

    public void setMaxAp(final int maxAp) {
        this.maxAp = maxAp;
    }

    public int getApNow() {
        return this.apNow;
    }

    public void setApNow(final int apNow) {
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

    public void setRaid(final boolean raid) {
        this.raid = raid;
    }

    public boolean isRaidResult() {
        return this.raidResult;
    }

    public void setRaidResult(final boolean raidResult) {
        this.raidResult = raidResult;
    }

    public boolean isHelpEnable() {
        return this.helpEnable;
    }

    public void setHelpEnable(final boolean helpEnable) {
        this.helpEnable = helpEnable;
    }

    public boolean isFeverEnable() {
        return this.feverEnable;
    }

    public void setFeverEnable(final boolean feverEnable) {
        this.feverEnable = feverEnable;
    }

    public boolean isFullPower() {
        return this.isFullPower;
    }

    public void setFullPower(final boolean isFullPower) {
        this.isFullPower = isFullPower;
    }

    public boolean isSpecialAttack() {
        return this.isSpecialAttack;
    }

    public void setSpecialAttack(final boolean isSpecialAttack) {
        this.isSpecialAttack = isSpecialAttack;
    }

    public boolean isCanAttack() {
        return this.canAttack;
    }

    public void setCanAttack(final boolean canAttack) {
        this.canAttack = canAttack;
    }

    public int getRaidBossType() {
        return this.raidBossType;
    }

    public void setRaidBossType(final int raidBossType) {
        this.raidBossType = raidBossType;
    }

    public RaidItemModel getSpecialAttack() {
        return this.specialAttack;
    }

    public long getMinDamage() {
        return this.minDamage;
    }

    public void setMinDamage(final long minDamage) {
        this.minDamage = minDamage;
    }

}
