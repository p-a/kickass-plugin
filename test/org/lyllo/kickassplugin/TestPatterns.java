package org.lyllo.kickassplugin;

import java.util.regex.Matcher;

import org.testng.Assert;
import org.testng.annotations.Test;

@Test
public class TestPatterns {

    @Test
    public void testNewImportRegex() throws Exception {
       Assert.assertTrue(Patterns.IMPORT_PATTERN.matcher("#import \"tjo.inc\"").matches());
    }
    
    @Test
    public void testNewImportRegexBad() throws Exception {
       Assert.assertFalse(Patterns.IMPORT_PATTERN.matcher("#import bla \"tjo.inc\"").matches());
       Assert.assertFalse(Patterns.IMPORT_PATTERN.matcher("#import tjo.inc\"").matches());
    }
    
    
    @Test
    public void testImportifRegex() throws Exception {
       Matcher m = Patterns.IMPORTIF_PATTERN.matcher("#importif BLA_BLA \"tjo.inc\"");
       Assert.assertTrue(m.matches());
       Assert.assertEquals(m.group(m.groupCount()), "tjo.inc");
    }
    

    @Test
    public void testImportifRegexBad() throws Exception {
       Assert.assertFalse(Patterns.IMPORTIF_PATTERN.matcher("#importif BLA_BLA \"tjo.inc").matches());
    }


    @Test
    public void testImportifRegexMulti() throws Exception {
       Matcher m = Patterns.IMPORTIF_PATTERN.matcher("#importif BLA_BLA && tjotjo || !bla \"tjo.inc\"");
       Assert.assertTrue(m.matches());
       Assert.assertEquals(m.group(m.groupCount()), "tjo.inc");
    }

}
