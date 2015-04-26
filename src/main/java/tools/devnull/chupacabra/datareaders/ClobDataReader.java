package tools.devnull.chupacabra.datareaders;

import tools.devnull.chupacabra.DataReader;
import tools.devnull.chupacabra.Reads;
import tools.devnull.chupacabra.Statistics;

import java.io.Reader;
import java.nio.CharBuffer;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.Types;

@Reads(Types.CLOB)
public class ClobDataReader implements DataReader {

  @Override
  public void read(ResultSet resultSet, int column, Statistics statistics) throws Exception {
    Clob clob = resultSet.getClob(column);
    Reader reader = clob.getCharacterStream();
    CharBuffer buffer = CharBuffer.allocate((int) clob.length());
    reader.read(buffer);
    statistics.readed(buffer.length() * Statistics.CHAR);
    clob.free();
  }

}
