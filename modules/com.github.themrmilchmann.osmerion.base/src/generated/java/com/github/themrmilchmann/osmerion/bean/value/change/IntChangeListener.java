/*
 * Copyright (c) 2017 Leon Linhart,
 * All rights reserved.
 * MACHINE GENERATED FILE, DO NOT EDIT
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 *  Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.themrmilchmann.osmerion.bean.value.change;

import com.github.themrmilchmann.osmerion.bean.value.ObservableIntValue;

/**
 * A specialized {@code int} {@link ChangeListener}.
 *
 * @author Leon Linhart
 * @since 1.0.0
 */
@FunctionalInterface
public interface IntChangeListener {

    /**
     * Processes a value change of an ObservableValue this listener is attached to.
     *
     * @param observable the observable whose value has changed
     * @param oldValue   the old value
     * @param newValue   the new value
     *
     * @since 1.0.0
     */
    void onChanged(ObservableIntValue observable, int oldValue, int newValue);

    /**
     * Returns a specialized ChangeListener wrapping around the given one.
     *
     * @param listener the listener to be wrapped
     *
     * @return a specialized ChangeListener wrapping around the given one
     *
     * @since 1.0.0
     */
    static IntChangeListener wrap(ChangeListener<? super Integer> listener) {
        return new IntChangeListener() {
        
            @Override
            public void onChanged(ObservableIntValue observable, int oldValue, int newValue) {
                listener.onChanged(observable, oldValue, newValue);
            }
        
            @Override
            public boolean equals(Object other) {
                return other == listener || other == this;
            }
        
            @Override
            public int hashCode() {
                return listener.hashCode();
            }
        
        };
    }

}