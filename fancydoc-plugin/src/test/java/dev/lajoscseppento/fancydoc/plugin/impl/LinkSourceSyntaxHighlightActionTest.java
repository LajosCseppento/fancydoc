package dev.lajoscseppento.fancydoc.plugin.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

class LinkSourceSyntaxHighlightActionTest {
  @TempDir Path javadocDestinationDir;

  @Test
  void test() throws Exception {
    // Given
    Path linkSourceFile =
        javadocDestinationDir.resolve("src-html/dev/lajoscseppento/fancydoc/example/Library.html");
    Utils.copyResource("Library.original.html", linkSourceFile);

    StandardJavadocDocletOptions options = Mockito.mock(StandardJavadocDocletOptions.class);
    Mockito.doReturn(true).when(options).isLinkSource();

    Javadoc javadoc = Mockito.mock(Javadoc.class);
    Mockito.doReturn(Mockito.mock(Logger.class)).when(javadoc).getLogger();
    Mockito.doReturn(javadocDestinationDir.toFile()).when(javadoc).getDestinationDir();
    Mockito.doReturn(options).when(javadoc).getOptions();

    // When
    new LinkSourceSyntaxHighlightAction().execute(javadoc);

    // Then
    String expected;
    try (InputStream is = getClass().getResourceAsStream("/Library.expected.html")) {
      expected = new Scanner(is, StandardCharsets.UTF_8.name()).useDelimiter("\\A").next();
    }

    assertThat(linkSourceFile).content().isEqualTo(expected);
  }

  @Test
  void testMissingJavadoc() {
    // Given
    StandardJavadocDocletOptions options = Mockito.mock(StandardJavadocDocletOptions.class);
    Mockito.doReturn(true).when(options).isLinkSource();

    Javadoc javadoc = Mockito.mock(Javadoc.class);
    Mockito.doReturn(Mockito.mock(Logger.class)).when(javadoc).getLogger();
    Mockito.doReturn(javadocDestinationDir.toFile()).when(javadoc).getDestinationDir();
    Mockito.doReturn(options).when(javadoc).getOptions();

    // When
    new LinkSourceSyntaxHighlightAction().execute(javadoc);

    // Then
    assertThat(javadocDestinationDir).isEmptyDirectory();
  }
}
