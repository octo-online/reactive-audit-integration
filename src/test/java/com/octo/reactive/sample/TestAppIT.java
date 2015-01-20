/**
 *
 */
package com.octo.reactive.sample;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * @author Yacine
 *
 */
@Category(com.octo.reactive.sample.IntegrationTest.class)
public class TestAppIT {

    private static final Pattern LINE_HEADER_PATTERN = Pattern.compile("(\\Qinfo   : Use reactive audit with \\E.*\\Q at \\E)\\d{2}\\:\\d{2}\\:\\d{2} \\w{3} \\d{4}");
    private static final String LINE_HEADER_REPLACEMENT = "$1HH:MM:SS CET YYYY";

    @Test
    public void testAuditOutput() throws IOException {
        compareFiles("OK-reactiveAuditConsole.log", System.getProperty("consoleOutputFilePath"), System.getProperty("buildDirPath"));
    }

    @Test
    public void testConsoleOutput() throws IOException {
        compareFiles("OK-audit.log", System.getProperty("auditFilePath"), System.getProperty("buildDirPath"));
    }

    private void compareFiles(String expectedFileName, String actualFileName, String buildDirPath) throws IOException {
        final File expectedFile = FileUtils.getFile(buildDirPath, expectedFileName);
        Assert.assertTrue(expectedFile.exists());
        final File outputFile = FileUtils.getFile(actualFileName);
        Assert.assertTrue(outputFile.exists());

        final String expectedContent = preprocess(expectedFile);
        final String outputContent = preprocess(outputFile);

        Assert.assertEquals("The files are different!", DigestUtils.sha1Hex(expectedContent), DigestUtils.sha1Hex(outputContent));
    }

    private String preprocess(final File file) throws IOException {
        final String content = FileUtils.readFileToString(file);
        final String newContent = content.replaceAll(LINE_HEADER_PATTERN.pattern(), LINE_HEADER_REPLACEMENT);
        return newContent;
    }

}
