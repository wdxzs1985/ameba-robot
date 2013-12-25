package robot.tnk47.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class RaidBattleEncountHandler extends Tnk47EventHandler {

    private static final Pattern RAID_BATTLE_ID_PATTERN = Pattern.compile("/raid/raid-battle\\?raidBattleId=([0-9]+_[0-9]+)");
    private static final Pattern BOSS_DATA_PATTERN = Pattern.compile("bossData = JSON.parse\\('(\\{.*\\})'\\);");

    public RaidBattleEncountHandler(final Tnk47Robot robot) {
        super(robot);
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

        this.findRaidBattleId(html);
        this.findBossData(html);
        return "/raid/battle";
    }

    private void findRaidBattleId(String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = RaidBattleEncountHandler.RAID_BATTLE_ID_PATTERN.matcher(html);
        if (matcher.find()) {
            final String raidBattleId = matcher.group(1);
            session.put("raidBattleId", raidBattleId);
            session.put("invite", false);
        }
    }

    private void findBossData(final String html) {
        final Matcher matcher = RaidBattleEncountHandler.BOSS_DATA_PATTERN.matcher(html);
        if (matcher.find()) {
            String text = matcher.group(1);
            text = StringEscapeUtils.unescapeJava(text);
            final JSONObject bossData = JSONObject.fromObject(text);
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
                                                StringUtils.repeat("★",
                                                                   bossRank)));
                    break;
                case 1:
                    this.log.info(String.format("【%s】 %s (Lv%d) %s",
                                                "疾风",
                                                name,
                                                level,
                                                StringUtils.repeat("★",
                                                                   bossRank)));
                    break;
                default:
                    this.log.info(String.format("%s (Lv%d) %s",
                                                name,
                                                level,
                                                StringUtils.repeat("★",
                                                                   bossRank)));
                    break;
                }
            }
        }
    }
}
