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
 * A {@code LogLevel} that can be logged at.
 *
 * <p>The default formatted version of a {@link LogMessage} which can be acquired by calling {@link LogMessage#toString()} is formatted by its respective
 * {@code LogLevel}. To change this behaviour one can extend the {@code LogLevel} class and overwrite this {@link LogLevel#format(LogMessage)} method.</p>
 *
 * <p>An {@link ILogger} must be configured to accept a {@code LogLevel}.</p>
 *
 * @see ILogger#getLogLevels()
 * @see ILogger#isEnabled(LogLevel)
 *
 * @author Leon Linhart
 * @since 1.0.0
 */
public class LogLevel {

    private final String name;
    private final int severity;

    /**
     * Creates a new {@link LogLevel} with the given name and properties.
     *
     * @param name the name for this {@code LogLevel}
     * @param severity the numerical severity for this {@code LogLevel}
     *
     * @since 1.0.0
     */
    public LogLevel(String name, int severity) {
        this.name = name;
        this.severity = severity;
    }

    /**
     * Formats a given {@code LogMessage}.
     *
     * @param logMessage the {@code LogMessage} to be formatted
     * @return the formatted message
     *
     * @since 1.0.0
     */
    String format(LogMessage logMessage) {
        StringBuilder stringBuilder = new StringBuilder("[")
            .append(logMessage.getLevel().getName())
            .append("]");

        if (logMessage.getMessage() != null) {
            stringBuilder.append(" ");
            stringBuilder.append(logMessage.getMessage());
        }

        if (logMessage.getThrowable() != null) {
            stringBuilder.append("\n");
            stringBuilder.append(logMessage.getThrowable());
        }

        return stringBuilder.toString();
    }

    /**
     * Returns the name of this {@link LogLevel}.
     *
     * @return the name of this {@code LogLevel}
     *
     * @since 1.0.0
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the numerical severity fo this {@link LogLevel}.
     *
     * @return the numerical severity of this {@code LogLevel}
     *
     * @since 1.0.0
     */
    public int getSeverity() {
        return this.severity;
    }

}
