package robot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import common.CommonHttpClient;

public class Launcher {

    protected final Log log = LogFactory.getLog(this.getClass());
    protected final Properties config = new Properties();
    protected final CommonHttpClient httpClient = new CommonHttpClient();

    public void init(final String setup) {
        this.initConfig(setup);
        this.initHttpClient(setup);
    }

    private void initConfig(final String setup) {
        InputStream inputConfig = null;
        try {
            inputConfig = FileUtils.openInputStream(new File(setup));
            this.config.load(inputConfig);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(inputConfig);
        }
    }

    private void initHttpClient(final String setup) {
        final String username = this.config.getProperty("Robot.username");
        final String cookie = this.config.getProperty("Robot.cookie");
        final File cookieFile = new File(username + cookie);
        this.httpClient.loadCookie(cookieFile);
    }

    public boolean is(final String key) {
        final String value = this.config.getProperty(key, "false");
        return Boolean.valueOf(value);
    }

}
