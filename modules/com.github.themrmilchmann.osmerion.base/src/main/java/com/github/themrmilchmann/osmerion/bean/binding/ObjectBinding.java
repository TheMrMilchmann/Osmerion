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
package com.github.themrmilchmann.osmerion.bean.binding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.github.themrmilchmann.osmerion.bean.value.*;
import com.github.themrmilchmann.osmerion.bean.value.change.*;
import com.github.themrmilchmann.osmerion.util.functional.function.*;

/**
 * An {@code ObservableObjectValue} that is used to keep track of another observable.
 *
 * @param <T> type of the observable value
 *
 * @author Leon Linhart
 * @since 1.0.0.0
 */
public abstract class ObjectBinding<T> implements ObservableObjectValue<T> {

    /**
     * Returns a new {@code ObjectBinding} that wraps around the given observable and uses the given converter to convert its value.
     *
     * @param observable the observable to be wrapped
     * @param converter the converter to convert the {@code boolean} value to type {@code T}
     * @param <T> the target type
     *
     * @return the wrapper binding
     *
     * @since 1.0.0.0
     */
    public static <T> ObjectBinding<T> wrap(ObservableBooleanValue observable, BooleanFunction<T> converter) {
        return new ObjectBinding<>() {{
            BooleanChangeListener listener = (observable, oldValue, newValue) -> {
                T prevValue = this.value;
                this.value = converter.apply(newValue);

                if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
            };

            listener.onChanged(observable, observable.get(), observable.get());
            observable.addListener(listener);
        }};
    }

    /**
     * Returns a new {@code ObjectBinding} that wraps around the given observable and uses the given converter to convert its value.
     *
     * @param observable the observable to be wrapped
     * @param converter the converter to convert the {@code byte} value to type {@code T}
     * @param <T> the target type
     *
     * @return the wrapper binding
     *
     * @since 1.0.0.0
     */
    public static <T> ObjectBinding<T> wrap(ObservableByteValue observable, ByteFunction<T> converter) {
        return new ObjectBinding<>() {{
            ByteChangeListener listener = (observable, oldValue, newValue) -> {
                T prevValue = this.value;
                this.value = converter.apply(newValue);

                if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
            };

            listener.onChanged(observable, observable.get(), observable.get());
            observable.addListener(listener);
        }};
    }

    /**
     * Returns a new {@code ObjectBinding} that wraps around the given observable and uses the given converter to convert its value.
     *
     * @param observable the observable to be wrapped
     * @param converter the converter to convert the {@code char} value to type {@code T}
     * @param <T> the target type
     *
     * @return the wrapper binding
     *
     * @since 1.0.0.0
     */
    public static <T> ObjectBinding<T> wrap(ObservableCharValue observable, CharFunction<T> converter) {
        return new ObjectBinding<>() {{
            CharChangeListener listener = (observable, oldValue, newValue) -> {
                T prevValue = this.value;
                this.value = converter.apply(newValue);

                if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
            };

            listener.onChanged(observable, observable.get(), observable.get());
            observable.addListener(listener);
        }};
    }

    /**
     * Returns a new {@code ObjectBinding} that wraps around the given observable and uses the given converter to convert its value.
     *
     * @param observable the observable to be wrapped
     * @param converter the converter to convert the {@code double} value to type {@code T}
     * @param <T> the target type
     *
     * @return the wrapper binding
     *
     * @since 1.0.0.0
     */
    public static <T> ObjectBinding<T> wrap(ObservableDoubleValue observable, DoubleFunction<T> converter) {
        return new ObjectBinding<>() {{
            DoubleChangeListener listener = (observable, oldValue, newValue) -> {
                T prevValue = this.value;
                this.value = converter.apply(newValue);

                if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
            };

            listener.onChanged(observable, observable.get(), observable.get());
            observable.addListener(listener);
        }};
    }

