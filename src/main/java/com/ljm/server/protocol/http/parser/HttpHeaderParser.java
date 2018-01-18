package com.ljm.server.protocol.http.parser;

import com.ljm.server.protocol.http.header.HttpMessageHeaders;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

/**
 * @author 李佳明 https://github.com/pkpk1234
 * @date 2018-01-2018/1/14
 */
public interface HttpHeaderParser {
    HttpMessageHeaders parser() throws UnsupportedEncodingException;
}
