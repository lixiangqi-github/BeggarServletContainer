package com.ljm.server.io.event.listener.impl;

import com.ljm.server.event.handler.EventHandler;
import com.ljm.server.event.listener.AbstractEventListener;

import java.nio.channels.SelectionKey;


public class SelectableChannleEventListener extends AbstractEventListener<SelectionKey> {
    private final EventHandler<SelectionKey> eventHandler;

    public SelectableChannleEventListener(EventHandler<SelectionKey> eventHandler) {
        this.eventHandler = eventHandler;
    }

    @Override
    protected EventHandler<SelectionKey> getEventHandler(SelectionKey event) {
        return this.eventHandler;
    }
}
