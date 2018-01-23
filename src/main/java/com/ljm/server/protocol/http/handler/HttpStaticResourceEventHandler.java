package com.ljm.server.protocol.http.handler;

import com.ljm.server.demo.FileTransfer;
import com.ljm.server.event.handler.HandlerException;
import com.ljm.server.io.connection.Connection;
import com.ljm.server.protocol.http.HttpRequestMessage;
import com.ljm.server.protocol.http.HttpResponseMessage;
import com.ljm.server.protocol.http.ResponseLine;
import com.ljm.server.protocol.http.body.HttpBody;
import com.ljm.server.protocol.http.header.HttpMessageHeaders;
import com.ljm.server.protocol.http.parser.AbstractHttpRequestMessageParser;
import com.ljm.server.protocol.http.response.HttpResponseConstants;
import com.ljm.server.protocol.http.response.HttpResponseMessageWriter;
import com.ljm.server.protocol.http.response.ResponseLineConstants;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-01-2018/1/8
 */
public class HttpStaticResourceEventHandler extends AbstractHttpEventHandler {

    private final String docBase;
    private final AbstractHttpRequestMessageParser httpRequestMessageParser;

    public HttpStaticResourceEventHandler(String docBase,
                                          AbstractHttpRequestMessageParser httpRequestMessageParser) {
        this.docBase = docBase;
        this.httpRequestMessageParser = httpRequestMessageParser;
    }

    @Override
    protected HttpRequestMessage doParserRequestMessage(Connection connection) {
        HttpRequestMessage httpRequestMessage = null;
        try {
            httpRequestMessage = httpRequestMessageParser
                    .parse(connection.getInputStream());
        } catch (IOException e) {
            throw new HandlerException(e);
        }
        return httpRequestMessage;
    }

    @Override
    protected HttpResponseMessage doGenerateResponseMessage(
            HttpRequestMessage httpRequestMessage) {
        String path = httpRequestMessage.getRequestLine().getRequestURI().getPath();
        Path filePath = Paths.get(docBase, path);
        if (Files.isDirectory(filePath)) {
            return HttpResponseConstants.HTTP_404;
        } else {
            ResponseLine ok = ResponseLineConstants.RES_200;
            // TODO: 添加正确的Content-Type
            HttpMessageHeaders headers = HttpMessageHeaders.newBuilder()
                    .addHeader("status", "200").build();
            HttpBody httpBody = null;
            try {
                httpBody = new HttpBody(new FileInputStream(filePath.toFile()));
            } catch (FileNotFoundException e) {
                return HttpResponseConstants.HTTP_404;
            }
            HttpResponseMessage httpResponseMessage = new HttpResponseMessage(ok, headers,
                    Optional.ofNullable(httpBody));
            return httpResponseMessage;
        }

    }

    @Override
    protected void doTransferToClient(HttpResponseMessage responseMessage,
                                      Connection connection) throws IOException {
        HttpResponseMessageWriter httpResponseMessageWriter = new HttpResponseMessageWriter();
        httpResponseMessageWriter.write(responseMessage, connection);
    }

}
