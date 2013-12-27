package robot.tnk47.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;
import robot.tnk47.raid.model.RaidBattleDamageMap;

public class RaidBattleEncountHandler extends Tnk47EventHandler {

    private static final Pattern RAID_BATTLE_ID_PATTERN = Pattern.compile("/raid/raid-battle\\?raidBattleId=([0-9]+_[0-9]+)");
    private static final Pattern BOSS_DATA_PATTERN = Pattern.compile("bossData = JSON.parse\\('(\\{.*\\})'\\);");

    private final RaidBattleDamageMap damageMap;

    public RaidBattleEncountHandler(final Tnk47Robot robot,
            final RaidBattleDamageMap damageMap) {
        super(robot);
        this.damageMap = damageMap;
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidBossId = (String) session.get("raidBossId");
        final String raidBossLevel = (String) session.get("raidBossLevel");
        final String raidId = (String) session.get("raidId");
        final String questId = (String) session.get("questId");
        final String areaId = (String) session.get("areaId");
        final String stageId = (String) session.get("stageId");
        final String token = (String) session.get("token");
        final String path = String.format("/raid/raid-boss-encount-animation?raidBossId=%s&raidBossLevel=%s&raidId=%s&questId=%s&areaId=%s&stageId=%s&token=%s",
                                          raidBossId,
                                          raidBossLevel,
                                          raidId,
                                          questId,
                                          areaId,
                                          stageId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        final String raidBattleId = this.findRaidBattleId(html);
        final JSONObject bossData = this.findBossData(html);
        if (StringUtils.isNotBlank(raidBattleId) && bossData != null) {
            this.printBossData(bossData);
            final int raidBossType = bossData.optInt("raidBossType");
            final int maxHp = bossData.optInt("maxHitPoint");
            final int minDamage = maxHp / 20;
            session.put("raidBattleId", raidBattleId);
            session.put("raidBossType", raidBossType);
            session.put("maxHp", maxHp);
            session.put("minDamage", minDamage);
            this.damageMap.setMinDamage(raidBattleId, minDamage);
            return "/raid/battle";
        }
        return "/raid/stage";
    }

    private String findRaidBattleId(final String html) {
        final Matcher matcher = RaidBattleEncountHandler.RAID_BATTLE_ID_PATTERN.matcher(html);
        if (matcher.find()) {
            final String raidBattleId = matcher.group(1);
            return raidBattleId;
        }
        return null;
    }

    private JSONObject findBossData(final String html) {
        final Matcher matcher = RaidBattleEncountHandler.BOSS_DATA_PATTERN.matcher(html);
        if (matcher.find()) {
            String text = matcher.group(1);
            text = StringEscapeUtils.unescapeJava(text);
            final JSONObject bossData = JSONObject.fromObject(text);
            return bossData;
        }
        return null;
    }

    private void printBossData(final JSONObject bossData) {
        final String name = bossData.optString("name");
        final int raidBossType = bossData.optInt("raidBossType");
        final int bossRank = bossData.optInt("bossRank");
        final int level = bossData.optInt("level");

        if (this.log.isInfoEnabled()) {
            switch (raidBossType) {
            case 2:
                this.log.info(String.format("【%s】 %s (Lv%d) %s",
                                            "大",
                                            name,
                                            level,
                                            StringUtils.repeat("★", bossRank)));
                break;
            case 1:
                this.log.info(String.format("【%s】 %s (Lv%d) %s",
                                            "疾风",
                                            name,
                                            level,
                                            StringUtils.repeat("★", bossRank)));
                break;
            default:
                this.log.info(String.format("%s (Lv%d) %s",
                                            name,
                                            level,
                                            StringUtils.repeat("★", bossRank)));
                break;
            }
        }
    }
}
