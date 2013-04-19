package com.lmax.disruptor.support.eventTranslators;

import com.lmax.disruptor.EventTranslator;

public class NoArgEventTranslator implements EventTranslator<Object[]> {
    @Override
    public void translateTo(Object[] event, long sequence) {
        event[0] = sequence;
    }
}
