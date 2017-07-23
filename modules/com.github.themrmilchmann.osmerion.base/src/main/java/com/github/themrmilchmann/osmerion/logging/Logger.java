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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * A simple {@code Logger} implementation.
 *
 * <p>The {@code Logger} may not be configured directly. To change the logger's configuration after it has been created by {@link Builder#build()}, the
 * respective {@link Handle} must be used.</p>
 *
 * <p>{@link ILogListener}s are guaranteed to be called in a thread-safe manner as long as they are not added to more than one logger. However, the thread from
 * which the the listeners are called may alter.</p>
 *
 * <p>A {@code Logger} may only be created by using a {@link Logger.Builder}.</p>
 *
 * @see Builder
 * @see ILogger
 * @see ILogListener
 * @see LogLevel
 * @see LogMessage
 *
 * @author Leon Linhart
 * @since 1.0.0.0
 */
public final class Logger implements ILogger {

    // #########################################################################################################################################################
    // # Logging ###############################################################################################################################################
    // #########################################################################################################################################################

    private ExecutorService service = Executors.newSingleThreadScheduledExecutor();

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public void log(LogLevel level, String message) {
        if (this.isEnabled(level)) {
            LogMessage logMessage = new LogMessage(this, level, message, null);
            this.log(logMessage);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public void log(LogLevel level, Supplier<String> messageSupplier) {
        if (this.isEnabled(level)) {
            LogMessage logMessage = new LogMessage(this, level, messageSupplier.get(), null);
            this.log(logMessage);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public void log(LogLevel level, Throwable t) {
        if (this.isEnabled(level)) {
            LogMessage logMessage = new LogMessage(this, level, null, t);
            this.log(logMessage);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public void log(LogLevel level, String message, Throwable t) {
        if (this.isEnabled(level)) {
            LogMessage logMessage = new LogMessage(this, level, message, t);
            this.log(logMessage);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public void log(LogLevel level, Supplier<String> messageSupplier, Throwable t) {
        if (this.isEnabled(level)) {
            LogMessage logMessage = new LogMessage(this, level, messageSupplier.get(), t);
            this.log(logMessage);
        }
    }

    private void log(LogMessage logMessage) {
        this.service.submit(() -> this.listeners.forEach(listener -> listener.onLogged(logMessage)));
    }

    // #########################################################################################################################################################
    // # Listening #############################################################################################################################################
    // #########################################################################################################################################################

    private final List<ILogListener> listeners = new ArrayList<>();

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public void addListener(ILogListener listener) {
        if (listener == null) throw new NullPointerException();
        if (this.listeners.contains(listener)) throw new IllegalArgumentException();

        this.listeners.add(listener);
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public void removeListener(ILogListener listener) {
        if (listener == null) throw new NullPointerException();
        if (!this.listeners.remove(listener)) throw new IllegalArgumentException();
    }

    // #########################################################################################################################################################
    // # Configuration #########################################################################################################################################
    // #########################################################################################################################################################

    private final Logger parent;
    private final List<LogLevel> logLevels;

    private volatile int severity;
    private volatile boolean useParentConfig;

    private Logger(Logger parent, List<LogLevel> logLevels) {
        this.parent = parent;
        this.logLevels = logLevels;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public List<LogLevel> getLogLevels() {
        return Collections.unmodifiableList(this.logLevels);
    }

    /**
     * Returns this loggers parent.
     *
     * @return this loggers parent
     *
     * @since 1.0.0.0
     */
    public Logger getParent() {
        return this.parent;
    }

    /**
     * Returns the numerical severity fo this {@link Logger}.
     *
     * <p>This serves as an additional filter of accepted messages once the accepted {@link LogLevel}s have been filtered out.</p>
     *
     * @return the numerical severity of this {@code LogLevel}
     *
     * @since 1.0.0.0
     */
    public int getSeverity() {
        return this.severity;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.0.0.0
     */
    @Override
    public boolean isEnabled(LogLevel level) {
        return this.useParentConfig ? this.parent.isEnabled(level) : this.logLevels.contains(level) && this.severity >= level.getSeverity();
    }

    /**
     * A factory class for loggers.
     *
     * @since 1.0.0.0
     */
    public static final class Builder {

        private final List<LogLevel> logLevels = new ArrayList<>();
        private final Logger parent;

        private int severity;
        private boolean useParentConfig;

        /**
         * Create a new builder that creates orphan {@code Logger}s (Loggers without a parent).
         *
         * @since 1.0.0.0
         */
        public Builder() {
            this(null);
        }

        /**
         * Create a new builder that uses the specified {@code Logger} as parent for its created {@code Logger}s.
         *
         * @param parent the logger to be used as parent
         *
         * @since 1.0.0.0
         */
        public Builder(Logger parent) {
            this.parent = parent;
        }

        /**
         * Creates a new {@link Logger} and returns its {@link Handle}.
         *
         * <p>This function may be called multiple times to create multiple loggers with the same configuration.</p>
         *
         * @return the newly initialized loggers {@code Logger.Handle}
         *
         * @since 1.0.0.0
         */
        public Handle build() {
            CopyOnWriteArrayList<LogLevel> logLevels = new CopyOnWriteArrayList<>(this.logLevels);
            Logger logger = new Logger(this.parent, logLevels);
            logger.severity = this.severity;
            logger.useParentConfig = this.useParentConfig;

            return new Handle(logger);
        }

        // #####################################################################################################################################################
        // # Configuration #####################################################################################################################################
        // #####################################################################################################################################################

        /**
         * Returns a mutable {@link List} of accepted {@link LogLevel}s.
         *
         * @return a mutable {@code List} of accepted {@code LogLevel}s
         *
         * @since 1.0.0.0
         */
        public List<LogLevel> getLogLevels() {
            return this.logLevels;
        }

        /**
         * Configures the severity for this builder.
         *
         * @param value the new severity
         *
         * @since 1.0.0.0
         */
        public void setSeverity(int value) {
            this.severity = value;
        }

        /**
         * Set whether this logger should use its parent's config.
         *
         * <p>The value is ignored if this logger has no parent.</p>
         *
         * @param value whether this logger should use its parent's config
         *
         * @since 1.0.0.0
         */
        public void setUseParentConfig(boolean value) {
            this.useParentConfig = value;
        }

    }

    /**
     * A {@link Logger}'s handle which allows for higher level access and configuration once the logger has been created.
     *
     * <p>While a {@code Logger.Handle} holds a strong reference to the {@code Logger} it's managing, the logger does not reference the handle in any way. Thus
     * the handle may be marked for garbage collection even if the logger is still use.</p>
     *
     * @since 1.0.0.0
     */
    public static final class Handle implements ILogProxy {

        private final Logger logger;

        private Handle(Logger logger) {
            this.logger = logger;
        }

        /**
         * Returns the {@link Logger} managed by this handle.
         *
         * @return the managed {@code Logger}
         *
         * @since 1.0.0.0
         */
        @Override
        public Logger getLogger() {
            return this.logger;
        }

        // #####################################################################################################################################################
        // # Configuration #####################################################################################################################################
        // #####################################################################################################################################################

        /**
         * Adds an {@link LogLevel} to this logger's list of accepted levels.
         *
         * <p><b>Note: Modifications to the {@code Logger}'s list of accepted {@code LogLevel}s are more expensive once the logger has been constructed. If
         * possible, the {@link Builder} should be used to configure the logger.</b></p>
         *
         * @param level the {@code LogLevel} to be added
         *
         * @throws NullPointerException if the given {@code listener} is {@code null}
         * @throws IllegalArgumentException if the given {@code listener} has already been added to this logger
         *
         * @see Builder#getLogLevels()
         *
         * @since 1.0.0.0
         */
        public void addLogLevel(LogLevel level) {
            if (level == null) throw new NullPointerException();
            if (this.logger.logLevels.contains(level)) throw new IllegalArgumentException();

            this.logger.logLevels.add(level);
        }

        /**
         * Removes an {@link LogLevel} from this logger's list of accepted levels.
         *
         * <p><b>Note: Modifications to the {@code Logger}'s list of accepted {@code LogLevel}s are more expensive once the logger has been constructed. If
         * possible, the {@link Builder} should be used to configure the logger.</b></p>
         *
         * @param level the {@code LogLevel} to be removed
         *
         * @throws NullPointerException if the given {@code level} is {@code null}
         * @throws IllegalArgumentException if the given {@code level} has already been removed from this logger
         *
         * @see Builder#getLogLevels()
         *
         * @since 1.0.0.0
         */
        public void removeLogLevel(LogLevel level) {
            if (level == null) throw new NullPointerException();
            if (!this.logger.logLevels.remove(level)) throw new IllegalArgumentException();
        }

        /**
         * Sets the severity of this {@code Logger} and returns a list containing all {@code LogLevel}s with which a method will now be passed to the Logger's
         * output listeners.
         *
         * @param value the new severity
         * @return a {@code List} of all {@code LogLevel}s with which a method will now be passed to the Logger's output listeners
         *
         * @since 1.0.0.0
         */
        public List<LogLevel> setSeverity(int value) {
            this.logger.severity = value;

            return this.logger.logLevels.stream().filter(this.logger::isEnabled).collect(Collectors.toList());
        }

        /**
         * Set whether this logger should use its parent's config.
         *
         * <p>The value is ignored if this logger has no parent.</p>
         *
         * @param value whether this logger should use its parent's config
         *
         * @since 1.0.0.0
         */
        public void setUseParentConfig(boolean value) {
            this.logger.useParentConfig = value && this.logger.parent != null;
        }

    }

}