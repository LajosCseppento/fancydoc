package dev.lajoscseppento.fancydoc.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FancydocPluginFunctionalTest {
  @TempDir Path projectDir;

  @Test
  void testBuild() {
    // Given
    setUpDemoProject();

    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("build")
            .withProjectDir(projectDir.toFile());

    // When
    BuildResult result = runner.build();

    // Then
    checkBuildResult(result);
  }

  @Test
  void testBuildEmptyProject() {
    // Given
    setUpEmptyProject();

    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("build")
            .withProjectDir(projectDir.toFile());

    // When
    BuildResult result = runner.build();

    // Then
    assertThat(result.getOutput()).contains("Task :javadoc");
  }

  @Test
  void testBuildWithGradle_7_5_1() {
    // Given
    setUpDemoProject();

    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withArguments("build")
            .withProjectDir(projectDir.toFile());

    // When
    BuildResult result = runner.build();

    // Then
    checkBuildResult(result);
  }

  @Test
  void testBuildFailsWithTooOldGradleVersion() {
    // Given
    setUpDemoProject();

    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withGradleVersion("7.4.2")
            .withArguments("build")
            .withProjectDir(projectDir.toFile());

    // When
    BuildResult result = runner.buildAndFail();

    // Then
    assertThat(result.getOutput())
        .containsPattern("Gradle version .+ is too old, please use .+ at least");
  }

  private void setUpDemoProject() {
    try {
      Path demoDir = Paths.get("../fancydoc-demo").toAbsolutePath().normalize();
      FileUtils.copyDirectory(
          demoDir.toFile(),
          projectDir.toFile(),
          FancydocPluginFunctionalTest::shouldCopyDemoProject,
          false);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private static boolean shouldCopyDemoProject(File file) {
    return file.isDirectory()
        || FilenameUtils.isExtension(file.getName(), "java", "kts", "properties");
  }

  private void setUpEmptyProject() {
    try {
      Path demoDir = Paths.get("../fancydoc-demo").toAbsolutePath().normalize();
      FileUtils.copyDirectory(
          demoDir.toFile(),
          projectDir.toFile(),
          FancydocPluginFunctionalTest::shouldCopyEmptyProject,
          false);
    } catch (IOException ex) {
      throw new UncheckedIOException(ex);
    }
  }

  private static boolean shouldCopyEmptyProject(File file) {
    return FilenameUtils.isExtension(file.getName(), "kts", "properties");
  }

  private void checkBuildResult(BuildResult result) {
    assertThat(result.getOutput()).contains("Task :javadoc");

    checkLinkSource();
  }

  private void checkLinkSource() {
    Path sourceFileInJavadoc =
        projectDir.resolve(
            "build/docs/javadoc/src-html/dev/lajoscseppento/fancydoc/example/Library.html");

    assertThat(sourceFileInJavadoc)
        .exists()
        .content()
        .contains("package dev.lajoscseppento.fancydoc.example;")
        .contains("<code class=\"language-java line-numbers\">")
        .contains(
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"../../../../../fancydoc/fancydoc.css\">")
        .contains(
            "<script type=\"text/javascript\" src=\"../../../../../fancydoc/prism/prism.js\"></script>")
        .doesNotContain("<span class=\"sourceLineNo\">");
  }
}
