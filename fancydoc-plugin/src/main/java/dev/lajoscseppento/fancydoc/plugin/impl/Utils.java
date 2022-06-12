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

  void copyResourceInto(@NonNull String resourceName, @NonNull Path targetDir) {
    logger.debug("Copying resource {} into {}", resourceName, targetDir);

    copyResource(resourceName, targetDir.resolve(resourceName));
  }

  void copyResource(@NonNull String resourceName, @NonNull Path targetFile) {
    logger.debug("Copying resource {} to {}", resourceName, targetFile);

    URL resource = Utils.class.getResource('/' + resourceName);
    if (resource == null) {
      throw new GradleException("Resource not found: " + resourceName);
    }

    try (InputStream is = resource.openStream()) {
      Files.createDirectories(targetFile.getParent());
      Files.copy(is, targetFile);
    } catch (Exception ex) {
      throw new GradleException("Failed to copy " + resourceName + " to " + targetFile);
    }
  }
}
