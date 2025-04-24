# mcunittests
[![Maven Central](https://img.shields.io/maven-central/v/io.github.json031/mcunittests?logo=apache-maven&logoColor=white)](https://search.maven.org/artifact/io.github.json031/mcunittests)
[![Java support](https://img.shields.io/badge/Java-8+-green?logo=java&logoColor=white)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/Json031/mcunittests/blob/main/LICENSE)
<br>
mcunittests是SpringBoot项目的一个单元测试库，支持API接口自动化单元测试，API接口高并发单元测试。
<br>Mcunittests is a unit testing library for the SpringBoot project that supports automated unit testing of API interfaces and high concurrency unit testing of API interfaces.

# 最新版本 Latest version
* 通过maven安装
```xml
<dependency>
	<groupId>io.github.json031</groupId>
	<artifactId>mcunittests</artifactId>
	<version>1.0.1</version>
	<scope>test</scope>
</dependency>

```

* 通过GitHub Packages安装
配置 Maven 仓库，在项目的 `pom.xml` 文件的repositories中，添加 GitHub Packages 仓库配置：

```xml
  <repository>
    <id>github</id>
    <name>GitHub Json031 Apache Maven Packages</name>
    <url>https://maven.pkg.github.com/json031/mcunittests</url>
  </repository>
```

添加依赖
在 pom.xml 中添加以下依赖：

```
  <dependency>
    <groupId>io.github.json031</groupId>
    <artifactId>mcunittests</artifactId>
    <version>1.0.1</version>
  </dependency>
```

配置认证
在 ~/.m2/settings.xml 的servers中，配置 GitHub 的认证信息：
```
  <server>
    <id>github</id>
    <username>your-github-username</username>
    <password>your-github-TOKEN</password>
  </server>
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
		// 测试api接口地址 Replace with actual path
		String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df?id=2"; 
		//期望最慢响应时间 Expected slowest response time
		long timeoutSeconds = 5;
		mcApiTests.assertApiRespondsWithinTimeout(apiUrl, timeoutSeconds);
	}

}

```
