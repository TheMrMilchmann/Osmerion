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
package com.github.themrmilchmann.osmerion.bean.property;

import java.util.ArrayList;
import java.util.List;

import com.github.themrmilchmann.osmerion.bean.value.WritableIntValue;
import com.github.themrmilchmann.osmerion.bean.value.change.*;

/**
 * A basic {@code int} property.
 *
 * @see SimpleIntProperty
 *
 * @author Leon Linhart
 * @since 1.0.0
 */
public abstract class AbstractIntProperty extends Property<Integer> implements ReadOnlyIntProperty, WritableIntValue {

    /**
     * The initial value of an AbstractIntProperty.
     *
     * @since 1.0.0
     */
    public static final int INITIAL_VALUE = 0;

    /**
     * The list of ChangeListeners attached to this property.
     *
     * @since 1.0.0
     */
    protected List<IntChangeListener> changeListeners;
    /**
     * The current value of this property.
     *
     * @since 1.0.0
     */
    protected int value;

    /**
     * Creates a new {@link AbstractIntProperty} with the default initial value {@link #INITIAL_VALUE}
     *
     * @since 1.0.0
     */
    protected AbstractIntProperty() {
        this(INITIAL_VALUE);
    }

    /**
     * Creates a new {@link AbstractIntProperty} with specified initial value.
     *
     * @param initialValue the initial value for this property
     *
     * @since 1.0.0
     */
    protected AbstractIntProperty(int initialValue) {
        this.value = initialValue;
    }

    // #########################################################################################################################################################
    // # Value Operations ######################################################################################################################################
    // #########################################################################################################################################################

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public final int get() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public final Integer getValue() {
        return this.get();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public final int set(int value) {
        if (this.isBound()) throw new UnsupportedOperationException("A bound property's value may not be set explicitly");
        
        int oldValue = this.value;
        value = this.validate(value);
        
        if (oldValue != value) {
            this.value = value;
        
            if (this.changeListeners != null) this.changeListeners.forEach(listener -> listener.onChanged(this, oldValue, this.value));
        }
        
        return oldValue;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public final Integer setValue(Integer value) {
        return this.set(value);
    }

    /**
     * Validates the specified value and returns the result.
     *
     * @param value the value to be validated
     *
     * @return the validated value
     *
     * @since 1.0.0
     */
    protected abstract int validate(int value);

    // #########################################################################################################################################################
    // # Listeners #############################################################################################################################################
    // #########################################################################################################################################################

    /**
     * {@inheritDoc}
     *
     * @see #removeListener(IntChangeListener)
     *
     * @since 1.0.0
     */
    @Override
    public final void addListener(IntChangeListener listener) {
        if (listener == null) throw new NullPointerException();
        if (this.changeListeners == null) this.changeListeners = new ArrayList<>(1);
        
        this.changeListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @see #addListener(IntChangeListener)
     *
     * @since 1.0.0
     */
    @Override
    public final void removeListener(IntChangeListener listener) {
        if (listener == null) throw new NullPointerException();
        if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0
     */
    @Override
    public final void removeListener(ChangeListener<? super Integer> listener) {
        if (listener == null) throw new NullPointerException();
        if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
    }

}