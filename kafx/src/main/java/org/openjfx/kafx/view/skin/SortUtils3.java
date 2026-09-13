/**
 * Copyright (c) 2013, 2018 ControlsFX
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *     * Neither the name of ControlsFX, any associated website, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL CONTROLSFX BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

// UNMODIFIED COPY
package org.openjfx.kafx.view.skin;

import javafx.beans.NamedArg;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.control.SortEvent;

public class SortUtils3 {
    
    /**
     * Convenient subclass of SortEvent to create a new SortEvent to indicate
     * the sorting has started
     * @param <C> the event source which sent the event
     */
    public static class SortStartedEvent3<C> extends SortEvent<C> {

        public static final EventType<?> SORT_STARTED_EVENT_3 = new EventType<>(SortEvent.ANY, "SORT_STARTED_EVENT_3");

        public SortStartedEvent3(@NamedArg("source") C source, @NamedArg("target") EventTarget target) {
            super(source, target);
            this.eventType = SORT_STARTED_EVENT_3;
        }

    }
    
    /**
     * Convenient subclass of SortEvent to create a new SortEvent to indicate
     * the sorting has ended
     * @param <C> the event source which sent the event
     */
    public static class SortEndedEvent3<C> extends SortEvent<C> {

        public static final EventType<?> SORT_ENDED_EVENT_3 = new EventType<>(SortEvent.ANY, "SORT_ENDED_EVENT_3");

        public SortEndedEvent3(@NamedArg("source") C source, @NamedArg("target") EventTarget target) {
            super(source, target);
            this.eventType = SORT_ENDED_EVENT_3;
        }

    }
}
