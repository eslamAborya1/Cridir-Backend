package com.NTG.Cridir.Config;

import com.NTG.Cridir.Websocket.ProviderLocationSocketHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ProviderLocationSocketHandler handler;

    @Value("${app.cors.allowed-origins:*}")
    private String allowedOrigins;

    public WebSocketConfig(ProviderLocationSocketHandler handler) {
        this.handler = handler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws/location")
                .setAllowedOrigins(Arrays.stream(allowedOrigins.split(",")).toArray(String[]::new));
    }
}
