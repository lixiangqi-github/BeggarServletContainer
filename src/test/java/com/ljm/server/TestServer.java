package com.ljm.server;

import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;

import com.ljm.server.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-01-2018/1/4
 */
public class TestServer extends TestServerBase {
    private static Server server;

    @BeforeClass
    public static void init() {
        ServerConfig serverConfig = new ServerConfig();
        server = ServerFactory.getServer(serverConfig);
    }

    @Test
    public void testServerStart() {
        startServer(server);
        waitServerStart(server);
        assertTrue("服务器启动后，状态是STARTED", server.getStatus().equals(ServerStatus.STARTED));
    }

    @Test
    public void testServerStop() {
        server.stop();
        assertTrue("服务器关闭后，状态是STOPED", server.getStatus().equals(ServerStatus.STOPED));
    }

    @Test
    public void testServerPort() {
        int port = server.getPort();
        assertTrue("默认端口号", ServerConfig.DEFAULT_PORT == port);
    }

}
