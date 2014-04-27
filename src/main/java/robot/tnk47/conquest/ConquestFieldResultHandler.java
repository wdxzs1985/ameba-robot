package robot.tnk47.conquest;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public class ConquestFieldResultHandler extends Tnk47EventHandler {

    private static final Pattern BATTLE_BONUS_PATTERN = Pattern.compile("data-bonus-ids=\"([0-9,]*?)\"");

    public ConquestFieldResultHandler(final Tnk47Robot robot) {
        super(robot);
    }

    @Override
    protected String handleIt() {
        final Map<String, Object> session = this.robot.getSession();
        final String conquestBattleId = (String) session.get("conquestBattleId");
        final String token = (String) session.get("token");
        final String path = "/conquest/conquest-field-battle-result?conquestBattleId=%s&token=%s";
        final String html = this.httpGet(String.format(path,
                                                       conquestBattleId,
                                                       token));

        final Matcher matcher = ConquestFieldResultHandler.BATTLE_BONUS_PATTERN.matcher(html);
        if (matcher.find()) {
            final String bounsId = matcher.group(1);
            session.put("bounsId", bounsId);
            return "/conquest/gacha";
        }
        return "/conquest";
    }

}
