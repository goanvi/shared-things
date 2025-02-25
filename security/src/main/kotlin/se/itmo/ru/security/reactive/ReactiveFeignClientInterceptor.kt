package se.itmo.ru.security.reactive

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.context.SecurityContext
import reactivefeign.client.ReactiveHttpRequest
import reactivefeign.client.ReactiveHttpRequestInterceptor
import reactor.core.publisher.Mono
import se.itmo.ru.security.common.InternalAuth

class ReactiveFeignClientInterceptor : ReactiveHttpRequestInterceptor {
    override fun apply(request: ReactiveHttpRequest): Mono<ReactiveHttpRequest> {
        return ReactiveSecurityContextHolder.getContext()
            .map { obj: SecurityContext -> obj.authentication }
            .filter { authentication: Authentication? -> authentication != null && authentication.isAuthenticated }
            .cast(InternalAuth::class.java)
            .map { authentication: InternalAuth ->
                request.headers().put("X-User-Id", listOf(authentication.userId.toString()))
                request.headers().put("X-User-Role", listOf(authentication.userRole))
                request
            }
            .switchIfEmpty(Mono.just(request))
    }
}
