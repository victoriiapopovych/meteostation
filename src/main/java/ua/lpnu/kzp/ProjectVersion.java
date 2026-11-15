package ua.lpnu.kzp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Надає версію програми, сформовану Maven під час збірки.
 */
public final class ProjectVersion {

    private static final String VERSION = loadVersion();

    private ProjectVersion() {
    }

    /**
     * Повертає версію програми.
     *
     * @return версія Maven-проєкту
     */
    public static String get() {
        return VERSION;
    }

    private static String loadVersion() {
        Properties properties = new Properties();

        try (InputStream input =
                     ProjectVersion.class.getResourceAsStream(
                             "/version.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "Не знайдено version.properties");
            }

            properties.load(input);

            String version =
                    properties.getProperty("application.version");

            if (version == null || version.isBlank()) {
                throw new IllegalStateException(
                        "Версія програми не визначена");
            }

            return version.trim();

        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Не вдалося прочитати версію програми",
                    exception);
        }
    }
}