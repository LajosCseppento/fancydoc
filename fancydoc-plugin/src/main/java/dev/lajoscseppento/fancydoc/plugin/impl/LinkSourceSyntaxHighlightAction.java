package dev.lajoscseppento.fancydoc.plugin.impl;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import lombok.NonNull;
import org.gradle.api.Action;
import org.gradle.api.GradleException;
import org.gradle.api.Task;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.javadoc.Javadoc;
import org.gradle.external.javadoc.MinimalJavadocOptions;
import org.gradle.external.javadoc.StandardJavadocDocletOptions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

/** Highlights Javadoc link source code using Prism. */
public class LinkSourceSyntaxHighlightAction implements Action<Task> {
  private Javadoc javadoc;
  private Logger logger;
  private Path srcHtmlDir;
  private Path fancydocDir;

  @Override
  public void execute(@NonNull Task task) {
    if (!(task instanceof Javadoc)) {
      throw new GradleException(
          getClass().getSimpleName()
              + " should be only applied to Javadoc task, "
              + task
              + " is not applicable");
    }

    javadoc = (Javadoc) task;
    logger = javadoc.getLogger();

    MinimalJavadocOptions options = javadoc.getOptions();
    if (options instanceof StandardJavadocDocletOptions) {
      if (((StandardJavadocDocletOptions) options).isLinkSource()) {
        applySyntaxHighlighting();
      } else {
        logger.warn(
            "{} is applied to Javadoc task with link source disabled, skipping syntax highlighting",
            getClass().getSimpleName());
      }
    } else {
      logger.warn(
          "{} is applied to Javadoc task without standard doclet (doclet: {}, Javadoc options type: {}), skipping syntax highlighting",
          getClass().getSimpleName(),
          options.getDoclet(),
          options.getClass().getName());
    }
  }

  private void applySyntaxHighlighting() {
    logger.lifecycle("Applying syntax highlight to generated Javadoc source code");

    try {
      if (javadoc.getDestinationDir() == null) {
        throw new GradleException("Javadoc destination directory is null");
      }

      Path javadocDir = javadoc.getDestinationDir().toPath();
      srcHtmlDir = javadocDir.resolve("src-html");
      fancydocDir = javadocDir.resolve("fancydoc");

      copyResources();
      rewriteSourceFiles();
    } catch (Exception ex) {
      throw new GradleException(
          "Failed to add syntax highlighting to generated Javadoc source code: " + ex.getMessage(),
          ex);
    }
  }

  private void copyResources() {
    Utils.copyResource("fancydoc.css", fancydocDir);
    Utils.copyResource("prism/LICENSE", fancydocDir);
    Utils.copyResource("prism/prism.css", fancydocDir);
    Utils.copyResource("prism/prism.js", fancydocDir);
  }

  private void rewriteSourceFiles() throws IOException {
    logger.debug("Looking for source code files in {} ...", srcHtmlDir);

    Files.walkFileTree(
        srcHtmlDir,
        new SimpleFileVisitor<>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
            if (file.getFileName().toString().endsWith(".html")) {
              logger.debug("Applying Javadoc syntax highlighting to {}", file);
              try {
                applySyntaxHighlighting(file);
              } catch (Exception ex) {
                throw new GradleException(
                    "Failed to apply syntax highlighting to " + file + ": " + ex.getMessage(), ex);
              }
            }

            return FileVisitResult.CONTINUE;
          }
        });
  }

  private void applySyntaxHighlighting(Path file) throws IOException {
    Document document = Jsoup.parse(file.toFile(), "utf-8");
    // Keep the original formatting
    document.outputSettings().prettyPrint(false);

    applySyntaxHighlighting(document, file);

    logger.debug("Writing file {}", file);
    // Use LF, like javadoc does
    Files.writeString(file, document.outerHtml().replace("\r\n", "\n"));
  }

  private void applySyntaxHighlighting(Document document, Path file) {
    String relativePathToFancydocDir =
        file.getParent().relativize(fancydocDir).toString().replace('\\', '/');

    addStylesheet(document, relativePathToFancydocDir + "/fancydoc.css");
    // JS import using file:/// URLs is supported by modern browsers due to security reasons
    addScript(document, relativePathToFancydocDir + "/prism/prism.js");

    Element pre = document.body().getElementsByTag("pre").get(0);
    Element code = document.createElement("code").attr("class", "language-java line-numbers");

    Iterator<Node> it = pre.childNodes().iterator();
    while (it.hasNext()) {
      Node childNode = it.next();

      if (childNode.nodeName().equals("span") && "sourceLineNo".equals(childNode.attr("class"))) {
        childNode.remove();
      } else if (childNode.nodeName().equals("a")) {
        code.appendChild(childNode);
      } else if (it.hasNext()) {
        code.appendChild(childNode);
      }

      // Leave the last text block with many empty lines out of <code>, but inside <pre>, as it was.
      // This prevents Prism adding line numbers on these empty lines.
    }

    pre.prependChild(code);
  }

  private void addStylesheet(Document document, String href) {
    document
        .head()
        .appendElement("link")
        .attr("rel", "stylesheet")
        .attr("type", "text/css")
        .attr("href", href);
    document.head().appendText("\n");
  }

  private void addScript(Document document, String src) {
    document.head().appendElement("script").attr("type", "text/javascript").attr("src", src);
    document.head().appendText("\n");
  }
}
