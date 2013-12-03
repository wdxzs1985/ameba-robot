package robot.tnk47;

import java.util.Collections;
import java.util.List;

import net.sf.json.JSONObject;

import org.apache.http.message.BasicNameValuePair;

import robot.AbstractEventHandler;
import robot.Robot;

public class EvnetInfomationHandler extends AbstractEventHandler {

	public EvnetInfomationHandler(final Robot robot) {
		super(robot);
	}

	@Override
	public String handleIt() {
		final String input = this.robot
				.buildPath("/event/ajax/get-current-event-information");
		final List<BasicNameValuePair> nvps = Collections.emptyList();
		final String html = this.robot.getHttpClient().post(input, nvps);
		final JSONObject currentEventInfomation = JSONObject.fromObject(html);
		final JSONObject data = currentEventInfomation.getJSONObject("data");
		if (data.containsKey("currentEventInfoDto")) {
			final JSONObject currentEventInfoDto = data
					.getJSONObject("currentEventInfoDto");
			if (this.log.isInfoEnabled()) {
				final int rank = currentEventInfoDto.getInt("rank");
				final int score = currentEventInfoDto.getInt("score");
				final String term = currentEventInfoDto.getString("term");
				final String mainText = currentEventInfoDto
						.getString("mainText");
				this.log.info("イベント中");
				this.log.info(String.format("获得总分: %d，排名: %d", score, rank));
				this.log.info(term);
				this.log.info(mainText);
			}

			final String linkUrl = currentEventInfoDto.getString("linkUrl");
			return linkUrl;
		}
		return ("/mypage");
	}

}
