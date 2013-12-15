package robot.tnk47.marathon;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonMissionHandler extends Tnk47EventHandler {

    private static final Pattern MISSION_PATTERN = Pattern.compile("href=\"/event/marathon/marathon-mission-animation\\?eventId=[0-9]+&userMissionId=[0-9_]+&missionId=([0-9]+)&missionKeyId=[0-9]+&token=[a-zA-Z0-9]{6}\"");

    private static final Pattern GIVE_ITEM_PATTERN = Pattern.compile("所持数:(\\d+)個");
    private static final Pattern GIVE_ITEM_TODAY_PATTERN = Pattern.compile("所持数（当日）:(\\d+)個");

    public MarathonMissionHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String userMissionId = (String) session.get("userMissionId");
        final String token = (String) session.get("token");
        final String path = String.format("/event/marathon/event-stage-detail?eventId=%s&userMissionId=%s&token=%s",
                                          eventId,
                                          userMissionId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        final Matcher missionMatcher = MarathonMissionHandler.MISSION_PATTERN.matcher(html);
        String missionId = null;
        String missionKeyId = "1";
        String useItemCount = "";
        if (missionMatcher.find()) {
            missionId = missionMatcher.group(1);
            if (StringUtils.equals(missionId, "3")) {
                int giveItemCount = 0;
                // int giveItemTodayCount = 0;
                Matcher matcher = null;
                if ((matcher = MarathonMissionHandler.GIVE_ITEM_TODAY_PATTERN.matcher(html)).find()) {
                    if (this.robot.isUseGiveItemToday()) {
                        giveItemCount += Integer.valueOf(matcher.group(1));
                    }
                }
                if ((matcher = MarathonMissionHandler.GIVE_ITEM_PATTERN.matcher(html)).find()) {
                    if (this.robot.isUseGiveItem()) {
                        giveItemCount += Integer.valueOf(matcher.group(1));
                    }
                }
                if (giveItemCount > 5) {
                    missionKeyId = "2";
                    useItemCount = "5";
                }
            }
            session.put("userMissionId", userMissionId);
            session.put("missionId", missionId);
            session.put("missionKeyId", missionKeyId);
            session.put("useItemCount", useItemCount);
            return "/marathon/mission/animation";
        }
        return "/mypage";
    }

}
