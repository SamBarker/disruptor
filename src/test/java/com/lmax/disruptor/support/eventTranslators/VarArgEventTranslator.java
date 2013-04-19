package com.lmax.disruptor.support.eventTranslators;

import com.lmax.disruptor.EventTranslatorVararg;

public class VarArgEventTranslator implements EventTranslatorVararg<Object[]> {
    @Override
    public void translateTo(Object[] event, long sequence, Object...args)
    {
        event[0] = (String)args[0] + args[1] + args[2] + args[3] + "-" + sequence;
    }
}
