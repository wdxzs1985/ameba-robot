package robot;

import java.util.Map;

import common.CommonHttpClient;

public interface Robot {

	public void init();

	public void dispatch(String string);

	public CommonHttpClient getHttpClient();

	public Map<String, Object> getSession();

	public String getHost();

	public String getUsername();

	public String getPassword();

	public int getRequestDelay();

	public int getScheduleDelay();
}
