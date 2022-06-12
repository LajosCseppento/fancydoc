package dev.lajoscseppento.fancydoc.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class FancydocPluginFunctionalTest {
  @TempDir Path projectDir;

  @BeforeEach
  void setUp() throws IOException {
    Path demoDir = Paths.get("../fancydoc-demo").toAbsolutePath().normalize();
    FileUtils.copyDirectory(
        demoDir.toFile(), projectDir.toFile(), FancydocPluginFunctionalTest::shouldCopy, false);
  }

  private static boolean shouldCopy(File file) {
    return file.isDirectory()
        || FilenameUtils.isExtension(file.getName(), "java", "kts", "properties");
  }

  @Test
  void testBuild() {
    // Given
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
  void testBuildWithGradle_7_0() {
    // Given
    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withGradleVersion("7.0")
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
    GradleRunner runner =
        GradleRunner.create()
            .forwardOutput()
            .withPluginClasspath()
            .withGradleVersion("6.9")
            .withArguments("build")
            .withProjectDir(projectDir.toFile());

    // When
    BuildResult result = runner.buildAndFail();

    // Then
    assertThat(result.getOutput())
        .containsPattern("Gradle version .+ is too old, please use .+ at least");
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
        .doesNotContain("<span class=\"sourceLineNo\">");
  }
}
