package com.emmily.runtimedeps.channel;

import com.emmily.runtimedeps.FileType;
import com.emmily.runtimedeps.MavenDependency;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class DelegatingReadableByteChannel implements ReadableByteChannel {

  private final ReadableByteChannel delegate;
  private final MavenDependency dependency;
  private final FileType fileType;
  private final Logger logger;
  private final AtomicInteger countdown;
  private final long totalSize;
  private long downloaded;

  public DelegatingReadableByteChannel(ReadableByteChannel delegate,
                                       MavenDependency dependency,
                                       FileType fileType,
                                       Logger logger,
                                       long totalSize) {
    this.delegate = delegate;
    this.dependency = dependency;
    this.fileType = fileType;
    this.logger = logger;
    this.totalSize = totalSize;
    this.countdown = new AtomicInteger(50);
  }

  @Override
  public int read(ByteBuffer dst) throws IOException {
    int bytes = delegate.read(dst);

    if (bytes <= 0) {
      return bytes;
    }

    downloaded += bytes;

    if (countdown.get() < 0) {
      logger.info(String.format(
        "Dependencies: Downloading %s file of the dependency %s: %s",
        fileType,
        dependency.getArtifactId(),
        Math.round(((double) downloaded / (double) totalSize) * 100)
      ) + " %");
      countdown.set(50);
    } else {
      countdown.getAndDecrement();
    }

    return bytes;
  }

  @Override
  public boolean isOpen() {
    return delegate.isOpen();
  }

  @Override
  public void close() throws IOException {
    delegate.close();
  }

}
