package robot.tnk47.upgrade;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.tnk47.Tnk47Robot;

public class UpgradeAutoConfirmHandler extends AbstractEventHandler<Tnk47Robot> {

	public UpgradeAutoConfirmHandler(final Tnk47Robot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		final String baseUserCardId = (String) session.get("baseUserCardId");
		final String token = (String) session.get("token");
		final List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("baseUserCardId", baseUserCardId));
		nvps.add(new BasicNameValuePair("token", token));
		final JSONObject jsonResponse = this.httpPostJSON(
				"/upgrade/ajax/get-auto-upgrade-confirm", nvps);
		this.resolveJsonToken(jsonResponse);

		if (jsonResponse.containsKey("data")) {
			final JSONObject data = jsonResponse.getJSONObject("data");
			if (data.getBoolean("canUpgrade")) {
				final JSONArray materialUserCards = data
						.getJSONArray("materialUserCards");
				final StringBuilder materialUserCardIds = new StringBuilder();
				for (int i = 0; i < materialUserCards.size(); i++) {
					if (i != 0) {
						materialUserCardIds.append(",");
					}
					final JSONObject materialUserCard = materialUserCards
							.getJSONObject(i);
					final String materialUserCardId = materialUserCard
							.getString("userCardId");
					materialUserCardIds.append(materialUserCardId);
				}
				session.put("baseUserCardId", baseUserCardId);
				session.put("materialUserCardIds",
						materialUserCardIds.toString());
				return "/upgrade/upgrade-animation";
			}
		} else if (jsonResponse.containsKey("message")) {
			final String message = jsonResponse.getString("message");
			if (this.log.isInfoEnabled()) {
				this.log.info(message);
			}
		}
		return "/mypage";
	}
}