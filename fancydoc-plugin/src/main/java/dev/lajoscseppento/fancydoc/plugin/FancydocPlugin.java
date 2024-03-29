package dev.lajoscseppento.fancydoc.plugin;

import dev.lajoscseppento.fancydoc.plugin.impl.LinkSourceSyntaxHighlightAction;
import dev.lajoscseppento.gradle.plugin.common.CurrentGradleVersion;
import lombok.NonNull;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;

/** Fancydoc Gradle plugin. */
public class FancydocPlugin implements Plugin<Project> {
  private static final String MINIMUM_GRADLE_VERSION = "8.4";
  private static final String UTF_8 = "utf-8";
  private Project project;
  private Logger logger;

  @Override
  public void apply(@NonNull Project project) {
    CurrentGradleVersion.requireAtLeast(MINIMUM_GRADLE_VERSION);

    this.project = project;
    logger = project.getLogger();

    logger.info("[{}] Apply on {}", getClass().getSimpleName(), project);

    configureJavadoc();
  }

  private void configureJavadoc() {
    project
        .getTasks()
        .withType(Javadoc.class)
        .configureEach(
            javadoc -> {
              MinimalJavadocOptions options = javadoc.getOptions();

              configureMinimalJavadocOptions(javadoc, options);

              if (options instanceof StandardJavadocDocletOptions standardJavadocDocletOptions) {
                configureStandardJavadocDocletOptions(javadoc, standardJavadocDocletOptions);

                javadoc.doLast(new LinkSourceSyntaxHighlightAction());
              }
            });
  }

  private void configureMinimalJavadocOptions(
      @NonNull Javadoc javadoc, @NonNull MinimalJavadocOptions options) {
    logger.info("[{}] Configuring minimal options of {}", getClass().getSimpleName(), javadoc);

    options.encoding(UTF_8);
  }

  private void configureStandardJavadocDocletOptions(
      @NonNull Javadoc javadoc, @NonNull StandardJavadocDocletOptions options) {
    logger.info("[{}] Configuring standard options of {}", getClass().getSimpleName(), javadoc);

    options.charSet(UTF_8);
    options.docEncoding(UTF_8);

    options.linkSource(true);
  }
}
