package wfm.util;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public final class LiquibaseBootstrap {
    private LiquibaseBootstrap() {
        // utility class
    }

    public static void update() throws Exception {
        Properties properties = new Properties();

        try (InputStream stream = LiquibaseBootstrap.class.getClassLoader().getResourceAsStream("liquibase.properties")) {
            if (stream == null) {
                throw new IllegalStateException("liquibase.properties not found in classpath");
            }
            properties.load(stream);
        }

        String url = System.getProperty("liquibase.url", System.getenv().getOrDefault("LIQUIBASE_URL", properties.getProperty("url")));
        String user = System.getProperty("liquibase.username", System.getenv().getOrDefault("LIQUIBASE_USERNAME", properties.getProperty("username")));
        String password = System.getProperty("liquibase.password", System.getenv().getOrDefault("LIQUIBASE_PASSWORD", properties.getProperty("password")));
        String driver = System.getProperty("liquibase.driver", System.getenv().getOrDefault("LIQUIBASE_DRIVER", properties.getProperty("driver")));

        if (driver != null && !driver.isBlank()) {
            Class.forName(driver);
        }

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
            try (Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.yaml", new ClassLoaderResourceAccessor(), database)) {
                liquibase.update(new Contexts(), new LabelExpression());
            }
        }
    }
}
