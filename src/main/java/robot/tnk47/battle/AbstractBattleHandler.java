package robot.tnk47.battle;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.tnk47.Tnk47EventHandler;
import robot.tnk47.Tnk47Robot;

public abstract class AbstractBattleHandler extends Tnk47EventHandler {

    private static final Pattern BATTLE_RESULT_PATTERN = Pattern.compile("nextUrl: \"/battle/prefecture-battle-result\\?prefectureBattleId=(.*?)\"");

    public AbstractBattleHandler(final Tnk47Robot robot) {
        super(robot);
    }

    protected boolean isBattleResult(final String html) {
        final Map<String, Object> session = this.robot.getSession();
        final Matcher matcher = AbstractBattleHandler.BATTLE_RESULT_PATTERN.matcher(html);
        if (matcher.find()) {
            final String prefectureBattleId = matcher.group(1);
            session.put("prefectureBattleId", prefectureBattleId);
            return true;
        }
        return false;
    }
}
