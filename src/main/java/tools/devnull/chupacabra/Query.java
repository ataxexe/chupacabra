package tools.devnull.chupacabra;

public interface Query {
  Statistics getStatistics();

  Query readLobs();

  Query register(DataReader reader);

  void execute(String sql);
}
