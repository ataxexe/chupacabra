package tools.devnull.chupacabra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Driver implements java.sql.Driver {

  static {
    try {
      DriverManager.registerDriver(new Driver());
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private static final org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger("chupacabra-driver");

  @Override
  public Connection connect(String url, Properties info) throws SQLException {
    Pattern pattern = Pattern.compile("jdbc:chupacabra:(?<realDriver>[._a-zA-Z0-9]+):(?<realUrl>.+)");
    Matcher matcher = pattern.matcher(url);
    if(matcher.matches()) {
      String realDriver = matcher.group("realDriver");
      String realUrl = url.replaceAll(pattern.pattern(), "jdbc:$2");
      try {
        logger.info("Loading driver " + realDriver);
        // load the real driver
        Class.forName(realDriver);
      } catch (ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
      logger.info("Creating connection to" + realUrl);
      return FakeConnection.newConnection(realUrl, info);
    } else {
      throw new SQLException();
    }
  }

  @Override
  public boolean acceptsURL(String url) throws SQLException {
    return url.startsWith("jdbc:chupacabra:");
  }

  @Override
  public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
    return new DriverPropertyInfo[]{
        new DriverPropertyInfo("realDriver", null)
    };
  }

  @Override
  public int getMajorVersion() {
    return 1;
  }

  @Override
  public int getMinorVersion() {
    return 0;
  }

  @Override
  public boolean jdbcCompliant() {
    return true;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }

}
