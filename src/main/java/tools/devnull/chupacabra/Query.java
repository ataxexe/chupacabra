package tools.devnull.chupacabra;

public interface Query {
  Statistics getStatistics();

  void readAllUsing(DataReader reader);

  Query readLobs();

  Query register(DataReader reader);

  void execute(String sql);
}
