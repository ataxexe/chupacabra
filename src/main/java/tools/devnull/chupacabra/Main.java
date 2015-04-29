package tools.devnull.chupacabra;

public class Main {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      printUsage();
      System.exit(0);
    }
    Options options = new Options(args);
    Query query = options.createQuery();
    Statistics statistics = query.getStatistics();

    new Thread(() -> query.execute(options.sql())).start();

    while (!statistics.isFinished()) {
      System.out.print("\r" + statistics + " ");
      Thread.sleep(100);
    }

    System.out.print("\r" + statistics + " ");
    System.out.println();
  }

  private static void printUsage() {
    System.out.println("Parameters: [<options>] <query>");
    System.out.println("  query\t\t\tThe query to execute");
    System.out.println("Options:");
    System.out.println("  --connection-url\tSets the connection url");
    System.out.println("  --user\t\tSets the user");
    System.out.println("  --password\t\tSets the password");
    System.out.println("  --read-lobs\t\tFetch lobs from database");
    System.out.println("  --driver-class\tSet the driver class (for non JDBC 4.0 drivers)");
  }

}
