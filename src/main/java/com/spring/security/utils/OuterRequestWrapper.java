package com.spring.security.utils;

import org.springframework.http.HttpMethod;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * @author qixlin
 * @date 2021/03/30 16:08
 */
public class OuterRequestWrapper extends HttpServletRequestWrapper {

    private final Map<String, String[]> params = new HashMap<>(16);

    private byte[] requestBody;

    public OuterRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        if (request.getMethod().equalsIgnoreCase(HttpMethod.POST.name()) || request.getMethod().equalsIgnoreCase(HttpMethod.PUT.name())) {
            requestBody = StreamUtils.copyToByteArray(request.getInputStream());
        }
    }

    public OuterRequestWrapper(HttpServletRequest request, Map<String, String[]> params) throws IOException {
        this(request);
        if (params != null) {
            this.params.putAll(params);
        }
    }

    @Override
    public ServletInputStream getInputStream() {
        if (requestBody == null) {
            requestBody = new byte[0];
        }

        return new ServletInputStream() {
            private boolean finished = false;
            final InputStream sourceStream = new ByteArrayInputStream(requestBody);
            @Override
            public boolean isFinished() {
                return finished;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public int available() throws IOException {
                return sourceStream.available();
            }

            @Override
            public void close() throws IOException {
                super.close();
                sourceStream.close();
            }

            @Override
            public int read() throws IOException {
                int data = sourceStream.read();
                if (data == -1) finished = true;
                return data;
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }


//    @Override
//    public String getParameter(String name) {
//        String[] parameter;
//        if (ObjectUtils.isNotEmpty(parameter = params.get(name))) {
//            return parameter[0];
//        }
//        return null;
//    }

    @Override
    public String[] getParameterValues(String name) {
        return params.get(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }
}
