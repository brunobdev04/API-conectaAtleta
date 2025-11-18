package br.appConecta.appConecta;

import java.net.URI;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;

@Configuration
public class DatabaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(Environment env) {
        try {
            String databaseUrl = env.getProperty("DATABASE_URL");
            if (databaseUrl != null && databaseUrl.startsWith("postgres://")) {
                // Convert render-style postgres://user:pass@host:port/db to JDBC
                URI uri = new URI(databaseUrl);
                String userInfo = uri.getUserInfo();
                String user = null;
                String pass = null;
                if (userInfo != null && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    user = parts[0];
                    pass = parts[1];
                }
                String host = uri.getHost();
                int port = uri.getPort();
                String path = uri.getPath(); // includes leading '/'
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d%s", host, port, path);
                StringBuilder jdbcWithParams = new StringBuilder(jdbcUrl);
                jdbcWithParams.append("?");
                if (user != null) {
                    jdbcWithParams.append("user=").append(user);
                }
                if (pass != null) {
                    jdbcWithParams.append("&password=").append(pass);
                }

                HikariDataSource ds = new HikariDataSource();
                ds.setJdbcUrl(jdbcWithParams.toString());
                ds.setDriverClassName("org.postgresql.Driver");
                if (user != null) ds.setUsername(user);
                if (pass != null) ds.setPassword(pass);
                // Also set persistence and dialect properties so Hibernate can determine the
                // dialect even if it attempts to bootstrap before JDBC metadata is available.
                try {
                    System.setProperty("jakarta.persistence.jdbc.url", jdbcWithParams.toString());
                    System.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
                    logger.info("Set system properties for JDBC URL and Hibernate dialect for Postgres");
                } catch (Exception ex) {
                    logger.warn("Unable to set system properties for JDBC/dialect: {}", ex.getMessage());
                }

                logger.info("Configured DataSource from DATABASE_URL (postgres URI)");
                return ds;
            }
        } catch (Exception e) {
            logger.warn("Failed to parse DATABASE_URL, falling back to properties: {}", e.getMessage());
        }

        // Fallback to properties (spring.datasource.* in application.properties)
        String jdbc = env.getProperty("spring.datasource.url");
        String driver = env.getProperty("spring.datasource.driver-class-name");
        String user = env.getProperty("spring.datasource.username");
        String pass = env.getProperty("spring.datasource.password");

        HikariDataSource ds = new HikariDataSource();
        if (jdbc != null) ds.setJdbcUrl(jdbc);
        if (driver != null) ds.setDriverClassName(driver);
        if (user != null) ds.setUsername(user);
        if (pass != null) ds.setPassword(pass);
        logger.info("Configured DataSource from spring.datasource.* properties");
        return ds;
    }
}
