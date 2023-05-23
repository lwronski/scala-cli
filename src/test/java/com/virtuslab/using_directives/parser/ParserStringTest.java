package com.virtuslab.using_directives.parser;

import static com.virtuslab.using_directives.DirectiveAssertions.assertDiagnostic;
import static com.virtuslab.using_directives.DirectiveAssertions.assertValueAtPath;
import static com.virtuslab.using_directives.TestUtils.*;

import com.virtuslab.using_directives.custom.model.Path;
import com.virtuslab.using_directives.custom.model.StringValue;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.custom.model.Value;
import com.virtuslab.using_directives.custom.utils.ast.StringLiteral;
import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import com.virtuslab.using_directives.reporter.PersistentReporter;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserStringTest {

  @Test
  public void testEscapeInString() {
    String input = "using keyA \"ab\\\"\\'\\\\\"";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA", "ab\"'\\");
  }

  @Test
  public void testMultilineString() {
    String input = joinLines("using keyA \"\"\"line1", "line2", "line3\"\"\"");
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "keyA", "line1\nline2\nline3");
  }

  @Test
  public void testNotAllowStringInterpolator() {
    PersistentReporter reporter = reporterAfterParsing("using keyA s\"foo\"");
    assertDiagnostic(reporter, 0, 11, "found string interpolator");
  }

  @Test
  public void testStringUtfEscape() {
    String input = "using foo \"\\u0042\"";
    UsingDirectives parsedDirective = testCode(1, input);
    assertValueAtPath(parsedDirective, "foo", "B");
  }
  @Test
  public void testDoubleQuotaString() {
    String input = "using foo \"bar\"";
    UsingDirectives parsedDirective = testCode(1, input);
    Value<?> directive = parsedDirective.getFlattenedMap().get(Path.fromString("foo")).get(0);
    assertInstanceOf(StringValue.class, directive);
    UsingTree astTree = directive.getRelatedASTNode();
    assertInstanceOf(StringLiteral.class, astTree);
    assertTrue(((StringLiteral) astTree).getIsWrappedDoubleQuotes());
    assertValueAtPath(parsedDirective, "foo", "bar");
  }

  @Test
  public void testFailInvalidUtdEscape() {
    PersistentReporter reporter = reporterAfterParsing("using foo \"\\u004X\"");
    assertDiagnostic(reporter, 0, 16, "invalid character in unicode escape sequence");
  }

  @Test
  public void testFailOctalEscape() {
    PersistentReporter reporter = reporterAfterParsing("using foo \"\\121\"");
    assertDiagnostic(reporter, 0, 10, "octal escape literals are unsupported", "\\u0051");
  }
}
