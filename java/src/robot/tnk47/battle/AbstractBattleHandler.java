package robot.tnk47.battle;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import robot.AbstractEventHandler;
import robot.Robot;

public abstract class AbstractBattleHandler extends AbstractEventHandler {

	private static final Pattern BATTLE_RESULT_PATTERN = Pattern
			.compile("nextUrl: \"/battle/prefecture-battle-result\\?prefectureBattleId=(.*)\"");

	public AbstractBattleHandler(Robot robot) {
		super(robot);
	}

	protected boolean isBattleResult(String html) {
		final Map<String, Object> session = this.robot.getSession();
		final Matcher battleResultMatcher = BATTLE_RESULT_PATTERN.matcher(html);
		if (battleResultMatcher.find()) {
			final String prefectureBattleId = battleResultMatcher.group(1);
			session.put("prefectureBattleId", prefectureBattleId);
			return true;
		}
		return false;
	}
}
