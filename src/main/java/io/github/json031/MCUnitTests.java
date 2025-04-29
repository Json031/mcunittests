package io.github.json031;

public class MCUnitTests {

    /**
     * 单例实例
     */
    private static final MCUnitTests INSTANCE = new MCUnitTests();

    public boolean verbose = true;

    /**
     * 私有构造函数
     */
    private MCUnitTests() {
        this.verbose = true;
    }

    /**
     * 获取单例
     */
    public static MCUnitTests getInstance() {
        return INSTANCE;
    }
}
