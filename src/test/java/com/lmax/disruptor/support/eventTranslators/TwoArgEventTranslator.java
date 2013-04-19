package com.lmax.disruptor.support.eventTranslators;

import com.lmax.disruptor.EventTranslatorTwoArg;

public class TwoArgEventTranslator implements EventTranslatorTwoArg<Object[], String, String> {
    @Override
    public void translateTo(Object[] event, long sequence, String arg0, String arg1) {
        event[0] = arg0 + arg1 + "-" + sequence;
    }
}
