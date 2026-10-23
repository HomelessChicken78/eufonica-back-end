package it.eufonica.gatewayservice.exception;

import org.jspecify.annotations.NullMarked;
import org.springframework.boot.webflux.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;

@Component
@Order(-2)
public class GatewayDownstreamExceptionHandler implements ErrorWebExceptionHandler {
    private HttpStatus statusFor(Throwable ex) {
        if (ex instanceof ConnectException)
            return HttpStatus.BAD_GATEWAY;

        // If it is not any of the above errors, let the default handler handle the error
        return null;
    }

    @Override
    @NullMarked
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        HttpStatus status = statusFor(ex);

        if (status == null)
            return Mono.error(ex);

        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().setComplete();
    }
}