    /**
     * Returns a new {@code ObjectBinding} that wraps around the given observable and uses the given converter to convert its value.
     *
     * @param observable the observable to be wrapped
     * @param converter the converter to convert the {@code float} value to type {@code T}
     * @param <T> the target type
     *
     * @return the wrapper binding
     *
     * @since 1.0.0.0
     */
    public static <T> ObjectBinding<T> wrap(ObservableFloatValue observable, FloatFunction<T> converter) {
        return new ObjectBinding<>() {{
            FloatChangeListener listener = (observable, oldValue, newValue) -> {
                T prevValue = this.value;
                this.value = converter.apply(newValue);

                if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
            };

            listener.onChanged(observable, observable.get(), observable.get());
            observable.addListener(listener);
        }};
    }

    /**
     * Returns a new {@code ObjectBinding} that wraps around the given observable and uses the given converter to convert its value.
     *
     * @param observable the observable to be wrapped
     * @param converter the converter to convert the {@code int} value to type {@code T}
     * @param <T> the target type
     *
     * @return the wrapper binding
     *
     * @since 1.0.0.0
     */
    public static <T> ObjectBinding<T> wrap(ObservableIntValue observable, IntFunction<T> converter) {
        return new ObjectBinding<>() {{
            IntChangeListener listener = (observable, oldValue, newValue) -> {
                T prevValue = this.value;
                this.value = converter.apply(newValue);

                if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
            };

            listener.onChanged(observable, observable.get(), observable.get());
            observable.addListener(listener);
        }};
    }

    /**
     * Returns a new {@code ObjectBinding} that wraps around the given observable and uses the given converter to convert its value.
     *
     * @param observable the observable to be wrapped
     * @param converter the converter to convert the {@code long} value to type {@code T}
     * @param <T> the target type
     *
     * @return the wrapper binding
     *
     * @since 1.0.0.0
     */
    public static <T> ObjectBinding<T> wrap(ObservableLongValue observable, LongFunction<T> converter) {
        return new ObjectBinding<>() {{
            LongChangeListener listener = (observable, oldValue, newValue) -> {
                T prevValue = this.value;
                this.value = converter.apply(newValue);

                if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
            };

            listener.onChanged(observable, observable.get(), observable.get());
            observable.addListener(listener);
        }};
    }

    /**
     * Returns a new {@code ObjectBinding} that wraps around the given observable and uses the given converter to convert its value.
     *
     * @param observable the observable to be wrapped
     * @param converter the converter to convert the {@code U} value to type {@code T}
     * @param <T> the target type
     * @param <U> the origin type
     *
     * @return the wrapper binding
     *
     * @since 1.0.0.0
     */
    public static <T, U> ObjectBinding<T> wrap(ObservableValue<U> observable, Function<U, T> converter) {
        return new ObjectBinding<>() {{
            ChangeListener<U> listener = (observable, oldValue, newValue) -> {
                T prevValue = this.value;
                this.value = converter.apply(newValue);

                if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
            };

            listener.onChanged(observable, observable.getValue(), observable.getValue());
            observable.addListener(listener);
        }};
    }

    /**
     * Returns a new {@code ObjectBinding} that wraps around the given observable and uses the given converter to convert its value.
     *
     * @param observable the observable to be wrapped
     * @param converter the converter to convert the {@code short} value to type {@code T}
     * @param <T> the target type
     *
     * @return the wrapper binding
     *
     * @since 1.0.0.0
     */
    public static <T> ObjectBinding<T> wrap(ObservableShortValue observable, ShortFunction<T> converter) {
        return new ObjectBinding<>() {{
            ShortChangeListener listener = (observable, oldValue, newValue) -> {
                T prevValue = this.value;
                this.value = converter.apply(newValue);

                if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
            };

            listener.onChanged(observable, observable.get(), observable.get());
            observable.addListener(listener);
        }};
    }

    // #########################################################################################################################################################
    // # Instance Fields #######################################################################################################################################
    // #########################################################################################################################################################

    /**
     * The list of ChangeListeners attached to this binding.
     *
     * @since 1.0.0.0
     */
    protected List<ChangeListener<? super T>> changeListeners;
    /**
     * The current value of this binding.
     *
     * @since 1.0.0.0
     */
    protected T value;

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

    // #########################################################################################################################################################
    // # Listening #############################################################################################################################################
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