package tools.devnull.chupacabra;

import java.sql.ResultSet;

public interface DataReader {

  void read(ResultSet resultSet, int column, Statistics statistics) throws Exception;

}