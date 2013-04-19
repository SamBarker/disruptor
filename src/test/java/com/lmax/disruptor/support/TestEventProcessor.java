package com.lmax.disruptor.support;

import com.lmax.disruptor.EventProcessor;
import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceBarrier;
import com.lmax.disruptor.SingleProducerSequencer;

public final class TestEventProcessor implements EventProcessor
{
    private final SequenceBarrier sequenceBarrier;
    private final Sequence sequence = new Sequence(SingleProducerSequencer.INITIAL_CURSOR_VALUE);

    public TestEventProcessor(final SequenceBarrier sequenceBarrier)
    {
        this.sequenceBarrier = sequenceBarrier;
    }

    @Override
    public Sequence getSequence()
    {
        return sequence;
    }

    @Override
    public void halt()
    {
    }

    @Override
    public void run()
    {
        try
        {
            sequenceBarrier.waitFor(0L);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }

        sequence.set(sequence.get() + 1L);
    }
}
