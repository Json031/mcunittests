package io.github.json031;

import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.unittests.RequestUnitTests;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;

import static org.junit.jupiter.api.Assertions.*;

public class ExcludedTests {

    @Test
    public void testRequestUnitTests() {
        RequestUnitTestsResult fakeres = RequestUnitTests.getInstance().requestWitRestTemplate("url", HttpMethod.GET, null, null, false);
        assertNull(fakeres);
    }
}
