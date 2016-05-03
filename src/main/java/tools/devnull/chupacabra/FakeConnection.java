package tools.devnull.chupacabra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class FakeConnection {

  private Connection connection;

  private FakeConnection(String url, Properties info) throws SQLException {
    this.connection = DriverManager.getConnection(url, info);
  }

  private Connection createConnection() {
    return new MethodDumper<>(Connection.class).createProxy(connection);
  }

  static Connection newConnection(String url, Properties info) throws SQLException {
    return new FakeConnection(url, info).createConnection();
  }

}
