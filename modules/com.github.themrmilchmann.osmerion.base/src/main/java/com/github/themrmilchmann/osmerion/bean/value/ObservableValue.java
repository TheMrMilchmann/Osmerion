/*
 * Copyright (c) 2017 Leon Linhart,
 * All rights reserved.
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
package com.github.themrmilchmann.osmerion.bean.value;

import com.github.themrmilchmann.osmerion.bean.value.change.*;

/**
 * A wrapper to make a value observable.
 *
 * <p>This class should not be used directly. Instead implement one of its specialized subclasses.</p>
 *
 * @param <T> type of the observable value
 *
 * @see WritableValue
 *
 * @author Leon Linhart
 * @since 1.0.0.0
 */
public interface ObservableValue<T> {

    // ########################################################################################################################################################
    // # Value Operations #####################################################################################################################################
    // ########################################################################################################################################################

    /**
     * Returns the value of this {@link ObservableValue}.
     *
     * @return the value of this {@code ObservableValue}
     *
     * @since 1.0.0.0
     */
    T getValue();

    // ########################################################################################################################################################
    // # Listeners ############################################################################################################################################
    // ########################################################################################################################################################

    /**
     * Attaches the specified listener to this {@link ObservableValue}.
     *
     * <p>As long as the listener is attached it will be notified whenever the value of this {@code ObservableValue} changes via
     * {@link ChangeListener#onChanged(ObservableValue, Object, Object)}.</p>
     *
     * @param listener the listener to be attached to this {@code ObservableValue}
     *
     * @throws NullPointerException if {@code listener} is {@code null}
     *
     * @see #removeListener(ChangeListener)
     *
     * @since 1.0.0.0
     */
    void addListener(ChangeListener<? super T> listener);

    /**
     * Detaches the specified listener from this {@link ObservableValue}.
     *
     * @param listener the listener to be detached from this {@code ObservableValue}
     *
     * @throws NullPointerException if {@code listener} is {@code null}
     *
     * @see #addListener(ChangeListener)
     *
     * @since 1.0.0.0
     */
    void removeListener(ChangeListener<? super T> listener);

}