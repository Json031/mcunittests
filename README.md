# mcunittests (MC Unit Tests)
[![Build Status](https://github.com/Json031/mcunittests/actions/workflows/java-ci.yml/badge.svg?branch=main)](https://github.com/Json031/mcunittests/actions/workflows/java-ci.yml?query=branch%3Amain)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.json031/mcunittests?logo=apache-maven&logoColor=white)](https://search.maven.org/artifact/io.github.json031/mcunittests)
![Codecov](https://img.shields.io/codecov/c/github/Json031/mcunittests?logo=Codecov)
[![Java support](https://img.shields.io/badge/Java-8+-green?logo=java&logoColor=white)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/Json031/mcunittests/blob/main/LICENSE)
<br>
**mcunittests**是一个Maven开源项目，专注于针对Java后端项目进行自动化单元测试，支持API接口、API高并发及业务数据的自动化单元测试。
<br>**mcunittests** is an open-source Maven project focused on automated unit testing for Java backend projects. It supports automated unit testing for API endpoints, high-concurrency API scenarios, and business data validation.

# 最新版本 Latest version
* 方式1️⃣通过maven安装 Install through Maven
```xml
<dependency>
	<groupId>io.github.json031</groupId>
	<artifactId>mcunittests</artifactId>
	<version>1.0.31</version>
	<scope>test</scope>
</dependency>

```

<details>
  <summary>方式2️⃣通过GitHub Packages安装 Install through GitHub Packages</summary>

* 配置 Maven 仓库，在项目的 `pom.xml` 文件的repositories中，添加 GitHub Packages 仓库配置：
<br>Configure the Maven repository and add the GitHub Packages repository configuration in the repositories of the project's pom. xml file

```xml
  <repository>
    <id>github</id>
    <name>GitHub Json031 Apache Maven Packages</name>
    <url>https://maven.pkg.github.com/json031/mcunittests</url>
  </repository>
```

* 添加依赖 Add Dependency
在 pom.xml 中添加以下依赖：
<br>Add the following dependencies in pom.xml:

```
  <dependency>
    <groupId>io.github.json031</groupId>
    <artifactId>mcunittests</artifactId>
    <version>1.0.31</version>
  </dependency>
```

* 配置认证 Configuration authentication
在 ~/.m2/settings.xml 的servers中，配置 GitHub 的认证信息：
<br>In ~/.m2/settings.xml servers tag, configure GitHub authentication information:

```
  <server>
    <id>github</id>
    <username>your-github-username</username>
    <password>your-github-TOKEN</password>
  </server>
```

</details>

## 示例代码 Example Codes
```
import io.github.json031.apitests.MCApiTests;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class YourProjectApplicationTests {

	private MCApiTests mcApiTests = new MCApiTests();

	@Test
	public void testExampleApi() {
		// 测试api接口地址 Replace with actual path
		String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df"; 
		Map<String, Object> param = new HashMap<>();
		param.put("id", 2);
		//期望最慢响应时间 Expected slowest response time
		long timeoutSeconds = 5;
		mcApiTests.assertApiRespondsWithinTimeout(apiUrl, HttpMethod.GET, param, null, timeoutSeconds, true);
	}

	@Test
	public void testApiWithHighConcurrency() {
		String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df";  // 替换为实际路径
		Map<String, Object> param = new HashMap<>();
		param.put("id", 2);
		long timeoutSecondsMillis = 1000;
		int threadCount = 1000;
		HighConcurrencyResult highConcurrencyResult = this.mcHighConcurrencyTests.highConcurrencyTestWithTimeoutMillis(apiUrl, threadCount, HttpMethod.GET, param, null, timeoutSecondsMillis, true);
		System.out.print("highConcurrencyResult:" + highConcurrencyResult.toString());
	}

	@Test
	public void testIsApiValidJson() {
		String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df"; // 替换为实际路径
		Map<String, Object> param = new HashMap<>();
		param.put("id", 2);
		this.mcApiTests.testApiReturnsValidJson(apiUrl, HttpMethod.GET, param, null,  true);
	}
}

```
![20250425110521902](https://github.com/user-attachments/assets/e8024cb3-d27f-46a6-9f01-be3ca6f96ef3)

# License
This library is licensed under the [MIT License](https://github.com/Json031/mcunittests/blob/main/LICENSE).
