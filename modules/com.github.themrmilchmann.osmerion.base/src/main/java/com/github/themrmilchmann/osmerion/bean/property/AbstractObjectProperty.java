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
package com.github.themrmilchmann.osmerion.bean.property;

import java.util.ArrayList;
import java.util.List;

import com.github.themrmilchmann.osmerion.bean.value.ObservableObjectValue;
import com.github.themrmilchmann.osmerion.bean.value.WritableObjectValue;
import com.github.themrmilchmann.osmerion.bean.value.change.ChangeListener;

/**
 * A basic {@code Object} property.
 *
 * @param <T> the type of the value
 *
 * @see SimpleObjectProperty
 *
 * @author Leon Linhart
 * @since 1.0.0.0
 */
public abstract class AbstractObjectProperty<T> extends Property<T> implements ReadOnlyObjectProperty<T>, WritableObjectValue<T> {

    /**
     * The initial value of an AbstractBooleanProperty.
     *
     * @since 1.0.0.0
     */
    public static final Object INITIAL_VALUE = null;

    /**
     * The list of ChangeListeners attached to this property.
     *
     * @since 1.0.0.0
     */
    protected List<ChangeListener<? super T>> changeListeners;
    /**
     * The current value of this property.
     *
     * @since 1.0.0.0
     */
    protected T value;

    private ObservableObjectValue<T> binding;
    private ChangeListener<T> bindingListener;

    /**
     * Creates a new {@link AbstractObjectProperty} with the default initial value {@link #INITIAL_VALUE}
     *
     * @since 1.0.0.0
     */
    @SuppressWarnings("unchecked")
    protected AbstractObjectProperty() {
        this((T) INITIAL_VALUE);
    }

    /**
     * Creates a new {@link AbstractObjectProperty} with specified initial value.
     *
     * @param initialValue the initial value for this property
     *
     * @since 1.0.0.0
     */
    protected AbstractObjectProperty(T initialValue) {
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
    public final T get() {
        return this.value;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public final T getValue() {
        return this.get();
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public final T set(T value) {
        if (this.isBound()) throw new UnsupportedOperationException("A bound property's value may not be set explicitly");

        return this.setImpl(value);
    }

    private T setImpl(T value) {
        T oldValue = this.value;
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
    public final T setValue(T value) {
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
    protected abstract T validate(T value);

    // #########################################################################################################################################################
    // # Binding ###############################################################################################################################################
    // #########################################################################################################################################################

    /**
     * Binds this property's value to the value of a given {@link ObservableObjectValue}. When a property is bound to a value it will always mirror the
     * validated ({@link #validate(Object)}) version of that value. Any attempt to set the value of a bound property explicitly will fail. A bound property may
     * be unbound again by calling {@link #unbind()}.
     *
     * @param other the observable value to bind this property to
     *
     * @throws NullPointerException if the given {@code ObservableValue} is null
     * @throws IllegalStateException if this property is already bound to a value
     *
     * @since 1.0.0.0
     */
    public final void bind(ObservableObjectValue<T> other) {
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
    @Override
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
     * @since 1.0.0.0
     */
    @Override
    public final void addListener(ChangeListener<? super T> listener) {
        if (listener == null) throw new NullPointerException();
        if (this.changeListeners == null) this.changeListeners = new ArrayList<>(1);

        this.changeListeners.add(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public final void removeListener(ChangeListener<? super T> listener) {
        if (listener == null) throw new NullPointerException();
        if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
    }

}