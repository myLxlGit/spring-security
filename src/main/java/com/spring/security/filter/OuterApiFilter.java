package com.spring.security.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.security.exception.CustomException;
import com.spring.security.filter.enums.ApiStatus;
import com.spring.security.utils.OuterRequestWrapper;
import com.spring.security.utils.WebUtils;
import com.spring.security.vo.InnerParams;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author lxl
 * @date 2023/9/15 10:52
 */
@Slf4j
@Component
public class OuterApiFilter implements Filter {

    public static final String HEADER_APP_ID = "App-Id";

    public static final String HEADER_REQ_TIME = "Req-Time";

    public static final String HEADER_BODY_SIGN = "Body-Sign";

    private static final long TIME_OFFSET = 60 * 1000;

    @Autowired
    private ObjectMapper ob;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String appId = httpRequest.getHeader(HEADER_APP_ID);
        String sign = httpRequest.getHeader(HEADER_BODY_SIGN);
        String timestamp = httpRequest.getHeader(HEADER_REQ_TIME);

        log.info("App-Id: {}, Req-Time: {},Body-Sign: {}", appId, timestamp, sign);
        if (existsEmpty(appId, timestamp, sign)) {
            log.error("参数不全：App-Id: {}, Req-Time: {},Body-Sign: {}", appId, timestamp, sign);
            responseRender(response, ApiStatus.INVALIDATE);
            return;
        }

        String servletPath = httpRequest.getServletPath();

        //从数据库中查询 这里做一下虚假数据
        String ePath = "erp/api/hello";
//        SysApiKey sysApiKey = apiKeyService.selectKey(appId, servletPath);
        if (!servletPath.equals(ePath)) {
            responseRender(response, ApiStatus.NO_PERMISSION);
            return;
        }

        String secretId = "123456";

        // post put请求
        String method = httpRequest.getMethod();

        if (HttpMethod.POST.name().equals(method) || HttpMethod.PUT.name().equals(method)) {
            OuterRequestWrapper wrapper = new OuterRequestWrapper(httpRequest, request.getParameterMap());
            ServletInputStream inStream = wrapper.getInputStream();
            String contentType = httpRequest.getContentType();
            if (!StringUtils.isEmpty(contentType)) {
                if (contentType.toLowerCase().contains("application/json")) {
                    String body = StreamUtils.copyToString(inStream, StandardCharsets.UTF_8);
                    String text = body + HEADER_APP_ID + appId + HEADER_REQ_TIME + timestamp + secretId;

                    String calcSign = DigestUtils.md5Hex(text).toUpperCase();
                    if (!calcSign.equalsIgnoreCase(sign)) {
                        responseRender(response, ApiStatus.VALIDATE_FAILED);
                    } else {
                        chain.doFilter(wrapper, response);
                    }
                } else if (contentType.toLowerCase().contains("application/x-www-form-urlencoded")) {
                    boolean validateGet = validateGet(request, response, appId, sign, secretId, timestamp, true);
                    if (validateGet) {
                        chain.doFilter(request, response);
                    }
                } else {
                    responseRender(response, ApiStatus.NOT_SUPPORTED);
                }
            } else {
                responseRender(response, ApiStatus.ABNORMAL_REQUEST);
            }
        } else if (HttpMethod.GET.name().equals(method) || HttpMethod.DELETE.name().equals(method)) {
            boolean validateGet = validateGet(request, response, appId, sign, secretId, timestamp, false);
            if (validateGet) {
                chain.doFilter(request, response);
            }
        } else {
            responseRender(response, ApiStatus.NOT_SUPPORTED);
        }
    }


    private boolean validateGet(ServletRequest request, ServletResponse response,
                                String appId, String sign, String secretId, String timestamp, boolean encode)
            throws JsonProcessingException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Set<InnerParams> params = new HashSet<>();
        parameterMap.forEach((k, v) -> params.add(new InnerParams(k, String.join("", v))));
        params.removeIf(innerParams -> HEADER_BODY_SIGN.equals(innerParams.getKey()));
        params.add(new InnerParams(HEADER_APP_ID, appId));
        params.add(new InnerParams(HEADER_REQ_TIME, timestamp));

        String collect = params.stream().sorted(InnerParams::compareTo).map(i -> {
            if (encode) {
                try {
                    return new InnerParams(URLEncoder.encode(i.getKey(), "UTF-8"), URLEncoder.encode(i.getValue(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    throw new CustomException(e);
                }
            } else {
                return i;
            }

        }).map(InnerParams::toString).collect(Collectors.joining());
        //查询securityKey
        collect += secretId;
        String s = DigestUtils.md5Hex(collect).toUpperCase();
        if (!s.equalsIgnoreCase(sign)) {
            responseRender(response, ApiStatus.VALIDATE_FAILED);
            return false;
        }
        return true;
    }


    private void responseRender(ServletResponse response, ApiStatus status) throws JsonProcessingException {
        WebUtils.renderString((HttpServletResponse) response, ob.writeValueAsString(status.ofResult()));
    }

    //判空
    private boolean existsEmpty(String... str) {
        for (String str1 : str) {
            if (!StringUtils.hasText(str1)) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        String appId = "chPtT44vU0vTgmstmR5";
        String timestamp = System.currentTimeMillis() + "";

        String sign = DigestUtils.md5Hex("App-Id" + appId + "Req-Time" + timestamp + "contractNoa1a33295e70648d6b4240b5d34a0b33d9ef8b9b7f9154af189c37a70a81a9d0f");

        System.out.println("时间：" + timestamp);
        System.out.println("APPID：" + appId);

        System.out.println("签名：" + sign);
        String s2 = "https://itrade.sumec.com/prod-api/erp/api/itrade/signing/downCertifyFileBase64?contractNo=a1a33295e70648d6b4240b5d34a0b33d";

        System.out.printf("地址：%s\n", s2);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HEADER_APP_ID, appId);
        httpHeaders.add(HEADER_REQ_TIME, timestamp);
        httpHeaders.add(HEADER_BODY_SIGN, sign);

        HttpEntity<Object> objectHttpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> exchange = restTemplate.exchange(s2, HttpMethod.GET, objectHttpEntity, String.class);
        String re = exchange.getBody();
        System.out.printf("响应体：%s", re);
    }


}
