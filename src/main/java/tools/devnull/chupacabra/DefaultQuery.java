package tools.devnull.chupacabra;

import tools.devnull.chupacabra.datareaders.ClobDataReader;
import tools.devnull.chupacabra.datareaders.DefaultDataReader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class DefaultQuery implements Query {

  private final Map<Integer, DataReader> readers = new HashMap<Integer, DataReader>(){
    private DataReader defaultDataReader = new DefaultDataReader();

    @Override
    public DataReader get(Object o) {
      if (!containsKey(o)) {
        return defaultDataReader;
      }
      return super.get(o);
    }
  };
  
  private final String url;
  private final String user;
  private final String password;

  private Statistics statistics = new Statistics();

  public DefaultQuery(String url, String user, String password) {
    this.url = url;
    this.user = user;
    this.password = password;
  }

  @Override
  public Statistics getStatistics() {
    return statistics;
  }

  @Override
  public Query readLobs() {
    register(new ClobDataReader());
    return this;
  }

  @Override
  public Query register(DataReader reader) {
    Reads types = reader.getClass().getAnnotation(Reads.class);
    if (types == null) {
      throw new IllegalArgumentException("The reader " + reader.getClass() + " must declare a @Reads annotation.");
    }
    for (int i : types.value()) {
      readers.put(i, reader);
    }
    return this;
  }

  @Override
  public void execute(String sql) {
    statistics.reset();
    Connection connection = null;
    ResultSet resultSet = null;
    Statement statement = null;
    try {
      statistics.connecting();
      connection = DriverManager.getConnection(url, user, password);
      statement = connection.createStatement();
      statistics.querying();
      resultSet = statement.executeQuery(sql);
      statistics.gettingMetadata();
      ResultSetMetaData metaData = resultSet.getMetaData();
      int count = metaData.getColumnCount();
      int[] types = new int[count];

      for (int i = 1; i <= count; i++) {
        types[i - 1] = metaData.getColumnType(i);
      }
      DataReader reader;
      statistics.startedProcessingRows();
      while (resultSet.next()) {
        statistics.nextRow();
        for (int i = 1; i <= count; i++) {
          reader = readers.get(types[i - 1]);
          reader.read(resultSet, i, statistics);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if(resultSet != null) resultSet.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        if(statement != null) statement.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        if(connection != null) connection.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      statistics.finish();
    }
  }
}
