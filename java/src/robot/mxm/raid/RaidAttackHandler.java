package robot.mxm.raid;

import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import robot.mxm.MxmEventHandler;
import robot.mxm.MxmRobot;

public class RaidAttackHandler extends MxmEventHandler {

	public RaidAttackHandler(final MxmRobot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final Map<String, Object> session = this.robot.getSession();
		String raidId = (String) session.get("raidId");
		String targetMonsterCategoryId = (String) session
				.get("targetMonsterCategoryId");
		String token = (String) session.get("token");

		String path = String.format("/raid/%s/attack", raidId);
		List<BasicNameValuePair> nvps = this.createNameValuePairs();
		nvps.add(new BasicNameValuePair("spendBp", "1"));
		nvps.add(new BasicNameValuePair("targetMonsterCategoryId",
				targetMonsterCategoryId));
		nvps.add(new BasicNameValuePair("token", token));

		String html = this.httpPost(path, nvps);

		this.log.debug(html);

		return "/raid/attack/result";
	}

}
