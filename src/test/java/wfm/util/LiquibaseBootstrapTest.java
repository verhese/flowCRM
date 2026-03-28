package wfm.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

class LiquibaseBootstrapTest {

    @Test
    void testLiquibaseUpdatesDatabaseSchema() throws Exception {
        LiquibaseBootstrap.update();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:flowcrm;DB_CLOSE_DELAY=-1;MODE=MYSQL", "sa", "")) {
            try (Statement statement = connection.createStatement()) {
                // clean up from previous CRUD tests in the same JVM in-memory DB
                statement.executeUpdate("DELETE FROM flow_services");

                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM flow_services");
                Assertions.assertTrue(rs.next());
                Assertions.assertEquals(0, rs.getInt(1));
            }
        }
    }

    @Test
    void testFlowServicesInsert() throws Exception {
        LiquibaseBootstrap.update();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:flowcrm;DB_CLOSE_DELAY=-1;MODE=MYSQL", "sa", "")) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM flow_services");

                int inserted = statement.executeUpdate("INSERT INTO flow_services(code) VALUES('SERVICE-1')");
                Assertions.assertEquals(1, inserted);

                ResultSet rs = statement.executeQuery("SELECT code FROM flow_services WHERE code='SERVICE-1'");
                Assertions.assertTrue(rs.next());
                Assertions.assertEquals("SERVICE-1", rs.getString("code"));
            }
        }
    }

    @Test
    void testFlowServicesUpdate() throws Exception {
        LiquibaseBootstrap.update();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:flowcrm;DB_CLOSE_DELAY=-1;MODE=MYSQL", "sa", "")) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM flow_services");
                statement.executeUpdate("INSERT INTO flow_services(code) VALUES('SERVICE-1')");

                int updated = statement.executeUpdate("UPDATE flow_services SET code='SERVICE-2' WHERE code='SERVICE-1'");
                Assertions.assertEquals(1, updated);

                ResultSet rs = statement.executeQuery("SELECT code FROM flow_services WHERE code='SERVICE-2'");
                Assertions.assertTrue(rs.next());
                Assertions.assertEquals("SERVICE-2", rs.getString("code"));
            }
        }
    }

    @Test
    void testFlowServicesDelete() throws Exception {
        LiquibaseBootstrap.update();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:flowcrm;DB_CLOSE_DELAY=-1;MODE=MYSQL", "sa", "")) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DELETE FROM flow_services");
                statement.executeUpdate("INSERT INTO flow_services(code) VALUES('SERVICE-1')");

                int deleted = statement.executeUpdate("DELETE FROM flow_services WHERE code='SERVICE-1'");
                Assertions.assertEquals(1, deleted);

                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM flow_services");
                Assertions.assertTrue(rs.next());
                Assertions.assertEquals(0, rs.getInt(1));
            }
        }
    }

    @Test
    void testTaskStateUpdateCreatesStateLog() throws Exception {
        LiquibaseBootstrap.update();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:flowcrm;DB_CLOSE_DELAY=-1;MODE=MYSQL", "sa", "")) {
            try (Statement statement = connection.createStatement()) {
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
                Assertions.assertFalse(rs.getString("updated_by").isBlank());
                Assertions.assertFalse(rs.next());
            }
        }
    }
}
