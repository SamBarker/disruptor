package com.lmax.disruptor.support.eventTranslators;

import com.lmax.disruptor.EventTranslatorThreeArg;

public class ThreeArgEventTranslator implements EventTranslatorThreeArg<Object[], String, String, String> {
    @Override
    public void translateTo(Object[] event, long sequence, String arg0, String arg1, String arg2) {
        event[0] = arg0 + arg1 + arg2 + "-" + sequence;
    }
}
