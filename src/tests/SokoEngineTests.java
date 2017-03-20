package tests;

import engine.SokoEngine;
import org.junit.jupiter.api.Test;

import static junit.framework.TestCase.assertTrue;

public class SokoEngineTests {

    @Test
    public void testConstructor() {
        SokoEngine engine = JUnit5TestSuite.engine;
        assertTrue(engine != null);
    }
}
