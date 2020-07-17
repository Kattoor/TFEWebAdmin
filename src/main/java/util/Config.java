package util;

import com.moandjiezana.toml.Toml;

public class Config {

    private static final Toml toml = new Toml().read(Config.class.getResourceAsStream("/app.secret.toml"));
    private static final String environment = System.getenv("env");

    public static String getKeystorePassword() {
        return toml.getString("keystore.password");
    }

    public static String getEnvironment() {
        return environment != null ? environment : "";
    }
}
