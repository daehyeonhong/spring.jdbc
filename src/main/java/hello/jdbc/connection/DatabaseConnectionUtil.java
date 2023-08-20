package hello.jdbc.connection;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConstant.*;

public class DatabaseConnectionUtil {
    private static final Logger log = LoggerFactory.getLogger(DatabaseConnectionUtil.class);

    public static Connection getConnection() {
        try {
            final Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection: {}, class: {}", connection, connection.getClass());
            return connection;
        } catch (final SQLException sqlException) {
            throw new IllegalStateException(sqlException);
        }
    }
}
