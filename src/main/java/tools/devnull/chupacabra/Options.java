package tools.devnull.chupacabra;

import static tools.devnull.trugger.element.Elements.element;

public class Options {

  private boolean readLobs;
  private String connectionUrl;
  private String user;
  private String password;
  private String sql;

  public Options(String[] args) {
    extractParameters(args);
  }

  private void extractParameters(String[] args) {
    for (String arg : args) {
      if (arg.startsWith("--")) {
        if (arg.contains("=")) {
          String[] option = arg.substring(2).split("=");
          String name = normalize(option[0]);
          String value = option[1];
          element(name).in(this).set(value);
        } else {
          String name = normalize(arg.substring(2));
          element(name).in(this).set(true);
        }
      }
      sql = arg;
    }
  }

  private String normalize(String param) {
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

  public void setDriverClass(String driverClass) {
    try {
      Class.forName(driverClass);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public Query createQuery() {
    Query query = new DefaultQuery(connectionUrl, user, password);
    if (readLobs) {
      query.readLobs();
    }
    return query;
  }

  public String sql() {
    return sql;
  }

}
