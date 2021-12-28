package ltd.fdsa.cloud.filter;

import lombok.extern.slf4j.Slf4j;
import ltd.fdsa.web.view.Result;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
public abstract class BaseFilter implements GlobalFilter, Ordered {

    protected String fullPath = "";

    protected String serviceName = "";

    protected ServerHttpRequest newRequest = null;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        this.fullPath = request.getPath().pathWithinApplication().value();
        if (StringUtils.isEmpty(fullPath)) {
            return chain.filter(exchange);
        }
        if (!fullPath.endsWith("/")) {
            fullPath += "/";
        }
        String[] paths = fullPath.split("/");
        if (paths.length >= 2) {
            serviceName = paths[1];
        }
        ServerHttpResponse response = exchange.getResponse();
        if (!access(request)) {
            byte[] bits = errorBody().toString().getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bits);
            response.setStatusCode(errorStatus());
            // 指定编码，否则在浏览器中会中文乱码
            response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
            return response.writeWith(Mono.just(buffer));
        }
        if (newRequest != null) {
            return chain.filter(exchange.mutate().request(newRequest).build());
        }
        return chain.filter(exchange);
    }


    protected abstract boolean access(ServerHttpRequest request);


    protected abstract HttpStatus errorStatus();

    protected abstract Result<Object> errorBody();
}
