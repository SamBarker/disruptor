package com.lmax.disruptor;

public interface RingBuffer<E> extends Cursored, DataProvider<E> {
    /**
     * @deprecated Use {@link com.lmax.disruptor.RingBuffer#get(long)}
     */
    @Deprecated
    E getPreallocated(long sequence);

    /**
     * @deprecated Use {@link com.lmax.disruptor.RingBuffer#get(long)}
     */
    @Deprecated
    E getPublished(long sequence);

    /**
     * Increment and return the next sequence for the ring buffer.  Calls of this
     * method should ensure that they always publish the sequence afterward.  E.g.
     * <pre>
     * long sequence = ringBuffer.next();
     * try {
     *     Event e = ringBuffer.get(sequence);
     *     // Do some work with the event.
     * } finally {
     *     ringBuffer.publish(sequence);
     * }
     * </pre>
     *
     * @return The next sequence to publish to.
     * @see com.lmax.disruptor.RingBuffer#publish(long)
     * @see com.lmax.disruptor.RingBuffer#get(long)
     */
    long next();

    /**
     * The same functionality as {@link com.lmax.disruptor.RingBuffer#next()}, but allows the caller to claim
     * the next n sequences.
     *
     * @param n number of slots to claim
     * @return sequence number of the highest slot claimed
     * @see com.lmax.disruptor.Sequencer#next(int)
     */
    long next(int n);

    /**
     * <p>Increment and return the next sequence for the ring buffer.  Calls of this
     * method should ensure that they always publish the sequence afterward.  E.g.
     * <pre>
     * long sequence = ringBuffer.next();
     * try {
     *     Event e = ringBuffer.get(sequence);
     *     // Do some work with the event.
     * } finally {
     *     ringBuffer.publish(sequence);
     * }
     * </pre>
     * <p>This method will not block if there is not space available in the ring
     * buffer, instead it will throw an {@link com.lmax.disruptor.InsufficientCapacityException}.
     *
     * @return The next sequence to publish to.
     * @throws com.lmax.disruptor.InsufficientCapacityException
     *          if the necessary space in the ring buffer is not available
     * @see com.lmax.disruptor.RingBuffer#publish(long)
     * @see com.lmax.disruptor.RingBuffer#get(long)
     */
    long tryNext() throws InsufficientCapacityException;

    /**
     * The same functionality as {@link com.lmax.disruptor.RingBuffer#tryNext()}, but allows the caller to attempt
     * to claim the next n sequences.
     *
     * @param n number of slots to claim
     * @return sequence number of the highest slot claimed
     * @throws com.lmax.disruptor.InsufficientCapacityException
     *          if the necessary space in the ring buffer is not available
     */
    long tryNext(int n) throws InsufficientCapacityException;

    /**
     * Resets the cursor to a specific value.  This can be applied at any time, but it is worth not
     * that it is a racy thing to do and should only be used in controlled circumstances.  E.g. during
     * initialisation.
     *
     * @param sequence The sequence to reset too.
     * @throws IllegalStateException If any gating sequences have already been specified.
     */
    void resetTo(long sequence);

    /**
     * Sets the cursor to a specific sequence and returns the preallocated entry that is stored there.  This
     * is another deliberatly racy call, that should only be done in controlled circumstances, e.g. initialisation.
     *
     * @param sequence The sequence to claim.
     * @return The preallocated event.
     */
    E claimAndGetPreallocated(long sequence);

    /**
     * Determines if a particular entry has been published.
     *
     * @param sequence The sequence to identify the entry.
     * @return If the value has been published or not.
     */
    boolean isPublished(long sequence);

    /**
     * Add the specified gating sequences to this instance of the Disruptor.  They will
     * safely and atomically added to the list of gating sequences.
     *
     * @param gatingSequences The sequences to add.
     */
    void addGatingSequences(Sequence... gatingSequences);

    /**
     * Get the minimum sequence value from all of the gating sequences
     * added to this ringBuffer.
     *
     * @return The minimum gating sequence or the cursor sequence if
     *         no sequences have been added.
     */
    long getMinimumGatingSequence();

    /**
     * Remove the specified sequence from this ringBuffer.
     *
     * @param sequence to be removed.
     * @return <tt>true</tt> if this sequence was found, <tt>false</tt> otherwise.
     */
    boolean removeGatingSequence(Sequence sequence);

    /**
     * Create a new SequenceBarrier to be used by an EventProcessor to track which messages
     * are available to be read from the ring buffer given a list of sequences to track.
     *
     * @param sequencesToTrack the additional sequences to track
     * @return A sequence barrier that will track the specified sequences.
     * @see com.lmax.disruptor.SequenceBarrier
     */
    SequenceBarrier newBarrier(Sequence... sequencesToTrack);

    /**
     * Get the current cursor value for the ring buffer.  The cursor value is
     * the last value that was published, or the highest available sequence
     * that can be consumed.
     */
    long getCursor();

