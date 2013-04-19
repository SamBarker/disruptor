package com.lmax.disruptor.support.eventTranslators;

import com.lmax.disruptor.EventTranslatorOneArg;

public class OneArgEventTranslator implements EventTranslatorOneArg<Object[], String> {
    @Override
    public void translateTo(Object[] event, long sequence, String arg0)
    {
        event[0] = arg0 + "-" + sequence;
    }
}
