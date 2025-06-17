package com.bookbookclub.bbc_user_service.global.kafka;

import com.bookbookclub.common.event.FollowCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * 팔로우 관련 Kafka 이벤트 발행
 */
@Service
@RequiredArgsConstructor
public class FollowEventProducer {

    private final KafkaTemplate<String, FollowCreatedEvent> kafkaTemplate;

    public void sendFollowCreated(Long senderUserId, Long receiverUserId) {
        FollowCreatedEvent event = new FollowCreatedEvent(senderUserId, receiverUserId);
        kafkaTemplate.send("follow.created", event);
    }
}
