package dev.lajoscseppento.fancydoc.plugin;

import dev.lajoscseppento.fancydoc.plugin.impl.VersionComparator;
import lombok.NonNull;
import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

/** Fancydoc Gradle plugin. */
public class FancydocPlugin implements Plugin<Project> {
  private static final String MINIMUM_GRADLE_VERSION = "7.0";
  private Project project;
  private Logger logger;

  @Override
  public void apply(@NonNull Project project) {
    checkGradleVersion(project.getGradle().getGradleVersion());

    this.project = project;
    logger = project.getLogger();

    logger.info("[{}] Apply on {}", getClass().getSimpleName(), project);

    configureJavadoc();
  }

  private void configureJavadoc() {
    project
        .getTasks()
        .withType(
            Javadoc.class,
            javadoc -> {
              MinimalJavadocOptions options = javadoc.getOptions();

              configureMinimalJavadocOptions(javadoc, options);

              if (options instanceof StandardJavadocDocletOptions) {
                configureStandardJavadocDocletOptions(
                    javadoc, (StandardJavadocDocletOptions) options);
              }
            });
  }

  private void configureMinimalJavadocOptions(Javadoc javadoc, MinimalJavadocOptions options) {
    logger.info("[{}] Configuring minimal options of {}", getClass().getSimpleName(), javadoc);

    options.encoding("UTF-8");
  }

  private void configureStandardJavadocDocletOptions(
      Javadoc javadoc, StandardJavadocDocletOptions options) {
    logger.info("[{}] Configuring standard options of {}", getClass().getSimpleName(), javadoc);

    options.charSet("UTF-8");
    options.docEncoding("UTF-8");

    options.linkSource(true);
  }

  // TODO This method is a candidate for moving to a common utility
  private static void checkGradleVersion(String gradleVersion) {
    int cmp = new VersionComparator().compare(MINIMUM_GRADLE_VERSION, gradleVersion);

    if (cmp > 0) {
      String msg =
          String.format(
              "Gradle version %s is too old, please use %s at least.",
              gradleVersion, MINIMUM_GRADLE_VERSION);
      throw new GradleException(msg);
    }
  }
}