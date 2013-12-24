package robot.tnk47.raid;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class RaidStageForwardHandler extends Tnk47EventHandler {

    private static final Pattern BOSS_ENCOUNT_PATTERN = Pattern.compile("/raid/raid-boss-encount-animation\\?raidBossId=(\\d+)&raidBossLevel=(\\d+)&raidId=(\\d+)&questId=(\\d+)&areaId=(\\d+)&stageId=(\\d+)&token=([a-zA-Z0-9]{6})");

    public RaidStageForwardHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String raidId = (String) session.get("raidId");
        final String questId = (String) session.get("questId");
        final String areaId = (String) session.get("areaId");
        final String stageId = (String) session.get("stageId");
        final String token = (String) session.get("token");
        final String path = "/raid/ajax/put-raid-stage-forward";
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("raidId", raidId));
        nvps.add(new BasicNameValuePair("questId", questId));
        nvps.add(new BasicNameValuePair("areaId", areaId));
        nvps.add(new BasicNameValuePair("stageId", stageId));
        nvps.add(new BasicNameValuePair("token", token));
        final JSONObject jsonResponse = this.httpPostJSON(path, nvps);
        this.resolveJsonToken(jsonResponse);

        final JSONObject data = jsonResponse.optJSONObject("data");
        if (data != null) {
            final int needExpForNextLevel = data.optInt("needExpForNextLevel",
                                                        0);
            if (needExpForNextLevel > 0) {
                session.put("needExpForNextLevel", needExpForNextLevel);
                final JSONObject userData = data.optJSONObject("userData");
                final int stamina = userData.optInt("stamina");
                final int maxStamina = userData.optInt("maxStamina");
                if (this.log.isInfoEnabled()) {
                    this.log.info(String.format("体力[%d/%d]，还有[%d]经验升级。",
                                                stamina,
                                                maxStamina,
                                                needExpForNextLevel));
                }
            }

            final String areaEncountType = data.optString("areaEncountType");
            if (!StringUtils.equals(areaEncountType, "NONE")) {
                if (StringUtils.equals(areaEncountType, "ITEM")) {
                    if (this.log.isInfoEnabled()) {
                        final JSONObject encountCardData = data.optJSONObject("encountCardData");
                        final String name = encountCardData.optString("name");
                        this.log.info(String.format("隊士発見: %s", name));
                    }
                } else {
                    if (this.log.isInfoEnabled()) {
                        final String encountMessage = data.optString("encountMessage");
                        this.log.info(encountMessage);
                    }
                }
            }
            return "/raid/stage-forward";
        } else {
            return this.battleEncountAnimation(jsonResponse);
        }
    }

    private String battleEncountAnimation(final JSONObject jsonResponse) {
        final Map<String, Object> session = this.robot.getSession();
        final String url = jsonResponse.optString("url");
        final Matcher matcher = RaidStageForwardHandler.BOSS_ENCOUNT_PATTERN.matcher(url);
        if (matcher.find()) {
            final String raidBossId = matcher.group(1);
            final String raidBossLevel = matcher.group(2);
            final String raidId = matcher.group(3);
            final String questId = matcher.group(4);
            final String areaId = matcher.group(5);
            final String stageId = matcher.group(6);
            final String token = matcher.group(7);

            session.put("raidBossId", raidBossId);
            session.put("raidBossLevel", raidBossLevel);
            session.put("raidId", raidId);
            session.put("questId", questId);
            session.put("areaId", areaId);
            session.put("stageId", stageId);
            session.put("token", token);
            return "/raid/boss-encount";
        }
        return "/mypage";
    }
}
