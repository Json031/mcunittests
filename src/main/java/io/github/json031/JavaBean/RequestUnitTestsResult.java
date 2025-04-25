package io.github.json031.JavaBean;

import org.springframework.http.ResponseEntity;

/**
 * Request Unit Tests Result.
 */
public class RequestUnitTestsResult {
    // 毫秒级耗时
    public final long durationMillis;
    // api请求响应结果
    public final ResponseEntity<String> response;

    public RequestUnitTestsResult(long durationMillis, ResponseEntity<String> response) {
        this.durationMillis = durationMillis;
        this.response = response;
    }
}
