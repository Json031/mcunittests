package io.github.json031;

import io.github.json031.JavaBean.RequestUnitTestsResult;
import io.github.json031.apitests.MCApiTests;
import io.github.json031.apitests.MCHighConcurrencyTests;
import io.github.json031.unittests.DataUnitTests;
import io.github.json031.unittests.RequestUnitTests;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ExcludedTests {

    @Test
    public void testRequestUnitTests() {
        RequestUnitTestsResult fakeres = RequestUnitTests.getInstance().requestWitRestTemplate("url", HttpMethod.GET, null, null, false);
        assertNull(fakeres);
    }
}
