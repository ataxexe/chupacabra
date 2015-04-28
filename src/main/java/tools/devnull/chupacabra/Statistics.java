package tools.devnull.chupacabra;

public class Statistics {

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
  private long lobBytesRead = 0;

  private int currentFormat = 0;
  private long startTime;
  private long startRowProcessingTime;

  private long elapsedInRowProcessing;
  private long speed;
  private int row;

  private Status status;

  public void reset() {
    startTime = System.currentTimeMillis();
    elapsedInRowProcessing = 0;
    speed = 0;
    row = 0;
    status = Status.STOPPED;
  }

  public void readed(int bytes) {
    lobBytesRead += bytes;
    if(currentFormat < FORMAT.length - 1 && lobBytesRead > BASE_VALUES[currentFormat + 1]) {
      currentFormat++;
    }
  }

  public void startedProcessingRows() {
    status = Status.SUCKING;
    startRowProcessingTime = System.currentTimeMillis();
  }

  public void nextRow() {
    elapsedInRowProcessing = (System.currentTimeMillis() - startRowProcessingTime);
    if (elapsedInRowProcessing > 0) {
      speed = row * 1000 / elapsedInRowProcessing;
    }
    row++;
  }

  public void finish() {
    elapsedInRowProcessing = (System.currentTimeMillis() - startRowProcessingTime);
    speed = row * 1000 / elapsedInRowProcessing;
    status = Status.FINISHED;
  }

  @Override
  public String toString() {
    double time = (System.currentTimeMillis() - startTime) / 1000.0;
    switch (status) {
      case CONNECTING:
        return String.format("Connecting... | %.2f seconds", time);
      case GETTING_METADATA:
        return String.format("Getting metadata | %.2f seconds", time);
      case QUERYING:
        return String.format("Querying database | %.2f seconds", time);
    }
    if (lobBytesRead > 0) {
      return String.format("Rows: %d | %d rows/second | %.2f seconds | %.2f %s of lobs readed", row, speed, time,
          lobBytesRead / BASE_VALUES[currentFormat], FORMAT[currentFormat]);
    }
    return String.format("Rows: %d | %d rows/second | %.2f seconds", row, speed, time);
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
