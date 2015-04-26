package tools.devnull.chupacabra;

public interface Query {
  Statistics getStatistics();

  void readAllUsing(DataReader reader);

  Query basic();

  Query readLobs();

  Query register(DataReader reader);

  void execute(String sql);
}
