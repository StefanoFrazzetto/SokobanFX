import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        GameEngineTests.class,
        LevelTests.class,
        GameGridTests.class
})

public class JUnitTestSuite {
}