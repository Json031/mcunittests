# mcunittests
mcunittests是SpringBoot项目的一个单元测试库，是一个在MIT许可下分发的开源项目。源代码可以在GitHub上找到。
<br>mcunittests is a unit test library for SpringBoot project, and is an open source project distributed under the liberal MIT license. The source code is available on GitHub.

# 最新版本 Latest version

```xml

<dependency>
    <groupId>io.github.json031</groupId>
    <artifactId>mcunittests</artifactId>
    <version>1.0.0</version>
    <scope>test</scope>
</dependency>
```

## 示例代码 Example Codes
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
