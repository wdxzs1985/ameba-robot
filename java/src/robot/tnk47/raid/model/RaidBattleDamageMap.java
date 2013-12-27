package robot.tnk47.raid.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RaidBattleDamageMap {

    private final Map<String, RaidBattleDamageBean> map = Collections.synchronizedMap(new HashMap<String, RaidBattleDamageBean>());

    public boolean isRaiding(String raidBattleId) {
        return this.map.containsKey(raidBattleId);
    }

    public boolean isDamageEnough(String raidBattleId) {
        if (this.isRaiding(raidBattleId)) {
            RaidBattleDamageBean damageBean = this.map.get(raidBattleId);
            return damageBean.isDamageEnough();
        }
        return false;
    }

    public void setMinDamage(String raidBattleId, int minDamage) {
        RaidBattleDamageBean damageBean = this.getRaidBattleDamageBean(raidBattleId);
        damageBean.setMinDamage(minDamage);
    }

    public void addDamage(String raidBattleId, int damage) {
        RaidBattleDamageBean damageBean = this.getRaidBattleDamageBean(raidBattleId);
        int totalDamage = damageBean.getDamage();
        totalDamage += damage;
        damageBean.setDamage(totalDamage);
    }

    public RaidBattleDamageBean getRaidBattleDamageBean(String raidBattleId) {
        if (this.isRaiding(raidBattleId)) {
            return this.map.get(raidBattleId);
        } else {
            RaidBattleDamageBean bean = new RaidBattleDamageBean();
            bean.setRaidBattleId(raidBattleId);
            bean.setDamage(0);
            bean.setMinDamage(0);
            this.map.put(raidBattleId, bean);
            return bean;
        }
    }

    public void remove(String raidBattleId) {
        if (this.isRaiding(raidBattleId)) {
            this.map.remove(raidBattleId);
        }
    }
}
