package ongoing.backend.config.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketTemplate {

  private final SimpMessagingTemplate messagingTemplate;

  public WebSocketTemplate(SimpMessagingTemplate messagingTemplate) {
    this.messagingTemplate = messagingTemplate;
  }

  public void broadcast(String topic, String message) {
    String destination = topic.startsWith("/topic/") ? topic : "/topic/" + topic;
    messagingTemplate.convertAndSend(destination, message);// Broadcast to WebSocket topic
  }
}

