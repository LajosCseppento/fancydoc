package dev.lajoscseppento.fancydoc.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import org.gradle.api.Project;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

class FancydocPluginTest {
  @Test
  void testApplyDoesNotApplyTheJavaPlugin() {
    // Given
    Project project = ProjectBuilder.builder().build();
    project.setGroup("test");
    project.setVersion("0.0.0-SNAPSHOT");

    // When
    project.getPlugins().apply("dev.lajoscseppento.fancydoc");

    // Then
    assertThat(project.getPluginManager().hasPlugin("dev.lajoscseppento.fancydoc")).isTrue();
    assertThat(project.getPluginManager().hasPlugin("java")).isFalse();
  }

  @Test
  void testApplyConfiguresJavadicIfPresent() {
    // Given
    Project project = ProjectBuilder.builder().build();
    project.setGroup("test");
    project.setVersion("0.0.0-SNAPSHOT");

    // When
    project.getPlugins().apply("dev.lajoscseppento.fancydoc");
    project.getPlugins().apply("java-library");

    // Then
    assertThat(project.getPluginManager().hasPlugin("dev.lajoscseppento.fancydoc")).isTrue();

    project
        .getTasks()
        .withType(
            Javadoc.class,
            javadoc -> {
              StandardJavadocDocletOptions options =
                  (StandardJavadocDocletOptions) javadoc.getOptions();
              assertThat(options.getCharSet()).isEqualTo("UTF-8");
              assertThat(options.getDocEncoding()).isEqualTo("UTF-8");
              assertThat(options.getEncoding()).isEqualTo("UTF-8");
              assertThat(options.isLinkSource()).isTrue();
            });
  }
}
