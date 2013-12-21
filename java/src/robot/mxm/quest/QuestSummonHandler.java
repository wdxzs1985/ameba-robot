package robot.mxm.quest;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class QuestSummonHandler extends MxmEventHandler {

    private static final Pattern QUEST_DATA_PATTERN = Pattern.compile("new mxm.Quest530\\((.*?)\\);");

    public QuestSummonHandler(final MxmRobot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String userId = (String) session.get("userId");
        final String summonId = (String) session.get("summonId");
        final String token = (String) session.get("token");

        final String path = String.format("/touch_summon/%s/%s/update",
                                          userId,
                                          summonId);
        final List<BasicNameValuePair> nvps = this.createNameValuePairs();
        nvps.add(new BasicNameValuePair("token", token));
        final String html = this.httpPost(path, nvps);
        final JSONObject data = this.resloveQuestData(html);
        if (data != null) {
            this.resolveJsonToken(data);
            if (data.optBoolean("bpRecovered", false)) {
                if (this.log.isInfoEnabled()) {
                    this.log.info("BP +1");
                }
            }

            if (data.optBoolean("noFatigue", false)) {
                if (this.log.isInfoEnabled()) {
                    this.log.info("体力不支");
                }
                session.put("isQuestEnable", false);
            }

            final JSONObject experienceParam = data.optJSONObject("experienceParam");
            if (experienceParam != null) {
                if (experienceParam.optBoolean("levelUp", false)) {
                    final int beforeLv = experienceParam.optInt("beforeLv");
                    final int afterLv = experienceParam.optInt("afterLv");
                    if (this.log.isInfoEnabled()) {
                        this.log.info(String.format("Level Up: %d > %d",
                                                    beforeLv,
                                                    afterLv));
                    }
                    if (experienceParam.optBoolean("reachMaxLevel", false)) {
                        if (this.log.isInfoEnabled()) {
                            this.log.info("Max Level");
                        }
                        return "/monster";
                    }
                }
            }

            final String redirectType = data.optString("redirectType");
            if (StringUtils.equals("RAID", redirectType)) {
                return "/raid/animation";
            } else if (StringUtils.equals("TOUCH_RESULT", redirectType)) {
                return "/quest/result";
            } else if (StringUtils.equals("RING_GET", redirectType)) {
                return "/quest/getRing";
            } else if (StringUtils.equals("STAGE_CLEAR", redirectType)) {
                return "/quest/stageClear";
            } else {
                this.log.debug(redirectType);
            }
        }
        return "/mypage";
    }

    private JSONObject resloveQuestData(final String html) {
        final Matcher matcher = QuestSummonHandler.QUEST_DATA_PATTERN.matcher(html);
        if (matcher.find()) {
            final String data = matcher.group(1);
            return JSONObject.fromObject(data);
        }
        return null;
    }
}
