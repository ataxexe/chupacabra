package tools.devnull.chupacabra;

import java.util.*;

import static tools.devnull.trugger.element.Elements.copy;

public class Main {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      printUsage();
      return;
    }
    List<String> argsList = new ArrayList<>();
    for (String arg : args) {
      argsList.add(arg);
    }
    Map<String, Object> argsMap = new HashMap<>();
    Iterator<String> argsIterator = argsList.iterator();
    while(argsIterator.hasNext()) {
      String arg = argsIterator.next();
      if (arg.startsWith("--")) {
        if (arg.contains("=")) {
          String[] option = arg.substring(2).split("=");
          argsMap.put(normalize(option[0]), option[1]);
        } else {
          argsMap.put(normalize(arg.substring(2)), true);
        }
        argsIterator.remove();
      }
    }
    Options options = new Options();
    copy().from(argsMap).to(options);

    String sql = argsList.get(0);

    final Query query = options.createQuery();
    Statistics statistics = query.getStatistics();
    Thread thread = new Thread(() -> query.execute(sql));
    thread.start();
    while (!statistics.isFinished()) {
      System.out.print("\r" + statistics + "                      ");
      Thread.sleep(100);
    }
    System.out.print("\r" + statistics + "                      ");
    System.out.println();
  }

  private static String normalize(String param) {
    String[] split = param.split("-");
    StringBuilder builder = new StringBuilder();
    for (String s : split) {
      if(builder.length() == 0) {
        builder.append(s);
      } else {
        builder.append(Character.toUpperCase(s.charAt(0)));
        builder.append(s.substring(1));
      }
    }
    return builder.toString();
  }

  private static void printUsage() {
    System.out.println("Parameters: [<options>] <query>");
    System.out.println("  query\t\t\tThe query to execute");
    System.out.println("Options:");
    System.out.println("  --connection-url\tSets the connection url");
    System.out.println("  --user\t\tSets the user");
    System.out.println("  --password\t\tSets the password");
    System.out.println("  --type\t\tUses specific driver readers: (teiid)");
    System.out.println("  --read-lobs\t\tFetch lobs from database");
    System.out.println("  --driver-class\tSet the driver class (for non JDBC 4.0 drivers)");
  }

}
