package searchengine;
import org.junit.Rule;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;

import java.io.FileNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Kim Ida Schild
 * @author Matthias Giovanni Moller
 * @author Frederik Wonsild
 * @author Philine Zeinert
 */
class FileHelperTest {


    // Rule needed for testing FileNotFoundException
    @Rule
    public ExpectedException thrown= ExpectedException.none();

    // Test that FileNotFoundException is thrown when a wrong file name is given to the FileHelper.
    @Test
    void FileNotFoundException() {
        thrown.expect(FileNotFoundException.class);
        List<Website> sites = FileHelper.parseFile("wrong filename");
    }

    // Test that a correct input is handled as expected
    @Test
    void parseGoodFile() {
        List<Website> sites = FileHelper.parseFile("data/test-file.txt");
        assertEquals(2, sites.size());
        assertEquals("title1", sites.get(0).getTitle());
        assertEquals("title2", sites.get(1).getTitle());
        assertTrue(sites.get(0).containsWord("word1"));
        assertFalse(sites.get(0).containsWord("word3"));
    }

    // Test that invalid input is handled as expected
    @Test
    void parseBadFile() {
        List<Website> sites = FileHelper.parseFile("data/test-file-errors.txt");
        assertEquals(2, sites.size());
        assertEquals("title1", sites.get(0).getTitle());
        assertEquals("title2", sites.get(1).getTitle());
        assertTrue(sites.get(0).containsWord("word1"));
        assertFalse(sites.get(0).containsWord("word3"));
    }
}
