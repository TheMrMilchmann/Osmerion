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

import com.github.themrmilchmann.osmerion.bean.value.change.ChangeListener;

/**
 * A basic read-only property wrapper.
 *
 * <p>Method calls are redirected to the underlying property.</p>
 *
 * <p>Not extending this class in a custom read-only wrapper may break the default behaviour of {@link ReadOnlyProperty#asReadOnlyProperty()}.</p>
 *
 * @param <T> the type of the value
 * @param <P> the type of the property being wrapped
 *
 * @author Leon Linhart
 * @since 1.0.0.0
 */
public abstract class ReadOnlyWrapper<T, P extends ReadOnlyProperty<T>> implements ReadOnlyProperty<T> {

    /**
     * The wrapped property.
     *
     * @since 1.0.0.0
     */
	protected P property;

    /**
     * Creates a new read-only wrapper wrapping around the specified property.
     *
     * @param property the property to be wrapped
     *
     * @since 1.0.0.0
     */
	protected ReadOnlyWrapper(P property) {
		this.property = property;
	}

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
	@Override
	public final T getValue() {
		return this.property.getValue();
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
		this.property.addListener(listener);
	}

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
	@Override
	public final void removeListener(ChangeListener<? super T> listener) {
		this.property.removeListener(listener);
	}

}