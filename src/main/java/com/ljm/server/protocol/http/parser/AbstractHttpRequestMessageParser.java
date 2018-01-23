package com.ljm.server.protocol.http.parser;

import com.ljm.server.protocol.http.HttpQueryParameters;
import com.ljm.server.protocol.http.HttpRequestMessage;
import com.ljm.server.protocol.http.RequestLine;
import com.ljm.server.protocol.http.body.HttpBody;
import com.ljm.server.protocol.http.header.IMessageHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-01-2018/1/14
 */
public abstract class AbstractHttpRequestMessageParser extends AbstractParser implements HttpRequestMessageParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpRequestMessageParser.class);

    /**
     * 定义parse流程
     *
     * @return
     */
    @Override
    public HttpRequestMessage parse(InputStream inputStream) throws IOException {
        //1.设置上下文
        getAndSetBytesBeforeBodyToContext(inputStream);
        //2.解析构造RequestLine
        RequestLine requestLine = parseRequestLine();
        //3.解析构造QueryParameters
        HttpQueryParameters httpQueryParameters = parseHttpQueryParameters();
        //4.解析构造HTTP请求头，并设置是否有body，如果有则body段之前byte长度到上下文中
        IMessageHeaders messageHeaders = parseRequestHeaders();
        //5.解析构造HTTP Body，如果有个的话
        Optional<HttpBody> httpBody = parseRequestBody();

        HttpRequestMessage httpRequestMessage = new HttpRequestMessage(requestLine, messageHeaders, httpBody, httpQueryParameters);
        return httpRequestMessage;
    }

    /**
     * 读取请求发送的数据，并保存为byte数组设置到解析上下文中
     *
     * @param inputStream
     * @throws IOException
     */
    private void getAndSetBytesBeforeBodyToContext(InputStream inputStream) throws IOException {
        byte[] bytes = copyRequestBytesBeforeBody(inputStream);
        HttpParserContext.setHttpMessageBytes(bytes);
        HttpParserContext.setBytesLengthBeforeBody(bytes.length);
    }

    /**
     * 解析并构建RequestLine
     *
     * @return
     */
    protected abstract RequestLine parseRequestLine() throws UnsupportedEncodingException;

    /**
     * 解析并构建HTTP请求Headers集合
     *
     * @return
     */
    protected abstract IMessageHeaders parseRequestHeaders() throws UnsupportedEncodingException;

    /**
     * 解析并构建HTTP 请求Body
     *
     * @return
     */
    protected abstract Optional<HttpBody> parseRequestBody();

    /**
     * 解析并构建QueryParameter集合
     *
     * @return
     */
    protected abstract HttpQueryParameters parseHttpQueryParameters();

    private byte[] copyRequestBytesBeforeBody(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(inputStream.available());
        int i = -1;

        byte[] temp = new byte[3];
        while ((i = inputStream.read()) != -1) {
            byteArrayOutputStream.write(i);
            if ((char) i == '\r') {
                int len = inputStream.read(temp, 0, temp.length);
                byteArrayOutputStream.write(temp, 0, len);
                if (new String(temp).equals("\n\r\n")) {
                    break;
                }
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
}
