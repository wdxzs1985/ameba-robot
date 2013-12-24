package robot.tnk47.raid;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class RaidDamageMap {

    private Map<String, RaidDamageBean> map = new HashMap<String, RaidDamageBean>();

    public void refresh(JSONArray raidDtos) {
        Map<String, RaidDamageBean> newRaidDamageMap = new HashMap<String, RaidDamageBean>();
        for (int i = 0; i < raidDtos.size(); i++) {
            JSONObject raidDto = raidDtos.optJSONObject(i);
            String raidBattleId = raidDto.optString("raidBattleId");
            JSONObject bossDto = raidDto.optJSONObject("raidBossDto");
            int battleServiceMinPoint = bossDto.optInt("battleServiceMinPoint");
            RaidDamageBean bean = new RaidDamageBean();
            bean.setRaidBattleId(raidBattleId);
            bean.setFirstDamage(0);
            bean.setMinDamage(battleServiceMinPoint);
            bean.setTotalDamage(0);
            newRaidDamageMap.put(raidBattleId, bean);
        }

        for (Entry<String, RaidDamageBean> entry : this.map.entrySet()) {
            String raidBattleId = entry.getKey();
            RaidDamageBean bean = entry.getValue();
            if (newRaidDamageMap.containsKey(raidBattleId)) {
                newRaidDamageMap.put(raidBattleId, bean);
            }
        }
        this.map = newRaidDamageMap;
    }

    public RaidDamageBean get(String raidBattleId) {
        return this.map.get(raidBattleId);
    }
}
