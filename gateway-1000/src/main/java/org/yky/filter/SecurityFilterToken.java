package org.yky.filter;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.yky.constant.UserConstant;
import org.yky.exception.ErrorCode;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@Slf4j
public class SecurityFilterToken implements GlobalFilter, Ordered {

    @Resource
    private ExcludeUrlProperties excludeUrlProperties;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 1. 获得当前用户请求的路径url
        String url = exchange.getRequest().getURI().getPath();
        log.info("SecurityFilterToken url = {}", url);
        // 2. 获得所有需要排除校验的url list
        List<String> excludeList = excludeUrlProperties.getUrls();
        // 3. 校验并且排除excludeList
        if (excludeList != null && !excludeList.isEmpty()) {
            for (String excludeUrl : excludeList) {
                if (antPathMatcher.matchStart(excludeUrl, url)) {
                    return chain.filter(exchange);
                }
            }
        }
        // 4. 代码到达此处，表示请求被拦截，需要进行校验
        log.info("当前请求的路径[{}]被拦截...", url);
        // 5. 判断header中是否有token，对用户请求进行判断拦截
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String userId = headers.getFirst(UserConstant.HEADER_USER_ID);
        String userToken = headers.getFirst(UserConstant.HEADER_USER_TOKEN);
        log.info("userId = {}", userId);
        log.info("userToken = {}", userToken);
        // 6. 判断header中是否有token，对用户请求进行判断拦截
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String redisToken = stringRedisTemplate.opsForValue().get(UserConstant.REDIS_USER_TOKEN + ":" + userId);
            if (redisToken.equals(userToken)) {
                return chain.filter(exchange);
            }
        }

        // 默认不放行
        return RenderErrorUtils.display(exchange, ErrorCode.NOT_LOGIN_ERROR);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
