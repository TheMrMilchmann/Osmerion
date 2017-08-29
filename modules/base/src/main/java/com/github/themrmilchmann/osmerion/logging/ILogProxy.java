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
package com.github.themrmilchmann.osmerion.logging;

import java.util.List;
import java.util.function.Supplier;

/**
 * Utility class that may be implemented by classes holding and exposing an {@code ILogger}.
 *
 * @see ILogger
 *
 * @author Leon Linhart
 * @since 1.0.0.0
 */
public interface ILogProxy extends ILogger {

    /**
     * Returns the underlying {@link ILogger}.
     *
     * @return the underlying {@code ILogger}.
     *
     * @since 1.0.0.0
     */
    ILogger getLogger();

    // #########################################################################################################################################################
    // # Logging ###############################################################################################################################################
    // #########################################################################################################################################################

    /**
     * {@inheritDoc}
     *
     * <p><b>Note: The call is redirected to the underlying logger.</b></p>
     *
     * @since 1.0.0.0
     */
    @Override
    default void log(LogLevel level, String message) {
        this.getLogger().log(level, message);
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>Note: The call is redirected to the underlying logger.</b></p>
     *
     * @since 1.0.0.0
     */
    @Override
    default void log(LogLevel level, Supplier<String> messageSupplier) {
        this.getLogger().log(level, messageSupplier);
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>Note: The call is redirected to the underlying logger.</b></p>
     *
     * @since 1.0.0.0
     */
    @Override
    default void log(LogLevel level, Throwable t) {
        this.getLogger().log(level, t);
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>Note: The call is redirected to the underlying logger.</b></p>
     *
     * @since 1.0.0.0
     */
    @Override
    default void log(LogLevel level, String message, Throwable t) {
        this.getLogger().log(level, message, t);
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>Note: The call is redirected to the underlying logger.</b></p>
     *
     * @since 1.0.0.0
     */
    @Override
    default void log(LogLevel level, Supplier<String> messageSupplier, Throwable t) {
        this.getLogger().log(level, messageSupplier, t);
    }

    // #########################################################################################################################################################
    // # Configuration #########################################################################################################################################
    // #########################################################################################################################################################

    /**
     * {@inheritDoc}
     *
     * <p><b>Note: The call is redirected to the underlying logger.</b></p>
     *
     * @since 1.0.0.0
     */
    @Override
    default List<LogLevel> getLogLevels() {
        return this.getLogger().getLogLevels();
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>Note: The call is redirected to the underlying logger.</b></p>
     *
     * @since 1.0.0.0
     */
    default boolean isEnabled(LogLevel level) {
        return this.getLogger().isEnabled(level);
    }

    // #########################################################################################################################################################
    // # Listening #############################################################################################################################################
    // #########################################################################################################################################################

    /**
     * {@inheritDoc}
     *
     * <p><b>Note: The call is redirected to the underlying logger.</b></p>
     *
     * @since 1.0.0.0
     */
    @Override
    default void addListener(ILogListener listener) {
        this.getLogger().addListener(listener);
    }

    /**
     * {@inheritDoc}
     *
     * <p><b>Note: The call is redirected to the underlying logger.</b></p>
     *
     * @since 1.0.0.0
     */
    @Override
    default void removeListener(ILogListener listener) {
        this.getLogger().removeListener(listener);
    }

}