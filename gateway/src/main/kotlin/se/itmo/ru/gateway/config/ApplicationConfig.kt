package se.itmo.ru.gateway.config

import org.springframework.boot.autoconfigure.http.HttpMessageConverters
import org.springframework.cloud.gateway.route.RouteLocator
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter

@Configuration
class ApplicationConfig {

    @Bean
    fun messageConverters(): HttpMessageConverters {
        return HttpMessageConverters(MappingJackson2HttpMessageConverter())
    }

    @Bean
    fun customRouteLocator(builder: RouteLocatorBuilder): RouteLocator =
        builder.routes()
            .route("accounts") { r ->
                r.path("/api/accounts/**")
                    .filters { f ->
                        f.stripPrefix(2)
                        f.circuitBreaker { c ->
                            c.name = "accountsCitcuitBreaker"
                        }
                    }
                    .uri("lb://accounts:8002")
            }
            .route("bookings") { r ->
                r.path("/api/bookings/**")
                    .filters { f ->
                        f.stripPrefix(2)
                        f.circuitBreaker { c ->
                            c.name = "bookingsCitcuitBreaker"
                        }
                    }
                    .uri("lb://bookings:8003")
            }
            .route("wishlists") { r ->
                r.path("/api/wishlists/**")
                    .filters { f ->
                        f.stripPrefix(2)
                        f.circuitBreaker { c ->
                            c.name = "wishlistsCitcuitBreaker"
                        }
                    }
                    .uri("lb://wishlists:8004")
            }
            .build()

}