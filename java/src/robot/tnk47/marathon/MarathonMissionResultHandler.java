package robot.tnk47.marathon;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonMissionResultHandler extends Tnk47EventHandler {

    private static final Pattern GIVE_ITEM_RESULT_PATTERN = Pattern.compile("<span>(\\d+)</span>匹成功 <span>(\\d+)</span>匹失敗");
    private static final Pattern TOTAL_ITEM_PATTERN = Pattern.compile("<span class=\"totalItemCount\">(.*?)</span>");
    private static final Pattern TOTAL_POINT_PATTERN = Pattern.compile("<span class=\"contributionPointNum\">(.*?)</span>");

    private static final Pattern DISCOVERY_FLAG_PATTERN = Pattern.compile("'/event/marathon/marathon-notification\\?eventId=\\d+&missionId=\\d+&userMissionId=[\\d_]+&discoveryFlg=(.*?)'");
    private static final Pattern NOTIFICATION_USER_PATTERN = Pattern.compile("<li class=\"user selected\" data-user-id=\"(\\d+)\">");

    private final String notificationUser;

    public MarathonMissionResultHandler(final Tnk47Robot robot) {
        super(robot);
        this.notificationUser = robot.getNotificationUser();
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String eventId = (String) session.get("eventId");
        final String token = (String) session.get("token");
        final String path = String.format("/event/marathon/marathon-mission-result?eventId=%s&token=%s",
                                          eventId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
        Matcher matcher = null;
        if (this.log.isInfoEnabled()) {
            if ((matcher = MarathonMissionResultHandler.GIVE_ITEM_RESULT_PATTERN.matcher(html)).find()) {
                final String success = matcher.group(1);
                final String failed = matcher.group(2);
                this.log.info(String.format("%s匹成功 %s匹失敗", success, failed));
                this.printSuccessRatio(Integer.valueOf(success) > 0);
            }
            if ((matcher = MarathonMissionResultHandler.TOTAL_ITEM_PATTERN.matcher(html)).find()) {
                final String totalItemCount = matcher.group(1);
                this.log.info(totalItemCount);
            }
            if ((matcher = MarathonMissionResultHandler.TOTAL_POINT_PATTERN.matcher(html)).find()) {
                final String contributionPointNum = matcher.group(1);
                this.log.info(contributionPointNum);
            }
        }

        if ((matcher = MarathonMissionResultHandler.NOTIFICATION_USER_PATTERN.matcher(html)).find()) {
            String userId = this.notificationUser;
            if (StringUtils.isBlank(userId)) {
                userId = matcher.group(1);
            }
            session.put("userId", userId);
            if ((matcher = MarathonMissionResultHandler.DISCOVERY_FLAG_PATTERN.matcher(html)).find()) {
                final String discoveryFlg = matcher.group(1);
                session.put("discoveryFlg", discoveryFlg);
            }
            return "/marathon/notification";
        }
        return "/marathon";
    }

    private void printSuccessRatio(final boolean isSuccess) {
        final Map<String, Object> session = this.robot.getSession();
        final String missionKeyId = (String) session.get("missionKeyId");
        if (StringUtils.equals("1", missionKeyId)) {
            int suzumeCount = (Integer) (session.containsKey("suzumeCount") ? session.get("suzumeCount")
                                                                           : 0);
            int suzumeSuccess = (Integer) (session.containsKey("suzumeSuccess") ? session.get("suzumeSuccess")
                                                                               : 0);

            suzumeCount++;
            if (isSuccess) {
                suzumeSuccess++;
            }

            session.put("suzumeCount", suzumeCount);
            session.put("suzumeSuccess", suzumeSuccess);

            if (this.log.isInfoEnabled()) {
                this.log.info(String.format("遇到麻雀%d只，暴击%d只，暴击率%d%%",
                                            suzumeCount,
                                            suzumeSuccess,
                                            suzumeSuccess * 100 / suzumeCount));
            }
        }

    }
}
