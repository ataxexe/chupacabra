package tools.devnull.chupacabra.datareaders;

import tools.devnull.chupacabra.DataReader;
import tools.devnull.chupacabra.Reads;
import tools.devnull.chupacabra.Statistics;

import java.sql.ResultSet;
import java.sql.Types;

@Reads(Types.INTEGER)
public class IntegerDataReader implements DataReader {
  @Override
  public void read(ResultSet resultSet, int column, Statistics statistics) throws Exception {
    resultSet.getInt(column);
    statistics.readed(Statistics.INT);
  }
}
