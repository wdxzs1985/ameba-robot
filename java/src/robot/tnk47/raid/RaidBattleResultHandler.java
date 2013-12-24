package robot.tnk47.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class RaidBattleResultHandler extends Tnk47EventHandler {

    private static final Pattern BOSS_PATTERN = Pattern.compile("<span class=\"raidBossName\">(.*?)<em class=\"bossLv\">(.*?)</em></span>");

    private static final Pattern POINT_PATTERN = Pattern.compile("<p class=\"getContributionPointText\"><span class=\"getContributionPointLiner\">(.*?)</span><span class=\"contributionPointNum\">(.*?)</span></p>");
    private static final Pattern TOTAL_POINT_PATTERN = Pattern.compile("<p class=\"totalContributionPoint\">(.*?)</p>");

    public RaidBattleResultHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    public String handleIt() {
        Map<String, Object> session = this.robot.getSession();
        String raidBattleId = (String) session.get("raidBattleId");
        String token = (String) session.get("token");
        final String path = String.format("/raid/raid-battle-result?raidBattleId=%s&token=%s",
                                          raidBattleId,
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);
        this.printBossName(html);
        this.printPoint(html);
        this.printTotalPoint(html);
        return "/raid";
    }

    private void printBossName(String html) {
        if (this.log.isInfoEnabled()) {
            Matcher matcher = BOSS_PATTERN.matcher(html);
            if (matcher.find()) {
                String bossName = matcher.group(1);
                String bossLevel = matcher.group(2);
                this.log.info(String.format("%s (%s)", bossName, bossLevel));
            }
        }
    }

    private void printPoint(String html) {
        if (this.log.isInfoEnabled()) {
            Matcher matcher = POINT_PATTERN.matcher(html);
            while (matcher.find()) {
                String contributionPointLiner = matcher.group(1);
                String contributionPointNum = matcher.group(2);
                this.log.info(String.format("%s %s",
                                            contributionPointLiner,
                                            StringEscapeUtils.unescapeHtml(contributionPointNum)));
            }
        }
    }

    private void printTotalPoint(String html) {
        if (this.log.isInfoEnabled()) {
            Matcher matcher = TOTAL_POINT_PATTERN.matcher(html);
            if (matcher.find()) {
                String text = matcher.group(1);
                text = StringUtils.remove(text, "<span>");
                text = StringUtils.remove(text, "</span>");
                this.log.info(text);
            }
        }
    }
}
