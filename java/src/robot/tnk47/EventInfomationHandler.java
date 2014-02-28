package robot.tnk47;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringUtils;
import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;

public class EventInfomationHandler extends AbstractEventHandler<Tnk47Robot> {

    private static final Pattern MARATHON_PATTERN = Pattern.compile("/event/marathon/event-marathon\\?eventId=([0-9]+)");
    private static final String POINTRACE = "/event/pointrace";
    private static final String RAID = "/event/raid";

    public EventInfomationHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String path = "/mypage/ajax/get-mypage-info";
        final List<BasicNameValuePair> nvps = Collections.emptyList();
        final JSONObject mypageInfo = this.httpPostJSON(path, nvps);
        final JSONObject data = mypageInfo.optJSONObject("data");
        final JSONObject eventInfo = data.optJSONObject("eventInfo");
        final JSONObject currentEventInfoDto = eventInfo.optJSONObject("currentEventInfoDto");
        if (currentEventInfoDto != null) {
            if (this.log.isInfoEnabled()) {
                final int rank = currentEventInfoDto.optInt("rank");
                final int score = currentEventInfoDto.optInt("score");
                final String term = currentEventInfoDto.optString("term");
                final String mainText = currentEventInfoDto.optString("mainText");
                this.log.info("イベント中");
                this.log.info(term);
                this.log.info(mainText);
                this.log.info(String.format("获得总分: %d，排名: %d", score, rank));
            }

            final String linkUrl = currentEventInfoDto.optString("linkUrl");
            if (this.isPointRace(linkUrl)) {
                session.put("isBattleEnable", false);
                return "/pointrace";
            } else if (this.isMarathon(linkUrl)) {
                session.put("isQuestEnable", false);
                return "/marathon";
            } else if (this.isRaid(linkUrl)) {
                session.put("isQuestEnable", false);
                return "/raid";
            }
        }
        return "/mypage";
    }

    private boolean isPointRace(final String linkUrl) {
        return StringUtils.equals(linkUrl, EventInfomationHandler.POINTRACE);
    }

    private boolean isMarathon(final String linkUrl) {
        final Matcher matcher = EventInfomationHandler.MARATHON_PATTERN.matcher(linkUrl);
        if (matcher.find()) {
            final Map<String, Object> session = this.robot.getSession();
            final String eventId = matcher.group(1);
            session.put("eventId", eventId);
            return true;
        }
        return false;
    }

    private boolean isRaid(final String linkUrl) {
        return StringUtils.equals(linkUrl, EventInfomationHandler.RAID);
    }

}
