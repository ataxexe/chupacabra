package tools.devnull.chupacabra;

import tools.devnull.chupacabra.datareaders.teiid.TeiidBlobDataReader;
import tools.devnull.chupacabra.datareaders.teiid.TeiidClobDataReader;

public class TeiidQuery implements Query {

  private final Query query;

  public TeiidQuery(Query query) {
    this.query = query;
  }

  @Override
  public Statistics getStatistics() {
    return query.getStatistics();
  }

  @Override
  public void readAllUsing(DataReader reader) {
    query.readAllUsing(reader);
  }

  @Override
  public Query basic() {
    query.basic();
    return this;
  }

  @Override
  public Query readLobs() {
    query.register(new TeiidClobDataReader());
    query.register(new TeiidBlobDataReader());
    return this;
  }

  @Override
  public Query register(DataReader reader) {
    query.register(reader);
    return this;
  }

  @Override
  public void execute(String sql) {
    query.execute(sql);
  }

}
