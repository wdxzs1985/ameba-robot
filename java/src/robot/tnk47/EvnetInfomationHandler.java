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

public class EvnetInfomationHandler extends AbstractEventHandler<Tnk47Robot> {

    private static final Pattern MARATHON_PATTERN = Pattern.compile("/event/marathon/event-marathon\\?eventId=([0-9]+)");
    private static final Pattern POINTRACE_PATTERN = Pattern.compile("/event/pointrace");

    public EvnetInfomationHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String input = this.robot.buildPath("/event/ajax/get-current-event-information");
        final List<BasicNameValuePair> nvps = Collections.emptyList();
        final String html = this.robot.getHttpClient().postForHtml(input, nvps);
        final JSONObject currentEventInfomation = JSONObject.fromObject(html);
        final JSONObject data = currentEventInfomation.getJSONObject("data");

        if (!StringUtils.equals(data.getString("currentEventInfoDto"), "null")) {
            final JSONObject currentEventInfoDto = data.getJSONObject("currentEventInfoDto");
            if (this.log.isInfoEnabled()) {
                final int rank = currentEventInfoDto.getInt("rank");
                final int score = currentEventInfoDto.getInt("score");
                final String term = currentEventInfoDto.getString("term");
                final String mainText = currentEventInfoDto.getString("mainText");
                this.log.info("イベント中");
                this.log.info(term);
                this.log.info(mainText);
                this.log.info(String.format("获得总分: %d，排名: %d", score, rank));
            }

            final String linkUrl = currentEventInfoDto.getString("linkUrl");
            Matcher matcher = null;
            if ((matcher = EvnetInfomationHandler.POINTRACE_PATTERN.matcher(linkUrl)).find()) {
                session.put("isBattleEnable", false);
                return "/pointrace";
            }
            if ((matcher = EvnetInfomationHandler.MARATHON_PATTERN.matcher(linkUrl)).find()) {
                final String eventId = matcher.group(1);
                session.put("eventId", eventId);
                session.put("isQuestEnable", false);
                return "/marathon";
            }
        }
        return "/mypage";
    }

}
