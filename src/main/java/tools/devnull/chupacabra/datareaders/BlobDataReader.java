package tools.devnull.chupacabra.datareaders;

import tools.devnull.chupacabra.DataReader;
import tools.devnull.chupacabra.Reads;
import tools.devnull.chupacabra.Statistics;

import java.sql.Blob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Reads(Types.BLOB)
public class BlobDataReader implements DataReader {
  @Override
  public void read(ResultSet resultSet, int column, Statistics statistics) throws SQLException {
    Blob blob = resultSet.getBlob(column);
    byte[] bytes = blob.getBytes(0, (int) blob.length()); //ugly, but works for now
    statistics.readed(bytes.length);
    blob.free();
  }

}
