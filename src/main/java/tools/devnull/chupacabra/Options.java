package tools.devnull.chupacabra;

public class Options {

  public boolean readLobs;
  public String connectionUrl;
  public String user;
  public String password;
  public String type;

  public void setDriverClass(String driverClass) {
    try {
      Class.forName(driverClass);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public Query createQuery() {
    Query query = new DefaultQuery(connectionUrl, user, password);
    if (type != null) {
      switch (type) {
        case "teiid":
          query = new TeiidQuery(query);
          break;
      }
    }
    query.basic();
    if (readLobs) {
      query.readLobs();
    }
    return query;
  }

}
