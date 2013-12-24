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
        final Map<String, Object> session = this.robot.getSession();
        final String raidBattleId = (String) session.get("raidBattleId");
        final String token = (String) session.get("token");
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

    private void printBossName(final String html) {
        if (this.log.isInfoEnabled()) {
            final Matcher matcher = RaidBattleResultHandler.BOSS_PATTERN.matcher(html);
            if (matcher.find()) {
                final String bossName = matcher.group(1);
                final String bossLevel = matcher.group(2);
                this.log.info(String.format("%s (%s) 讨伐成功", bossName, bossLevel));
            }
        }
    }

    private void printPoint(final String html) {
        if (this.log.isInfoEnabled()) {
            final Matcher matcher = RaidBattleResultHandler.POINT_PATTERN.matcher(html);
            while (matcher.find()) {
                final String contributionPointLiner = matcher.group(1);
                final String contributionPointNum = matcher.group(2);
                this.log.info(String.format("%s %s",
                                            contributionPointLiner,
                                            StringEscapeUtils.unescapeHtml(contributionPointNum)));
            }
        }
    }

    private void printTotalPoint(final String html) {
        if (this.log.isInfoEnabled()) {
            final Matcher matcher = RaidBattleResultHandler.TOTAL_POINT_PATTERN.matcher(html);
            if (matcher.find()) {
                String text = matcher.group(1);
                text = StringUtils.replace(text, "<span>", " ");
                text = StringUtils.replace(text, "</span>", " ");
                this.log.info(text);
            }
        }
    }
}
