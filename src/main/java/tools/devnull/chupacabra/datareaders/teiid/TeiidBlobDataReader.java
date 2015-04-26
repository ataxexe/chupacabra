package tools.devnull.chupacabra.datareaders.teiid;

import org.teiid.core.types.BlobType;
import tools.devnull.chupacabra.DataReader;
import tools.devnull.chupacabra.Reads;
import tools.devnull.chupacabra.Statistics;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@Reads(Types.BLOB)
public class TeiidBlobDataReader implements DataReader {
  @Override
  public void read(ResultSet resultSet, int column, Statistics statistics) throws SQLException {
    BlobType reference = new BlobType(resultSet.getBlob(column));
    byte[] bytes = reference.getBytes(0, (int) reference.length()); //ugly, but works for now
    statistics.readed(bytes.length);
    reference.free();
  }

}
