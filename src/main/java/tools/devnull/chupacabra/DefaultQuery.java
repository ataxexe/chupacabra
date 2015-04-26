package tools.devnull.chupacabra;

import tools.devnull.chupacabra.datareaders.*;
import tools.devnull.trugger.util.registry.MapRegistry;
import tools.devnull.trugger.util.registry.Registry;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.List;

import static tools.devnull.trugger.reflection.FieldPredicates.type;
import static tools.devnull.trugger.reflection.Reflection.fields;
import static tools.devnull.trugger.reflection.Reflection.handle;

public class DefaultQuery implements Query {

  private final Registry<Integer, DataReader> readers = new MapRegistry<>();
  
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
  public void readAllUsing(DataReader reader) {
    List<Field> types = fields().filter(type(int.class)).in(Types.class);
    for (Field type : types) {
      readers.register(reader).to(handle(type).value());
    }
  }

  @Override
  public Query basic() {
    readAllUsing(new DefaultDataReader());

    register(new DecimalDataReader());
    register(new IntegerDataReader());
    register(new VarcharDataReader());
    return this;
  }

  @Override
  public Query readLobs() {
    register(new ClobDataReader());
    register(new BlobDataReader());
    return this;
  }

  @Override
  public Query register(DataReader reader) {
    Reads types = reader.getClass().getAnnotation(Reads.class);
    if (types == null) {
      throw new IllegalArgumentException("The reader " + reader.getClass() + " must declare a @Reads annotation.");
    }
    for (int i : types.value()) {
      readers.register(reader).to(i);
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
      while (resultSet.next()) {
        statistics.nextRow();
        for (int i = 1; i <= count; i++) {
          reader = readers.registryFor(types[i - 1]);
          reader.read(resultSet, i, statistics);
        }
      }
      statistics.finish();
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
    }
  }
}
