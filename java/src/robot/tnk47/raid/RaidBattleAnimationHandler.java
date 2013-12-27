package robot.tnk47.raid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.commons.lang.StringEscapeUtils;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;
import robot.tnk47.raid.model.RaidBattleDamageBean;
import robot.tnk47.raid.model.RaidBattleDamageMap;

public class RaidBattleAnimationHandler extends Tnk47EventHandler {

    private static final Pattern RAID_RESULT_DATA_PATTERN = Pattern.compile("raidResultData = '(\\{.*\\})';");

    private final RaidBattleDamageMap damageMap;

    public RaidBattleAnimationHandler(final Tnk47Robot robot,
            final RaidBattleDamageMap damageMap) {
        super(robot);
        this.damageMap = damageMap;
    }

    @Override
    public String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String deckId = (String) session.get("deckId");
        final String raidBattleId = (String) session.get("raidBattleId");
        final String isFullPower = (String) session.get("isFullPower");
        final String isSpecialAttack = (String) session.get("isSpecialAttack");
        final String useApSmall = (String) session.get("useApSmall");
        final String useApFull = (String) session.get("useApFull");
        final String usePowerHalf = (String) session.get("usePowerHalf");
        final String usePowerFull = (String) session.get("usePowerFull");
        final String token = (String) session.get("token");

        final String path = String.format("/raid/raid-battle-animation?deckId=%s&isFullPower=%s&isSpecialAttack=%s&raidBattleId=%s&useApSmall=%s&useApFull=%s&usePowerHalf=%s&usePowerFull=%s&token=%s",
                                          deckId,
                                          isFullPower,
                                          isSpecialAttack,
                                          raidBattleId,
                                          useApSmall,
                                          useApFull,
                                          usePowerHalf,
                                          usePowerFull,
                                          token);
        final String html = this.httpGet(path);
        this.resolveInputToken(html);

        final JSONObject raidResultData = this.resolveRaidResultData(html);
        if (raidResultData != null) {
            final JSONObject animation = raidResultData.optJSONObject("animation");
            final long damagePoint = animation.optInt("damagePoint");
            this.damageMap.addDamage(raidBattleId, damagePoint);
            if (this.log.isInfoEnabled()) {
                final RaidBattleDamageBean damageBean = this.damageMap.getRaidBattleDamageBean(raidBattleId);
                final long totalDamage = damageBean.getDamage();
                this.log.info(String.format("对BOSS造成 %d 的伤害, total: %d。",
                                            damagePoint,
                                            totalDamage));
            }
        }
        return "/raid/battle";
    }

    private JSONObject resolveRaidResultData(final String html) {
        final Matcher matcher = RaidBattleAnimationHandler.RAID_RESULT_DATA_PATTERN.matcher(html);
        if (matcher.find()) {
            String text = matcher.group(1);
            text = StringEscapeUtils.unescapeJava(text);
            final JSONObject raidResultData = JSONObject.fromObject(text);
            return raidResultData;
        }
        return null;
    }
}
