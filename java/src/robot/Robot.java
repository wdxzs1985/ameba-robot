package robot;

import java.util.Map;

import common.CommonHttpClient;

public interface Robot {

	public CommonHttpClient getHttpClient();

	public void dispatch(String string);

	public String buildPath(String string);

	public Map<String, Object> getSession();

	public int getActionTime();

	public String getHost();

	public String getUsername();

	public String getPassword();
}
