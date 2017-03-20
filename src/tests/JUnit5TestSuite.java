package tests;

import engine.SokoEngine;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import java.io.File;
import java.nio.file.Files;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        SokoEngineTests.class
})

public class JUnit5TestSuite
{
    static SokoEngine engine;

    @BeforeAll
    public static void setUp() throws Exception {
        File folder = new File(System.getProperty("user.dir") + "\\src\\level\\debugGame.skb");
        if (!Files.exists(folder.toPath())) {
            folder = new File(System.getProperty("user.dir"));
        }

        File saveFile = new File(folder + "/SampleGame.skb");
        engine = new SokoEngine(saveFile);
    }
}
