package se.itmo.ru.wishlists.config

import org.springframework.cloud.client.discovery.DiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandler
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.web.socket.client.WebSocketClient
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.util.concurrent.CompletableFuture


@Configuration
class WebSocketConfig(
    private val discoveryClient: DiscoveryClient
) {

    @Bean
    @Profile("!test")
    fun wsStompSession(): StompSession {
        val webSocketClient: WebSocketClient = StandardWebSocketClient()
        val stompClient = WebSocketStompClient(webSocketClient)
        stompClient.messageConverter = MappingJackson2MessageConverter()
        val sessionHandler: StompSessionHandler = object : StompSessionHandlerAdapter() {}

        val instances = discoveryClient.getInstances("notification-service")
        val url = "ws://" + instances[0].host + ":" + instances[0].port + "/ws-server"
        val stompSessionCompletableFuture: CompletableFuture<StompSession> =
            stompClient.connectAsync(url, sessionHandler)
        return stompSessionCompletableFuture.join()
    }
}