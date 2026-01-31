# mcunittests (MC Unit Tests)
<a name="top"></a>

[![Build Status](https://github.com/Json031/mcunittests/actions/workflows/java-ci.yml/badge.svg?branch=main)](https://github.com/Json031/mcunittests/actions/workflows/java-ci.yml?query=branch%3Amain)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.json031/mcunittests?logo=apache-maven&logoColor=white)](https://search.maven.org/artifact/io.github.json031/mcunittests)
![Codecov](https://codecov.io/github/json031/mcunittests/coverage.svg?branch=main)
[![Java support](https://img.shields.io/badge/Java-8+-green?logo=java&logoColor=white)](https://openjdk.java.net/)
[![License](https://img.shields.io/badge/license-MIT-brightgreen.svg)](https://github.com/Json031/mcunittests/blob/main/LICENSE)
![Codecov](https://img.shields.io/codecov/c/github/Json031/mcunittests?logo=Codecov)

---

## 🌍 Language / 语言选择

**[English](#english)** | **[中文](#中文)**

---

## 📖 English
<a name="english"></a>

### Overview

**mcunittests** is an open-source Maven project focused on automated unit testing for Java backend projects. It supports automated unit testing for API endpoints, high-concurrency API scenarios, and business data validation.

### Installation

**Latest Version:** `1.0.31`

#### Method 1: Install via Maven Central (Recommended)

Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.json031</groupId>
    <artifactId>mcunittests</artifactId>
    <version>1.0.31</version>
    <scope>test</scope>
</dependency>
```

<details>
  <summary>Method 2: Install via GitHub Packages</summary>

**Step 1: Configure Maven Repository**

Add the GitHub Packages repository configuration in the `repositories` section of your project's `pom.xml`:

```xml
<repository>
    <id>github</id>
    <name>GitHub Json031 Apache Maven Packages</name>
    <url>https://maven.pkg.github.com/json031/mcunittests</url>
</repository>
```

**Step 2: Add Dependency**

Add the following dependency in `pom.xml`:

```xml
<dependency>
    <groupId>io.github.json031</groupId>
    <artifactId>mcunittests</artifactId>
    <version>1.0.31</version>
</dependency>
```

**Step 3: Configure Authentication**

Configure GitHub authentication information in the `servers` section of `~/.m2/settings.xml`:

```xml
<server>
    <id>github</id>
    <username>your-github-username</username>
    <password>your-github-TOKEN</password>
</server>
```

</details>

### Usage Examples

#### Basic API Test

```java
import io.github.json031.apitests.MCApiTests;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class YourProjectApplicationTests {

    private MCApiTests mcApiTests = new MCApiTests();

    @Test
    public void testExampleApi() {
        // Replace with actual API endpoint
        String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df";
        Map<String, Object> param = new HashMap<>();
        param.put("id", 2);
        // Expected slowest response time (in seconds)
        long timeoutSeconds = 5;
        mcApiTests.assertApiRespondsWithinTimeout(apiUrl, HttpMethod.GET, param, null, timeoutSeconds, true);
    }
}
```

#### High Concurrency API Test

```java
@Test
public void testApiWithHighConcurrency() {
    String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df";
    Map<String, Object> param = new HashMap<>();
    param.put("id", 2);
    long timeoutSecondsMillis = 1000;
    int threadCount = 1000;
    HighConcurrencyResult highConcurrencyResult = this.mcHighConcurrencyTests.highConcurrencyTestWithTimeoutMillis(
        apiUrl, threadCount, HttpMethod.GET, param, null, timeoutSecondsMillis, true
    );
    System.out.print("highConcurrencyResult:" + highConcurrencyResult.toString());
}
```

#### JSON Validation Test

```java
@Test
public void testIsApiValidJson() {
    String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df";
    Map<String, Object> param = new HashMap<>();
    param.put("id", 2);
    this.mcApiTests.testApiReturnsValidJson(apiUrl, HttpMethod.GET, param, null, true);
}
```

### Test Results

![Test Results](https://github.com/user-attachments/assets/e8024cb3-d27f-46a6-9f01-be3ca6f96ef3)

### License

This library is licensed under the [MIT License](https://github.com/Json031/mcunittests/blob/main/LICENSE).

---

## 📖 中文
<a name="中文"></a>

### 项目简介

**mcunittests** 是一个 Maven 开源项目，专注于针对 Java 后端项目进行自动化单元测试，支持 API 接口、API 高并发及业务数据的自动化单元测试。

### 安装

**最新版本：** `1.0.31`

#### 方式 1：通过 Maven Central 安装（推荐）

在 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>io.github.json031</groupId>
    <artifactId>mcunittests</artifactId>
    <version>1.0.31</version>
    <scope>test</scope>
</dependency>
```

<details>
  <summary>方式 2：通过 GitHub Packages 安装</summary>

**步骤 1：配置 Maven 仓库**

在项目的 `pom.xml` 文件的 `repositories` 中，添加 GitHub Packages 仓库配置：

```xml
<repository>
    <id>github</id>
    <name>GitHub Json031 Apache Maven Packages</name>
    <url>https://maven.pkg.github.com/json031/mcunittests</url>
</repository>
```

**步骤 2：添加依赖**

在 `pom.xml` 中添加以下依赖：

```xml
<dependency>
    <groupId>io.github.json031</groupId>
    <artifactId>mcunittests</artifactId>
    <version>1.0.31</version>
</dependency>
```

**步骤 3：配置认证**

在 `~/.m2/settings.xml` 的 `servers` 中，配置 GitHub 的认证信息：

```xml
<server>
    <id>github</id>
    <username>your-github-username</username>
    <password>your-github-TOKEN</password>
</server>
```

</details>

### 使用示例

#### 基础 API 测试

```java
import io.github.json031.apitests.MCApiTests;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class YourProjectApplicationTests {

    private MCApiTests mcApiTests = new MCApiTests();

    @Test
    public void testExampleApi() {
        // 测试 API 接口地址
        String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df";
        Map<String, Object> param = new HashMap<>();
        param.put("id", 2);
        // 期望最慢响应时间（秒）
        long timeoutSeconds = 5;
        mcApiTests.assertApiRespondsWithinTimeout(apiUrl, HttpMethod.GET, param, null, timeoutSeconds, true);
    }
}
```

#### 高并发 API 测试

```java
@Test
public void testApiWithHighConcurrency() {
    String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df";
    Map<String, Object> param = new HashMap<>();
    param.put("id", 2);
    long timeoutSecondsMillis = 1000;
    int threadCount = 1000;
    HighConcurrencyResult highConcurrencyResult = this.mcHighConcurrencyTests.highConcurrencyTestWithTimeoutMillis(
        apiUrl, threadCount, HttpMethod.GET, param, null, timeoutSecondsMillis, true
    );
    System.out.print("highConcurrencyResult:" + highConcurrencyResult.toString());
}
```

#### JSON 验证测试

```java
@Test
public void testIsApiValidJson() {
    String apiUrl = "http://localhost:8088/json031/c/2a-d7b4-8005-a16f-8a95f07011df";
    Map<String, Object> param = new HashMap<>();
    param.put("id", 2);
    this.mcApiTests.testApiReturnsValidJson(apiUrl, HttpMethod.GET, param, null, true);
}
```

### 测试结果

![测试结果](https://github.com/user-attachments/assets/e8024cb3-d27f-46a6-9f01-be3ca6f96ef3)

### 许可证

本项目基于 [MIT License](https://github.com/Json031/mcunittests/blob/main/LICENSE) 开源协议。

---

**[⬆ Back to Top / 返回顶部](#top)**
