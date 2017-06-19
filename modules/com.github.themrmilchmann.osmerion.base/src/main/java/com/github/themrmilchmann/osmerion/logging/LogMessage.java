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

/**
 * An immutable data class used to hold various information about a logged message.
 *
 * <p>A {@code LogMessage} is created by an {@link ILogger} {@code log(...)} method and passed to the loggers {@link ILogListener}s.</p>
 *
 * @see ILogger
 * @see ILogListener
 *
 * @author Leon Linhart
 * @since 1.0.0
 */
public final class LogMessage {

    private ILogger logger;
    private LogLevel level;
    private String message;
    private Throwable throwable;

    private final long constructionTime;

    LogMessage(ILogger logger, LogLevel level, String message, Throwable throwable) {
        this.logger = logger;
        this.level = level;
        this.message = message;
        this.throwable = throwable;

        this.constructionTime = System.currentTimeMillis();
    }

    /**
     * Returns the time at which this {@code LogMessage} has been constructed.
     *
     * <p>Capturing the construction time is the last thing happening during the construction of a {@code LogMessage}.</p>
     *
     * @return the time at which this {@code LogMessage} has been constructed.
     *
     * @since 1.0.0
     */
    public long getConstructionTime() {
        return this.constructionTime;
    }

    /**
     * Returns the {@link LogLevel} this of this {@code LogMessage}.
     *
     * @return the {@code LogLevel} of this {@code LogMessage}
     *
     * @since 1.0.0
     */
    public LogLevel getLevel() {
        return this.level;
    }

    /**
     * Returns the {@link ILogger} that constructed this {@code LogMessage}.
     *
     * @return the {@code ILogger} that constructed this {@code LogMessage}
     *
     * @since 1.0.0
     */
    public ILogger getLogger() {
        return this.logger;
    }

    /**
     * Returns the {@code String} message component of this {@code LogMessage} or {@code null} if no {@code message} has been attached.
     *
     * @return the {@code String} message component of this {@code LogMessage} or {@code null}
     *
     * @since 1.0.0
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Returns the {@link Throwable} attached to this {@code LogMessage} or {@code null} if no {@code Throwable} has been attached.
     *
     * @return the {@code Throwable} attached to this {@code LogMessage} or {@code null}
     *
     * @since 1.0.0
     */
    public Throwable getThrowable() {
        return this.throwable;
    }

    /**
     * Returns this message formatted by this messages {@link LogLevel}.
     *
     * @return this message formatted by this messages {@code LogLevel}
     *
     * @see LogLevel#format(LogMessage)
     *
     * @since 1.0.0
     */
    @Override
    public String toString() {
        return this.level.format(this);
    }

}