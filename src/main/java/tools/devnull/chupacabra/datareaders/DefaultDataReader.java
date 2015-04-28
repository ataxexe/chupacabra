package tools.devnull.chupacabra.datareaders;

import tools.devnull.chupacabra.DataReader;
import tools.devnull.chupacabra.Statistics;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultDataReader implements DataReader {

  @Override
  public void read(ResultSet resultSet, int column, Statistics statistics) throws SQLException {
    resultSet.getObject(column);
  }

}
