package com.olujobii.config;

import com.google.gson.JsonSyntaxException;
import com.olujobii.model.Criteria;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test for CriteriaParser")
public class ConfigLoaderTest {

    @BeforeAll
    static void setupResources() throws IOException {
        Path path = Path.of("src/test/resources/empty-criteria.json");
        Files.deleteIfExists(path);
        Files.createFile(path);
    }

    @AfterAll
    static void cleanup() throws IOException{
        Path path = Path.of("src/test/resources/empty-criteria.json");
        Files.deleteIfExists(path);
    }

    @Test
    @DisplayName("Testing when criteria file path is read successfully")
    void testReadConfigFile_whenFilePathIsRead_shouldReturnValidCriteria() throws IOException {
        String filePath = "src/test/resources/criteria.json";

        Criteria criteria = ConfigLoader.readConfigFile(filePath);

        assertNotNull(criteria, "criteria should not be null");
        assertEquals(2, criteria.forbiddenWords().size());
        assertTrue(criteria.professionalCheck());
        assertTrue(criteria.tone());
        assertTrue(criteria.excludePolitics());
    }

    @Test
    @DisplayName("Testing when criteria file path does not exist")
    void testReadConfigFile_whenFilePathDoesNotExist_shouldThrowIOException(){
        String filePath = "src/test/resources/mock-criteria.json";

        assertThrows(IOException.class, () -> ConfigLoader.readConfigFile(filePath));
    }

    @Test
    @DisplayName("Testing when criteria file path is not a valid JSON structure")
    void testReadConfigFile_whenFilePathIsNotAValidJsonStructure_shouldThrowJsonSyntaxException(){
        String filePath = "src/test/resources/malformed-criteria.json";

        assertThrows(JsonSyntaxException.class, () -> ConfigLoader.readConfigFile(filePath));
    }

    @Test
    @DisplayName("Testing when criteria is null")
    void testReadConfigFile_whenCriteriaIsNull_shouldThrowMalformedCriteriaFileException() {
        String filePath = "src/test/resources/empty-criteria.json";

        assertThrows(NullPointerException.class, () -> ConfigLoader.readConfigFile(filePath));
    }
}
