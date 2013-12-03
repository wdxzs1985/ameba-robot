package robot.tnk47.battle;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;

import robot.Robot;

public class BattleResultHandler extends AbstractBattleHandler {

	private static final Pattern SINGLE_RESULT_PATTERN = Pattern
			.compile("<h1 class=\"pageName\">対戦結果</h1><p><span>(.*)に<em>(.*)</em>のダメージ!</span></p>");

	public BattleResultHandler(final Robot robot) {
		super(robot);
	}

	@Override
	protected String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String token = (String) session.get("token");
		final String path = "/battle/battle-result";
		final List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("token", token));
		final String html = this.httpPost(path, nvps);

		if (this.isBattleResult(html)) {
			return ("/battle/prefecture-battle-result");
		}

		if (this.log.isInfoEnabled()) {
			final Matcher singleResultMatcher = BattleResultHandler.SINGLE_RESULT_PATTERN
					.matcher(html);
			if (singleResultMatcher.find()) {
				final String name = singleResultMatcher.group(1);
				final String damage = singleResultMatcher.group(2);
				this.log.info(String.format("对%s造成%s的伤害", name, damage));
			}
		}

		return ("/battle");
	}
}
