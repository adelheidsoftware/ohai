package model.gpu.general;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class GpuGeneralModel {

  private final String productName;
  private final String codeName;
  private final String revision;
  private final String vendor;
  private final String architecture;
  private final String foundry;
  private final String processNode;
  private final long transistors;
  private final int dieSizeSquare; // millimeters
  private final Instant releaseDate;
  private final String biosVersion;
  private final String deviceId;
  private final String busInterface;

  private GpuGeneralModel(Builder builder) {
    this.productName = builder.productName;
    this.codeName = builder.codeName;
    this.revision = builder.revision;
    this.vendor = builder.vendor;
    this.architecture = builder.architecture;
    this.foundry = builder.foundry;
    this.processNode = builder.processNode;
    this.transistors = builder.transistors;
    this.dieSizeSquare = builder.dieSizeSquare;
    this.releaseDate = builder.releaseDate;
    this.biosVersion = builder.biosVersion;
    this.deviceId = builder.deviceId;
    this.busInterface = builder.busInterface;
  }

  public String productName() {
    return productName;
  }

  public String codeName() {
    return codeName;
  }

  public String revision() {
    return revision;
  }

  public String vendor() {
    return vendor;
  }

  public String architecture() {
    return architecture;
  }

  public String foundry() {
    return foundry;
  }

  public String processNode() {
    return processNode;
  }

  public long transistors() {
    return transistors;
  }

  public int dieSizeSquare() {
    return dieSizeSquare;
  }

  public Instant releaseDate() {
    return releaseDate;
  }

  public String releaseDateString() {
    return DateTimeFormatter.ofPattern("MMM d, yyyy").format(LocalDateTime.ofInstant(releaseDate, ZoneOffset.UTC));
  }

  public String biosVersion() {
    return biosVersion;
  }

  public String deviceId() {
    return deviceId;
  }

  public String busInterface() {
    return busInterface;
  }

  public static class Builder {

    private final String productName;
    private String codeName = "Unknown";
    private String revision = "Unknown";
    private String vendor = "Unknown";
    private String architecture = "Unknown";
    private String foundry = "Unknown";
    private String processNode = "Unknown";
    private long transistors = 0;
    private int dieSizeSquare = 0;
    private Instant releaseDate = Instant.EPOCH;
    private String biosVersion = "Unknown";
    private String deviceId = "Unknown";
    private String busInterface = "Unknown";

    public Builder(String productName) {
      this.productName = productName;
    }

    public Builder codeName(String codeName) {
      this.codeName = codeName;
      return this;
    }

    public Builder revision(String revision) {
      this.revision = revision;
      return this;
    }

    public Builder vendor(String vendor) {
      this.vendor = vendor;
      return this;
    }

    public Builder architecture(String architecture) {
      this.architecture = architecture;
      return this;
    }

    public Builder foundry(String foundry) {
      this.foundry = foundry;
      return this;
    }

    public Builder processNode(String processNode) {
      this.processNode = processNode;
      return this;
    }

    public Builder transistors(long transistors) {
      this.transistors = transistors;
      return this;
    }

    public Builder dieSizeSquare(int dieSizeSquare) {
      this.dieSizeSquare = dieSizeSquare;
      return this;
    }

    public Builder releaseDate(Instant releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }

    public Builder biosVersion(String biosVersion) {
      this.biosVersion = biosVersion;
      return this;
    }

    public Builder deviceId(String deviceId) {
      this.deviceId = deviceId;
      return this;
    }

    public Builder busInterface(String busInterface) {
      this.busInterface = busInterface;
      return this;
    }

    public GpuGeneralModel build() {
      return new GpuGeneralModel(this);
    }

  }

}
