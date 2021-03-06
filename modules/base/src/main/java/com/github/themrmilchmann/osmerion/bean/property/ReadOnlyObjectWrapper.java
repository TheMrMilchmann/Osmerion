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

/**
 * A read-only wrapper for {@code Object} properties.
 *
 * <p>Method calls are redirected to the underlying property.</p>
 *
 * @author Leon Linhart
 * @since 1.0.0.0
 */
final class ReadOnlyObjectWrapper<T> extends ReadOnlyWrapper<T, ReadOnlyObjectProperty<T>> implements ReadOnlyObjectProperty<T> {

    /**
     * Creates a new {@code ReadOnlyObjectWrapper} wrapping around the given property.
     *
     * @param property the property to be wrapped
     *
     * @since 1.0.0.0
     */
    ReadOnlyObjectWrapper(ReadOnlyObjectProperty<T> property) {
        super(property);
    }

    // #############################################################################################################################################################
    // # Value Operations ##########################################################################################################################################
    // #############################################################################################################################################################

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public T get() {
        return this.property.get();
    }

    // #########################################################################################################################################################
    // # Binding ###############################################################################################################################################
    // #########################################################################################################################################################

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public boolean isBound() {
        return this.property.isBound();
    }

}