package com.spring.security.filter;

import com.spring.security.service.impl.IAuthService;
import com.spring.security.utils.SpringUtils;
import com.spring.security.vo.CrmAuthToken;
import com.spring.security.vo.LoginMode;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 自定义身份验证逻辑
 *
 * @author qixlin
 * @date 2021/06/23 12:55
 */
public class SecAuthProvider implements AuthenticationProvider {

    //AuthenticationProvider
    //认证是由 AuthenticationManager 来管理的，
    // 但是真正进行认证的是 AuthenticationManager 中定义的 AuthenticationProvider。
    // AuthenticationManager 中可以定义有多个 AuthenticationProvider。
    // 当我们使用 authentication-provider 元素来定义一个 AuthenticationProvider 时，
    // 如果没有指定对应关联的 AuthenticationProvider 对象，
    // Spring Security 默认会使用 DaoAuthenticationProvider。
    // DaoAuthenticationProvider 在进行认证的时候需要一个 UserDetailsService 来获取用户的信息 UserDetails，
    // 其中包括用户名、密码和所拥有的权限等。
    // 所以如果我们需要改变认证的方式，我们可以实现自己的 AuthenticationProvider；
    // 如果需要改变认证的用户信息来源，我们可以实现 UserDetailsService。
    //实现了自己的 AuthenticationProvider 之后，
    // 我们可以在配置文件中这样配置来使用我们自己的 AuthenticationProvider。
    // 其中 SecAuthProvider 就是我们自己的 AuthenticationProvider 实现类对应的 bean。

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(SecAuthProvider.class);

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        CrmAuthToken authToken = (CrmAuthToken) authentication;
        LoginMode mode = authToken.getMode();
        try {
//            List<String> strings = Arrays.asList(SpringUtils.getActiveProfiles());
//            if ((strings.contains("prod")) && mode == LoginMode.AC) {
//                throw new BadCredentialsException("用户名密码登录临时停用，请使用短信验证码登录");
//            }
//            if (Objects.isNull(mode)) {
//                throw new BadCredentialsException("不支持的登录方式");
//            }

            //获取登录模式的实现类
            IAuthService authService = SpringUtils.getBean(mode.getServiceClass());
//            IAuthService authService = SpringUtils.getBean(UserDetailsServiceImpl.class);

//            if (mode.isNeedCode()) {
//                authToken = authService.preCheck(authToken);
//            }

            UserDetails userDetails = authService.loadUserByUsername((String) authToken.getPrincipal());
            //检验数据是否正确的回调
            authService.afterCall(userDetails, authToken);

//            SysApiMenuService apiMenuService = SpringUtils.getBean(SysApiMenuService.class);
//            CompatibleUserService compatibleUserService = SpringUtils.getBean(CompatibleUserService.class);
//            List<CompatibleMenuDTO> me = compatibleUserService.selectMenu(((LoginUser) userDetails).getUser().getUserId());
//            ((LoginUser) userDetails).setApiKey(apiMenuService.getApiPermKeyList(me.stream().map(CompatibleMenuDTO::getMenuId).collect(Collectors.toList())));

//            if (authToken.getMode().equals(LoginMode.AC)) {
//                if (!PasswordUtils.strength(((String)authToken.getCredentials()))) {
//                    RedisCache bean = SpringUtils.getBean(RedisCache.class);
//                    bean.setCacheMapValue(Constants.WEAK_PASSWORD_USER_LIST, ((LoginUser)userDetails).getUser().getUserId(), "0");
//                }
//            }
            CrmAuthToken newAuthToken = new CrmAuthToken(userDetails, userDetails.getAuthorities());
            newAuthToken.setDetails(authToken.getDetails());
            newAuthToken.setSourceToken(authToken);
            return newAuthToken;

        } catch (Exception e) {
//            LogFactory.loginFailure(mode, (String) authentication.getPrincipal(), e.getMessage());
            log.error("登录失败，原因：{}", e.getMessage());
//            ThirdLoginServiceImpl.wxLoginSource.remove();
            throw new InternalAuthenticationServiceException(e.getMessage());
        }
    }

    //我就直接说了吧，supports方法是用来判断该对象表示的类或接口是否与指定的类参数表示的类或接口相同，
    //或者是其超类或超接口（一个叫isAssignableFrom的本地方法判断的）。如果是，则返回true；否则返回false
    @Override
    public boolean supports(Class<?> authentication) {
        //确定某个AuthenticationProvider是否需要对此次登录进行认证呢？玄机就在上面的support方法
        //上面参数authentication，是在认证时传递过来的，在Service层授权是传递的如下图所示，当认证时传递的CrmAuthToken类型，
        //则此类型经过supports方法判断时该Provider是否要处理此时认证
        return CrmAuthToken.class.isAssignableFrom(authentication);
    }
}
