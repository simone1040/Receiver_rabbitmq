package com.simone.progetto.syncro;
import com.simone.progetto.utils.Configuration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service("syncronization_queue")
@Slf4j
public class SyncronizationQueue implements SyncroCommunicator {
    private final RabbitTemplate rabbitTemplate;

    @Qualifier("fanout_syncro")
    @Autowired private FanoutExchange fanoutExchange;

    @Autowired
    private SyncronizationCodeResponseQueue communicator;

    @Autowired
    public SyncronizationQueue(final RabbitTemplate template) {
        this.rabbitTemplate = template;
    }

    public void sendMessage(SyncroMessage message) {
        rabbitTemplate.convertAndSend(fanoutExchange.getName(),
                "",message);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void SyncronizationStartup() {
        log.info("{"+Configuration.UUID + "} Syncro request");
        communicator.sendRequest(new SyncroCodeRequestMessage(Configuration.UUID,Configuration.GENESIS_HASH));
    }
}
