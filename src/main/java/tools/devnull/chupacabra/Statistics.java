package tools.devnull.chupacabra;

public class Statistics {

  private static final int BIT_SIZE = 8;

  public static final int BYTE = 8 / BIT_SIZE;
  public static final int INT = 32 / BIT_SIZE;
  public static final int LONG = 64 / BIT_SIZE;
  public static final int CHAR = 16 / BIT_SIZE;
  public static final int FLOAT = 32 / BIT_SIZE;
  public static final int DOUBLE = 64 / BIT_SIZE;

  private static final String[] FORMAT = {"bytes", "KB", "MB", "GB"};
  private static final int POWER = 1024;
  private static double[] BASE_VALUES = new double[FORMAT.length];

  static {
    for (int i = 0; i < FORMAT.length; i++) {
      BASE_VALUES[i] = Math.pow(POWER, i);
    }
  }



  private enum Status {
    STOPPED,
    CONNECTING,
    GETTING_METADATA,
    QUERYING,
    SUCKING,
    FINISHED
  }
  private long bytesRead = 0;

  private int currentFormat = 0;
  private long startTime;

  private long elapsed;
  private long speed;
  private int row;

  private Status status;

  public void reset() {
    startTime = System.currentTimeMillis();
    elapsed = 0;
    speed = 0;
    row = 0;
    status = Status.STOPPED;
  }

  public void readed(int bytes) {
    bytesRead += bytes;
    if(currentFormat < FORMAT.length && bytesRead > BASE_VALUES[currentFormat + 1]) {
      currentFormat++;
    }
  }

  public void nextRow() {
    status = Status.SUCKING;
    elapsed = (System.currentTimeMillis() - startTime);
    speed = row++ * 1000 / elapsed;
  }

  public void finish() {
    elapsed = (System.currentTimeMillis() - startTime);
    speed = row * 1000 / elapsed;
    status = Status.FINISHED;
  }

  @Override
  public String toString() {
    switch (status) {
      case CONNECTING:
        return "Connecting...";
      case GETTING_METADATA:
        return "Getting metadata";
      case QUERYING:
        return "Querying database";
      case STOPPED:
        return "Stopped";
    }
    return String.format("Rows: %d (%.2f %s read | %d rows/second) | %.2f seconds", row,
        bytesRead / BASE_VALUES[currentFormat], FORMAT[currentFormat], speed, elapsed / 1000.0);
  }

  public void querying() {
    status = Status.QUERYING;
  }

  public void connecting() {
    status = Status.CONNECTING;
  }

  public void gettingMetadata() {
    status = Status.GETTING_METADATA;
  }

  public boolean isFinished() {
    return status == Status.FINISHED;
  }
}