package ongoing.backend.service.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

@Service
public class MonitoringFileService {
  private static final Logger log = LoggerFactory.getLogger(MonitoringFileService.class);
  private final AtomicBoolean listen = new AtomicBoolean(false);

  private final WatchKey key;
  private final Path monitoringDirectory;
  private final List<Consumer<Path>> callbacks = new CopyOnWriteArrayList<>();
  private final ExecutorService executorService = Executors.newSingleThreadExecutor();

  public MonitoringFileService(@Value("${logging.file.path}") String directory) throws IOException {
    this.monitoringDirectory = new FileSystemResource(directory).getFile().toPath();
    final WatchService ws = FileSystems.getDefault().newWatchService();

    key = monitoringDirectory.register(ws, ENTRY_MODIFY, ENTRY_CREATE);

    executorService.submit(this::monitor);
  }

  public List<Path> getAllFiles() throws IOException {
    return Files.list(monitoringDirectory)
      .filter(Files::isRegularFile)
      .toList();
  }

  public void listen(Consumer<Path> consumer) {
    callbacks.add(consumer);
  }

  private void monitor() {
    listen.set(true);

    while (listen.get()) {
      try {
        Thread.sleep(50);
        for (final WatchEvent<?> event : key.pollEvents()) {
          final Path changed = monitoringDirectory.resolve((Path) event.context());

          if (event.kind() == ENTRY_MODIFY && Files.isReadable(changed)) {
            log.trace("monitor - ENTRY_MODIFY: " + changed);
            callbacks.forEach(c -> c.accept(changed));
          }
        }

        boolean isKeyStillValid = key.reset();
        if (!isKeyStillValid) {
          log.trace("monitor - key is no longer valid: " + key);
          listen.set(false);
        }
      } catch (InterruptedException ex) {
        listen.set(false);
      }
    }
  }
}
