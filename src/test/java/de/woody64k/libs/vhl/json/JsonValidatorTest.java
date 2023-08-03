package de.woody64k.libs.vhl.json;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import de.woody64k.libs.vhl.files.VhlFileUtils;

public class JsonValidatorTest {

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    @Test
    public void test() {
        File file = VhlFileUtils.findFileOnClasspath("json/test-json.json");
        List<String> results = JsonValidator.validate(file, TestJson.class);
        assertTrue(results.size() == 1);
        assertTrue(results.get(0).equals("root.addresses[1].fail"));
    }

}
