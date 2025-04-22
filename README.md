# mcunittests
A unit test library for SpringBoot project.

## Example Codes
```
import io.github.json031.MCApiTests;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YourProjectApplicationTests {

	private MCApiTests mcApiTests = new MCApiTests();

	@Test
	public void testExampleApi() {
		String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df?id=2"; // Replace with actual path
		long timeoutSeconds = 5;
		mcApiTests.assertApiRespondsWithinTimeout(apiUrl, timeoutSeconds);
	}

}

```
