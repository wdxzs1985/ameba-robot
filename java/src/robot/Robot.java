package robot;

import java.util.Map;
import java.util.Properties;

import common.CommonHttpClient;

public interface Robot {

	public CommonHttpClient getHttpClient();

	public void dispatch(String string);

	public String buildPath(String string);

	public Map<String, Object> getSession();

	public Properties getConfig();

}
