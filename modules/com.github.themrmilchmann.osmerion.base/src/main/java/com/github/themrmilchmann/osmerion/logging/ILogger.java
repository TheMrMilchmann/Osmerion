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
 * TODO doc
 *
 * @see ILogListener
 * @see LogLevel
 * @see LogMessage
 *
 * @author Leon Linhart
 * @since 1.0.0
 */
public interface ILogger {

    // #########################################################################################################################################################
    // # Logging ###############################################################################################################################################
    // #########################################################################################################################################################

    /**
     * Logs a message.
     *
     * @param level the level to be logged at
     * @param message the message to be logged
     *
     * @see LogLevel
     *
     * @since 1.0.0
     */
    void log(LogLevel level, String message);

    /**
     * Logs a message.
     *
     * <p>The message is only constructed if the {@code ILogger} is currently accepting messages from the given {@code LogLevel} by invoking
     * the provided supplier function.</p>
     *
     * @param level the level to be logged at
     * @param messageSupplier a function which produces the desired log message when called
     *
     * @see LogLevel
     *
     * @since 1.0.0
     */
    void log(LogLevel level, Supplier<String> messageSupplier);

    /**
     * Logs a message.
     *
     * @param level the level to be logged at
     * @param t the throwable to be logged
     *
     * @see LogLevel
     *
     * @since 1.0.0
     */
    void log(LogLevel level, Throwable t);

    /**
     * Logs a message.
     *
     * @param level the level to be logged at
     * @param message the message to be logged
     * @param t the throwable to be logged
     *
     * @see LogLevel
     *
     * @since 1.0.0
     */
    void log(LogLevel level, String message, Throwable t);

    /**
     * Logs a message.
     *
     * <p>The message is only constructed if the {@code ILogger} is currently accepting messages from the given {@code LogLevel} by invoking
     * the provided supplier function.</p>
     *
     * @param level the level to be logged at
     * @param messageSupplier a function which produces the desired log message when called
     * @param t the throwable to be logged
     *
     * @see LogLevel
     *
     * @since 1.0.0
     */
    void log(LogLevel level, Supplier<String> messageSupplier, Throwable t);

    // #########################################################################################################################################################
    // # Configuration #########################################################################################################################################
    // #########################################################################################################################################################

    /**
     * Returns an immutable view of this loggers accepted {@link LogLevel}s or {@code null} if this logger does not filter {@code LogLevel}s.
     *
     * @return an immutable view of this loggers accepted {@code LogLevel}s or {@code null} if this logger does not filter {@code LogLevel}s
     *
     * @since 1.0.0
     */
    List<LogLevel> getLogLevels();

    /**
     * Returns {@code true} if this logger is currently configured to redirect messages logged at the {@code level} to its listeners.
     *
     * @param level the level the configuration is to be checked for
     * @return {@code true} if this logger is currently configured to redirect messages logged at the {@code level} to its listeners.
     *
     * @see LogLevel
     *
     * @since 1.0.0
     */
    default boolean isEnabled(LogLevel level) {
        List<LogLevel> logLevels = this.getLogLevels();

        return logLevels == null || logLevels.contains(level);
    }

    // #########################################################################################################################################################
    // # Listening #############################################################################################################################################
    // #########################################################################################################################################################

    /**
     * Adds an {@link ILogListener} to handle this logger's {@link LogMessage}s.
     *
     * @param listener the listener to be added
     *
     * @throws NullPointerException if the given {@code listener} is {@code null}
     * @throws IllegalArgumentException if the given {@code listener} has already been added to this logger
     *
     * @see ILogListener
     * @see #removeListener(ILogListener)
     *
     * @since 1.0.0
     */
    void addListener(ILogListener listener);

    /**
     * Removes a previously added {@link ILogListener}.
     *
     * @param listener the listener to be removed
     *
     * @throws NullPointerException if the given {@code listener} is {@code null}
     * @throws IllegalArgumentException if the given {@code listener} has already been removed from this logger
     *
     * @see ILogListener
     * @see #addListener(ILogListener)
     *
     * @since 1.0.0
     */
    void removeListener(ILogListener listener);

}