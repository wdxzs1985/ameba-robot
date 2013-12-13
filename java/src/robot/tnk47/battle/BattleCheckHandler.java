package robot.tnk47.battle;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.tnk47.Tnk47Robot;

public class BattleCheckHandler extends AbstractBattleHandler {

	public BattleCheckHandler(final Tnk47Robot robot) {
		super(robot);
	}

	@Override
	protected String handleIt() {
		final Map<String, Object> session = this.robot.getSession();

		final String battleStartType = (String) session.get("battleStartType");
		final String enemyId = (String) session.get("enemyId");
		final String prefectureBattleId = (String) session
				.get("prefectureBattleId");
		final String path = "/battle/battle-check";
		final List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("battleStartType", battleStartType));
		nvps.add(new BasicNameValuePair("enemyId", enemyId));
		nvps.add(new BasicNameValuePair("prefectureBattleId",
				prefectureBattleId));

		final String html = this.httpPost(path, nvps);

		if (this.isBattleResult(html)) {
			return "/battle/prefecture-battle-result";
		}

		this.resolveInputToken(html);

		final JSONObject jsonPageParams = this.resolvePageParams(html);
		if (jsonPageParams != null) {
			final int curPower = jsonPageParams.getInt("curPower");
			final JSONObject selectedDeckData = jsonPageParams
					.getJSONObject("selectedDeckData");
			final int spendAttackPower = selectedDeckData
					.getInt("spendAttackPower");
			final String deckId = jsonPageParams.getString("selectedDeckId");
			if (curPower >= spendAttackPower || spendAttackPower == 0) {
				session.put("deckId", deckId);
				session.put("attackType", "1");
				session.put("powerRegenItemType", "");
				return "/battle/battle-animation";
			} else {
				final JSONObject powerRegenItems = jsonPageParams
						.getJSONObject("powerRegenItems");
				final JSONObject halfRegenUserItemDto = powerRegenItems
						.getJSONObject("halfRegenUserItemDto");
				final JSONObject fullRegenUserItemDto = powerRegenItems
						.getJSONObject("fullRegenUserItemDto");

				final int halfRegenTodayCount = halfRegenUserItemDto
						.getInt("todayCount");
				if (this.robot.isUseTodayPowerRegenItem()
						&& halfRegenTodayCount > 0) {
					final String itemName = halfRegenUserItemDto
							.getString("itemName");
					session.put("itemName", itemName);
					session.put("powerRegenItemType", "0");
					session.put("deckId", deckId);
					session.put("attackType", "1");
					return "/battle/battle-animation";
				}

				final int fullRegenTodayCount = fullRegenUserItemDto
						.getInt("todayCount");
				if (this.robot.isUseTodayPowerRegenItem()
						&& fullRegenTodayCount > 0) {
					final String itemName = fullRegenUserItemDto
							.getString("itemName");
					session.put("itemName", itemName);
					session.put("powerRegenItemType", "1");
					session.put("deckId", deckId);
					session.put("attackType", "1");
					return "/battle/battle-animation";
				}

				final int halfRegenTotalCount = halfRegenUserItemDto
						.getInt("totalCount");
				if (this.robot.isUseHalfPowerRegenItem()
						&& halfRegenTotalCount > 0) {
					final String itemName = halfRegenUserItemDto
							.getString("itemName");
					session.put("itemName", itemName);
					session.put("powerRegenItemType", "0");
					session.put("deckId", deckId);
					session.put("attackType", "1");
					return "/battle/battle-animation";
				}

				final int fullRegenTotalCount = fullRegenUserItemDto
						.getInt("totalCount");
				if (this.robot.isUseFullPowerRegenItem()
						&& fullRegenTotalCount > 0) {
					final String itemName = fullRegenUserItemDto
							.getString("itemName");
					session.put("itemName", itemName);
					session.put("powerRegenItemType", "1");
					session.put("deckId", deckId);
					session.put("attackType", "1");
					return "/battle/battle-animation";
				}

				session.put("isBattlePowerOut", true);
				if (this.log.isInfoEnabled()) {
					this.log.info("攻pt不足");
				}
			}
		}
		return "/mypage";
	}
}
