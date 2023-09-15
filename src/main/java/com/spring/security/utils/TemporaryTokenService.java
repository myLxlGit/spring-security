package com.spring.security.utils;

import com.spring.security.domain.LoginUser;
import com.spring.security.redis.RedisCache;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static com.spring.security.jwt.TokenResolver.LOGIN_TOKEN_KEY;

@Service
public class TemporaryTokenService {

    @Autowired
    private RedisCache cache;


    private static final String keyText = "d2d8ba186d24062929a73d301292b667";

    private static final Logger log = LoggerFactory.getLogger(TemporaryTokenService.class);

    public String createToken() throws GeneralSecurityException {
        try {
            String token = SecurityUtils.getLoginUser().getToken();
            String s = DateUtils.parseDateToStr(DateUtils.getAfter(3L, ChronoUnit.MINUTES));
            String mixToken = token + "," + s;
            byte[] bytes = Sm4Utils.encryptEcbPkcs7Padding(mixToken.getBytes(StandardCharsets.UTF_8), Hex.decode(keyText));
            return Hex.toHexString(bytes);
        } catch (Exception e) {
            log.error("组装临时Token失败：{}", e.getMessage(), e);
            throw e;
        }
    }

    public boolean parseToken(String ticket) {
        try {
            byte[] bytes = Sm4Utils.decryptEcbPkcs7Padding(Hex.decode(ticket), Hex.decode(keyText));
            String originToken = new String(bytes, StandardCharsets.UTF_8);
            String[] split = originToken.split(",");
            String s = LOGIN_TOKEN_KEY + split[0];
            LoginUser loginUser = cache.getCacheObject(s);
            Date nowDate = DateUtils.getNowDate();
            if (loginUser == null || loginUser.getExpireTime() < nowDate.getTime()) {
                return false;
            }
            Date date = DateUtils.parseDate(split[1]);
            assert date != null;
            return !date.before(nowDate);
        } catch (Exception e) {
            log.error("解析临时Token失败：{}", e.getMessage(), e);
            return false;
        }
    }

    public LoginUser getLoginUserByTicket(String ticket) {
        try {
            byte[] bytes = Sm4Utils.decryptEcbPkcs7Padding(Hex.decode(ticket), Hex.decode(keyText));
            String originToken = new String(bytes, StandardCharsets.UTF_8);
            String[] split = originToken.split(",");
            String s = LOGIN_TOKEN_KEY + split[0];
            return cache.getCacheObject(s);
        } catch (Exception e) {
            log.error("解析临时Token失败：{}", e.getMessage(), e);
            return null;
        }
    }
}