    /**
     * The size of the buffer.
     */
    int getBufferSize();

    /**
     * Given specified <tt>requiredCapacity</tt> determines if that amount of space
     * is available.  Note, you can not assume that if this method returns <tt>true</tt>
     * that a call to {@link com.lmax.disruptor.RingBuffer#next()} will not block.  Especially true if this
     * ring buffer is set up to handle multiple producers.
     *
     * @param requiredCapacity The capacity to check for.
     * @return <tt>true</tt> If the specified <tt>requiredCapacity</tt> is available
     *         <tt>false</tt> if now.
     */
    boolean hasAvailableCapacity(int requiredCapacity);

    /**
     * Publishes an event to the ring buffer.  It handles
     * claiming the next sequence, getting the current (uninitialised)
     * event from the ring buffer and publishing the claimed sequence
     * after translation.
     *
     * @param translator The user specified translation for the event
     */
    void publishEvent(EventTranslator<E> translator);

    /**
     * Attempts to publish an event to the ring buffer.  It handles
     * claiming the next sequence, getting the current (uninitialised)
     * event from the ring buffer and publishing the claimed sequence
     * after translation.  Will return false if specified capacity
     * was not available.
     *
     * @param translator The user specified translation for the event
     * @return true if the value was published, false if there was insufficient
     *         capacity.
     */
    boolean tryPublishEvent(EventTranslator<E> translator);

    /**
     * Allows one user supplied argument.
     *
     * @param translator The user specified translation for the event
     * @param arg0       A user supplied argument.
     * @see #publishEvent(com.lmax.disruptor.EventTranslator)
     */
    <A> void publishEvent(EventTranslatorOneArg<E, A> translator, A arg0);

    /**
     * Allows one user supplied argument.
     *
     * @param translator The user specified translation for the event
     * @param arg0       A user supplied argument.
     * @return true if the value was published, false if there was insufficient
     *         capacity.
     * @see #tryPublishEvent(com.lmax.disruptor.EventTranslator)
     */
    <A> boolean tryPublishEvent(EventTranslatorOneArg<E, A> translator, A arg0);

    /**
     * Allows two user supplied arguments.
     *
     * @param translator The user specified translation for the event
     * @param arg0       A user supplied argument.
     * @param arg1       A user supplied argument.
     * @see #publishEvent(com.lmax.disruptor.EventTranslator)
     */
    <A, B> void publishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1);

    /**
     * Allows two user supplied arguments.
     *
     * @param translator The user specified translation for the event
     * @param arg0       A user supplied argument.
     * @param arg1       A user supplied argument.
     * @return true if the value was published, false if there was insufficient
     *         capacity.
     * @see #tryPublishEvent(com.lmax.disruptor.EventTranslator)
     */
    <A, B> boolean tryPublishEvent(EventTranslatorTwoArg<E, A, B> translator, A arg0, B arg1);

    /**
     * Allows three user supplied arguments
     *
     * @param translator The user specified translation for the event
     * @param arg0       A user supplied argument.
     * @param arg1       A user supplied argument.
     * @param arg2       A user supplied argument.
     * @see #publishEvent(com.lmax.disruptor.EventTranslator)
     */
    <A, B, C> void publishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2);

    /**
     * Allows three user supplied arguments
     *
     * @param translator The user specified translation for the event
     * @param arg0       A user supplied argument.
     * @param arg1       A user supplied argument.
     * @param arg2       A user supplied argument.
     * @return true if the value was published, false if there was insufficient
     *         capacity.
     * @see #publishEvent(com.lmax.disruptor.EventTranslator)
     */
    <A, B, C> boolean tryPublishEvent(EventTranslatorThreeArg<E, A, B, C> translator, A arg0, B arg1, C arg2);

    /**
     * Allows a variable number of user supplied arguments
     *
     * @param translator The user specified translation for the event
     * @param args       User supplied arguments.
     * @see #publishEvent(com.lmax.disruptor.EventTranslator)
     */
    void publishEvent(EventTranslatorVararg<E> translator, Object... args);

    /**
     * Allows a variable number of user supplied arguments
     *
     * @param translator The user specified translation for the event
     * @param args       User supplied arguments .
     * @return true if the value was published, false if there was insufficient capacity.
     * @see #publishEvent(EventTranslator)
     */
    public boolean tryPublishEvent(EventTranslatorVararg<E> translator, Object... args);

    /**
     * Publish the specified sequence.  This action marks this particular
     * message as being available to be read.
     *
     * @param sequence the sequence to publish.
     */
    void publish(long sequence);

    /**
     * Publish the specified sequences.  This action marks these particular
     * messages as being available to be read.
     *
     * @param lo the lowest sequence number to be published
     * @param hi the highest sequence number to be published
     * @see com.lmax.disruptor.Sequencer#next(int)
     */
    void publish(long lo, long hi);

    /**
     * Get the remaining capacity for this ringBuffer.
     *
     * @return The number of slots remaining.
     */
    long remainingCapacity();
}
