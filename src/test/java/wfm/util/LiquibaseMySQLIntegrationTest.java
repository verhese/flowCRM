package wfm.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

class LiquibaseMySQLIntegrationTest {

    private void withMySql(ActionWithDb action) throws Exception {
        try (MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0.33")
                .withDatabaseName("flowCRM")
                .withUsername("tester")
                .withPassword("tester")
                .withCommand("--log-bin-trust-function-creators=1")) {
            mysql.start();

            System.setProperty("liquibase.url", mysql.getJdbcUrl());
            System.setProperty("liquibase.username", mysql.getUsername());
            System.setProperty("liquibase.password", mysql.getPassword());
            System.setProperty("liquibase.driver", "com.mysql.cj.jdbc.Driver");

            try {
                LiquibaseBootstrap.update();

                try (Connection connection = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
                     Statement statement = connection.createStatement()) {
                    action.execute(statement);
                }
            } finally {
                System.clearProperty("liquibase.url");
                System.clearProperty("liquibase.username");
                System.clearProperty("liquibase.password");
                System.clearProperty("liquibase.driver");
            }
        }
    }

    @FunctionalInterface
    private interface ActionWithDb {
        void execute(Statement statement) throws Exception;
    }

    @Test
    void testLiquibaseCanApplyToMySQLContainer() throws Exception {
        withMySql(statement -> {
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM flow_services");
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals(0, rs.getInt(1));
        });
    }

    @Test
    void testFlowServicesInsertMySQL() throws Exception {
        withMySql(statement -> {
            statement.executeUpdate("DELETE FROM flow_services");
            int inserted = statement.executeUpdate("INSERT INTO flow_services(code) VALUES('SERVICE-1')");
            Assertions.assertEquals(1, inserted);

            ResultSet rs = statement.executeQuery("SELECT code FROM flow_services WHERE code='SERVICE-1'");
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("SERVICE-1", rs.getString("code"));
        });
    }

    @Test
    void testFlowServicesUpdateMySQL() throws Exception {
        withMySql(statement -> {
            statement.executeUpdate("DELETE FROM flow_services");
            statement.executeUpdate("INSERT INTO flow_services(code) VALUES('SERVICE-1')");

            int updated = statement.executeUpdate("UPDATE flow_services SET code='SERVICE-2' WHERE code='SERVICE-1'");
            Assertions.assertEquals(1, updated);

            ResultSet rs = statement.executeQuery("SELECT code FROM flow_services WHERE code='SERVICE-2'");
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("SERVICE-2", rs.getString("code"));
        });
    }

    @Test
    void testFlowServicesDeleteMySQL() throws Exception {
        withMySql(statement -> {
            statement.executeUpdate("DELETE FROM flow_services");
            statement.executeUpdate("INSERT INTO flow_services(code) VALUES('SERVICE-1')");

            int deleted = statement.executeUpdate("DELETE FROM flow_services WHERE code='SERVICE-1'");
            Assertions.assertEquals(1, deleted);

            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM flow_services");
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals(0, rs.getInt(1));
        });
    }

    @Test
    void testTaskStateUpdateCreatesStateLogMySQL() throws Exception {
        withMySql(statement -> {
            statement.executeUpdate("DELETE FROM flow_task_state_logs");
            statement.executeUpdate("DELETE FROM flow_tasks");
            statement.executeUpdate("DELETE FROM flow_instances");
            statement.executeUpdate("DELETE FROM flow_services");

            statement.executeUpdate("INSERT INTO flow_services(code) VALUES('SERVICE-STATE')");
            statement.executeUpdate("INSERT INTO flow_instances(id, service_id) VALUES(1, 'SERVICE-STATE')");
            statement.executeUpdate("INSERT INTO flow_tasks(id, instance_id, service_code, state) VALUES(10, 1, 'SERVICE-STATE', 'RG')");

            int updated = statement.executeUpdate("UPDATE flow_tasks SET state='FIN' WHERE id=10");
            Assertions.assertEquals(1, updated);

            ResultSet rs = statement.executeQuery("SELECT new_state, updated_at, updated_by FROM flow_task_state_logs WHERE task_id=10");
            Assertions.assertTrue(rs.next());
            Assertions.assertEquals("FIN", rs.getString("new_state"));
            Assertions.assertNotNull(rs.getTimestamp("updated_at"));
            Assertions.assertTrue(rs.getString("updated_by").toLowerCase().contains("tester"));
            Assertions.assertFalse(rs.next());
        });
    }
}
