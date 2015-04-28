package tools.devnull.chupacabra;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static tools.devnull.trugger.element.Elements.copy;

public class Main {

  public static void main(String[] args) throws Exception {
    if (args.length == 0) {
      printUsage();
      System.exit(0);
    }
    Options options = new Options();
    String sql = extractParameters(args, options);
    Query query = options.createQuery();
    Statistics statistics = query.getStatistics();

    new Thread(() -> query.execute(sql)).start();

    while (!statistics.isFinished()) {
      System.out.print("\r" + statistics + " ");
      Thread.sleep(100);
    }

    System.out.print("\r" + statistics + " ");
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

  private static String extractParameters(String[] args, Object target) {
    List<String> argsList = new ArrayList<>();
    argsList.addAll(Arrays.asList(args));
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
    copy().from(argsMap).to(target);

    return argsList.get(0);
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
