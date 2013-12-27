package robot.tnk47.raid.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RaidBattleDamageMap {

    private final Map<String, RaidBattleDamageBean> map = Collections.synchronizedMap(new HashMap<String, RaidBattleDamageBean>());

    public boolean isRaiding(final String raidBattleId) {
        return this.map.containsKey(raidBattleId);
    }

    public boolean isDamageEnough(final String raidBattleId) {
        if (this.isRaiding(raidBattleId)) {
            final RaidBattleDamageBean damageBean = this.map.get(raidBattleId);
            return damageBean.isDamageEnough();
        }
        return false;
    }

    public void setMinDamage(final String raidBattleId, final long minDamage) {
        final RaidBattleDamageBean damageBean = this.getRaidBattleDamageBean(raidBattleId);
        damageBean.setMinDamage(minDamage);
    }

    public void addDamage(final String raidBattleId, final long damage) {
        final RaidBattleDamageBean damageBean = this.getRaidBattleDamageBean(raidBattleId);
        long totalDamage = damageBean.getDamage();
        totalDamage += damage;
        damageBean.setDamage(totalDamage);
    }

    public RaidBattleDamageBean getRaidBattleDamageBean(final String raidBattleId) {
        if (this.isRaiding(raidBattleId)) {
            return this.map.get(raidBattleId);
        } else {
            final RaidBattleDamageBean bean = new RaidBattleDamageBean();
            bean.setRaidBattleId(raidBattleId);
            bean.setDamage(0);
            bean.setMinDamage(0);
            this.map.put(raidBattleId, bean);
            return bean;
        }
    }

    public void remove(final String raidBattleId) {
        if (this.isRaiding(raidBattleId)) {
            this.map.remove(raidBattleId);
        }
    }
}
