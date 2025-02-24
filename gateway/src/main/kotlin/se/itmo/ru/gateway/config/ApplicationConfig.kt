package se.itmo.ru.gateway.config

import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import se.itmo.ru.gateway.security.AuthFilter

@Configuration
class ApplicationConfig(val authFilter: AuthFilter) {


    @Bean
    fun messageConverters(): HttpMessageConverters {
        return HttpMessageConverters(MappingJackson2HttpMessageConverter())
    }

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder, filter: AuthFilter): RouteLocator =
        builder.routes()
            .route("accounts") { r ->
                r.path("/api/accounts/**")
                    .filters { f ->
                        f.filters(mutableListOf(authFilter.apply(AuthFilter.Config())))
                        f.stripPrefix(2)
                        f.circuitBreaker { c ->
                            c.name = "accountsCircuitBreaker"
                        }
                    }
                    .uri("lb://accounts:8002")
            }
            .route("bookings") { r ->
                r.path("/api/bookings/**")
                    .filters { f ->
                        f.filters(mutableListOf(authFilter.apply(AuthFilter.Config())))
                        f.stripPrefix(2)
                        f.circuitBreaker { c ->
                            c.name = "bookingsCircuitBreaker"
                        }
                    }
                    .uri("lb://bookings:8003")
            }
            .route("wishlists") { r ->
                r.path("/api/wishlists/**")
                    .filters { f ->
                        f.filters(mutableListOf(authFilter.apply(AuthFilter.Config())))
                        f.stripPrefix(2)
                        f.circuitBreaker { c ->
                            c.name = "wishlistsCircuitBreaker"
                        }
                    }
                    .uri("lb://wishlists:8004")
            }
            .route("auth-service") { r ->
                r.path("/api/auth-service/auth/**")
                    .filters { f ->
//                                    f.filters(mutableListOf(filter.apply({})))
                        f.stripPrefix(2)
                        f.circuitBreaker { c ->
                            c.name = "authServiceCircuitBreaker"
                        }
                    }
                    .uri("lb://auth-service:8005")
            }
            .route("user-service") { r ->
                r.path("/api/user-service/users/**")
                    .filters { f ->
                        f.filters(mutableListOf(authFilter.apply(AuthFilter.Config())))
                        f.stripPrefix(2)
                        f.circuitBreaker { c ->
                            c.name = "authServiceCircuitBreaker"
                        }

                    }.uri("lb://auth-service:8005")
            }
            .build()


}