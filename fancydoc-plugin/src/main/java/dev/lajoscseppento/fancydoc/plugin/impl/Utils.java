package dev.lajoscseppento.fancydoc.plugin.impl;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;

@UtilityClass
class Utils {
  private static final Logger logger = Logging.getLogger(Utils.class);

  void copyResource(@NonNull String resourceName, @NonNull Path targetDir) {
    logger.debug("Copying resource {} to {}", resourceName, targetDir);

    URL resource = Utils.class.getResource('/' + resourceName);
    if (resource == null) {
      throw new GradleException("Resource not found: " + resourceName);
    }

    try (InputStream is = resource.openStream()) {
      Path target = targetDir.resolve(resourceName);
      Files.createDirectories(target.getParent());
      Files.copy(is, target);
    } catch (Exception ex) {
      throw new GradleException("Failed to copy " + resourceName + " to " + targetDir);
    }
  }
}
