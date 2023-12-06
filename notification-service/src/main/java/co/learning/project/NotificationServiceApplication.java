package co.learning.project;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@EnableEurekaClient
@Slf4j
public class NotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

    @KafkaListener(topics = "NotificationTopic")
    public void handleNotification(OrderPlaceEvent orderPlaceEvent){
//send sms messages
        log.info("Received Notification for order -{}"+ orderPlaceEvent.getOrderNumber() );
    }
}