package robot.tnk47.marathon;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class MarathonMissionHandler extends Tnk47EventHandler {

    private static final Pattern MISSION_PATTERN = Pattern.compile("/event/marathon/marathon-mission-animation\\?eventId=[0-9]+&userMissionId=[0-9_]+&missionId=([0-9]+)&missionKeyId=[0-9]+&token=[a-zA-Z0-9]{6}");
    private static final Pattern BOSS_PATTERN = Pattern.compile("bossUrl: '/event/marathon/event-boss-animation'");

    private static final Pattern GIVE_ITEM_PATTERN = Pattern.compile("所持数:(\\d+)個");
    private static final Pattern GIVE_ITEM_TODAY_PATTERN = Pattern.compile("所持数（当日）:(\\d+)個");

    private final boolean useGiveItemToday;
    private final boolean useGiveItem;
    private final boolean onlyGiveOne;

    public MarathonMissionHandler(final Tnk47Robot robot) {
        super(robot);
        this.useGiveItemToday = true;
        this.useGiveItem = robot.isUseGiveItem();
        this.onlyGiveOne = robot.isOnlyGiveOne();

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

        final Matcher bossMatcher = MarathonMissionHandler.BOSS_PATTERN.matcher(html);
        if (bossMatcher.find()) {
            if (this.log.isInfoEnabled()) {
                this.log.info("BOSS出现");
            }
            return "/marathon/stage/boss";
        }

        final Matcher missionMatcher = MarathonMissionHandler.MISSION_PATTERN.matcher(html);
        String missionId = null;
        String missionKeyId = "1";
        String useItemCount = "";
        if (missionMatcher.find()) {
            missionId = missionMatcher.group(1);
            if (StringUtils.equals(missionId, "2")) {
                int giveItemCount = 0;
                Matcher matcher = null;
                if (this.useGiveItemToday) {
                    if ((matcher = MarathonMissionHandler.GIVE_ITEM_TODAY_PATTERN.matcher(html)).find()) {
                        giveItemCount += Integer.valueOf(matcher.group(1));
                    }
                }
                if (this.useGiveItem) {
                    if ((matcher = MarathonMissionHandler.GIVE_ITEM_PATTERN.matcher(html)).find()) {
                        giveItemCount += Integer.valueOf(matcher.group(1));
                    }
                }
                if (this.onlyGiveOne) {
                    if (giveItemCount > 1) {
                        missionKeyId = "2";
                        useItemCount = "1";
                    }
                }
            } else if (StringUtils.equals(missionId, "3")) {
                int giveItemCount = 0;
                Matcher matcher = null;
                if (this.useGiveItemToday) {
                    if ((matcher = MarathonMissionHandler.GIVE_ITEM_TODAY_PATTERN.matcher(html)).find()) {
                        giveItemCount += Integer.valueOf(matcher.group(1));
                    }
                }
                if (this.useGiveItem) {
                    if ((matcher = MarathonMissionHandler.GIVE_ITEM_PATTERN.matcher(html)).find()) {
                        giveItemCount += Integer.valueOf(matcher.group(1));
                    }
                }
                if (this.onlyGiveOne) {
                    if (giveItemCount > 1) {
                        missionKeyId = "2";
                        useItemCount = "1";
                    }
                } else {
                    if (giveItemCount > 5) {
                        missionKeyId = "2";
                        useItemCount = "5";
                    }
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
