package com.zhaofujun.nest.context.event.channel.local;


import com.zhaofujun.nest.context.event.channel.MessageChannelProvider;
import com.zhaofujun.nest.context.event.message.MessageConverter;
import com.zhaofujun.nest.standard.EventHandler;
import com.zhaofujun.nest.context.event.message.MessageInfo;
import com.zhaofujun.nest.context.event.channel.MessageConsumer;
import com.zhaofujun.nest.context.event.channel.MessageProducer;

import java.util.HashMap;
import java.util.Map;

public class LocalMessageChannel implements MessageChannelProvider, MessageProducer, MessageConsumer {

    public static final String CHANNEL_CODE = "LocalMessageChannel";
    private MessageConsumer messageConsumer;

    public LocalMessageChannel() {
        this.messageConsumer = new LocalMessageConsumer(this);
    }


    private static Map<String, EventSource> eventSourceMap = new HashMap<>();

    public EventSource getEventSource(String messageGroup) {
        EventSource eventSource = eventSourceMap.get(messageGroup);
        if (eventSource == null) {
            eventSource = new EventSource(this);
            eventSourceMap.put(messageGroup, eventSource);
        }
        return eventSource;
    }

    @Override
    public String getCode() {
        return CHANNEL_CODE;
    }

    @Override
    public MessageProducer getMessageProducer() {
        return this;
    }

    @Override
    public MessageConsumer getMessageConsumer() {
        return this;
    }

    @Override
    public void send(String messageGroup, MessageInfo messageInfo) {
        EventSource eventSource = getEventSource(messageGroup);
        eventSource.send(messageInfo);
    }

    @Override
    public void subscribe(EventHandler eventHandler) {
        this.messageConsumer.subscribe(eventHandler);
    }
}

