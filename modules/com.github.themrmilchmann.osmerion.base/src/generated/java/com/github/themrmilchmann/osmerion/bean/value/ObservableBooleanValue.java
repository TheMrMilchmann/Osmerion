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
package com.github.themrmilchmann.osmerion.bean.value;

import com.github.themrmilchmann.osmerion.bean.value.change.*;

/**
 * An observable {@code boolean} value.
 *
 * @author Leon Linhart
 * @since 1.0.0
 */
public interface ObservableBooleanValue extends ObservableValue<Boolean> {

    // #########################################################################################################################################################
    // # Value Operations ######################################################################################################################################
    // #########################################################################################################################################################

    /**
     * Returns the value of this {@link ObservableBooleanValue}.
     *
     * @return the value of this {@code ObservableBooleanValue}
     *
     * @since 1.0.0
     */
    boolean get();

    // #########################################################################################################################################################
    // # Listeners #############################################################################################################################################
    // #########################################################################################################################################################

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
     * @see #removeListener(BooleanChangeListener)
     *
     * @since 1.0.0
     */
    void addListener(BooleanChangeListener listener);

    /**
     * {@inheritDoc}
     *
     * @see #addListener(BooleanChangeListener)
     *
     * @since 1.0.0
     */
    @Override
    default void addListener(ChangeListener<? super Boolean> listener) {
        this.addListener(BooleanChangeListener.wrap(listener));
    }

    /**
     * Detaches the specified listener from this value.
     *
     * @param listener the listener to be detached from this value
     *
     * @throws NullPointerException if {@code listener} is {@code null}
     *
     * @see #addListener(BooleanChangeListener)
     *
     * @since 1.0.0
     */
    void removeListener(BooleanChangeListener listener);

}