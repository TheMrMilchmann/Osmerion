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

import com.github.themrmilchmann.osmerion.bean.value.ObservableByteValue;
import com.github.themrmilchmann.osmerion.bean.value.WritableByteValue;
import com.github.themrmilchmann.osmerion.bean.value.change.*;

/**
 * A basic {@code byte} property.
 *
 * @see SimpleByteProperty
 *
 * @author Leon Linhart
 * @since 1.0.0.0
 */
public abstract class AbstractByteProperty extends Property<Byte> implements ReadOnlyByteProperty, WritableByteValue {

    /**
     * The initial value of an AbstractByteProperty.
     *
     * @since 1.0.0.0
     */
    public static final byte INITIAL_VALUE = 0;

    /**
     * The list of ChangeListeners attached to this property.
     *
     * @since 1.0.0.0
     */
    protected List<ByteChangeListener> changeListeners;
    /**
     * The current value of this property.
     *
     * @since 1.0.0.0
     */
    protected byte value;

    private ObservableByteValue binding;
    private ByteChangeListener bindingListener;

    /**
     * Creates a new {@link AbstractByteProperty} with the default initial value {@link #INITIAL_VALUE}
     *
     * @since 1.0.0.0
     */
    protected AbstractByteProperty() {
        this(INITIAL_VALUE);
    }

    /**
     * Creates a new {@link AbstractByteProperty} with specified initial value.
     *
     * @param initialValue the initial value for this property
     *
     * @since 1.0.0.0
     */
    protected AbstractByteProperty(byte initialValue) {
        this.value = initialValue;
    }

    // #########################################################################################################################################################
    // # Value Operations ######################################################################################################################################
    // #########################################################################################################################################################

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public final byte get() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public final Byte getValue() {
        return this.get();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public final byte set(byte value) {
        if (this.isBound()) throw new UnsupportedOperationException("A bound property's value may not be set explicitly");
        
        return this.setImpl(value);
    }


    private byte setImpl(byte value) {
        byte oldValue = this.value;
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
     * @since 1.0.0.0
     */
    @Override
    public final Byte setValue(Byte value) {
        return this.set(value);
    }

    /**
     * Validates the specified value and returns the result.
     *
     * @param value the value to be validated
     *
     * @return the validated value
     *
     * @since 1.0.0.0
     */
    protected abstract byte validate(byte value);

    // #########################################################################################################################################################
    // # Binding ###############################################################################################################################################
    // #########################################################################################################################################################

    /**
     * Binds this property's value to the value of a given {@link ObservableByteValue}. When a property is bound to a value it will always
     * mirror the validated ({@link #validate(byte)}) version of that value. Any attempt to set the value of a bound property explicitly will fail.
     * A bound property may be unbound again by calling {@link #unbind()}.
     *
     * @param other the observable value to bind this property to
     *
     * @throws NullPointerException if the given {@code ObservableValue} is null
     * @throws IllegalStateException if this property is already bound to a value
     *
     * @since 1.0.0.0
     */
    public final void bind(ObservableByteValue other) {
        if (other == null) throw new NullPointerException("The value to bind a property to may not be null!");
        if (this.binding != null) throw new IllegalStateException("The property is already bound to a value!");
        
        this.binding = other;
        this.binding.addListener(this.bindingListener = (observable, oldValue, newValue) -> this.setImpl(newValue));
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    public final boolean isBound() {
        return this.binding != null;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    public final void unbind() {
        if (this.binding == null) throw new IllegalStateException("The property is not bound to a value!");
        
        this.binding.removeListener(this.bindingListener);
        this.binding = null;
    }

    // #########################################################################################################################################################
    // # Listeners #############################################################################################################################################
    // #########################################################################################################################################################

    /**
     * {@inheritDoc}
     *
     * @see #removeListener(ByteChangeListener)
     *
     * @since 1.0.0.0
     */
    @Override
    public final void addListener(ByteChangeListener listener) {
        if (listener == null) throw new NullPointerException();
        if (this.changeListeners == null) this.changeListeners = new ArrayList<>(1);
        
        this.changeListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @see #addListener(ByteChangeListener)
     *
     * @since 1.0.0.0
     */
    @Override
    public final void removeListener(ByteChangeListener listener) {
        if (listener == null) throw new NullPointerException();
        if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public final void removeListener(ChangeListener<? super Byte> listener) {
        if (listener == null) throw new NullPointerException();
        if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
    }

}