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
package com.github.themrmilchmann.osmerion.bean.value

import com.github.themrmilchmann.kraton.*
import com.github.themrmilchmann.kraton.lang.java.*
import com.github.themrmilchmann.kraton.lang.jvm.*
import com.github.themrmilchmann.osmerion.*
import com.github.themrmilchmann.osmerion.bean.value.change.*

private fun name(type: JvmPrimitiveType) = "Observable${type.abbrevName}Value"
fun ObservableValue(type: JvmPrimitiveType) = if (types.contains(type)) JvmTypeReference(name(type), packageName) else throw IllegalArgumentException("")

val ObservableValue = Profile {
    types.forEach {
        val t_value = it

        public..javaInterface(
            name(t_value),
            packageName,
            MODULE_BASE,
            SRCSET_MAIN_GEN,
            documentation = "An observable {@code $t_value} value.",
            since = VERSION_1_0_0_0,
            superInterfaces = arrayOf(JvmTypeReference("ObservableValue", packageName, t_value.box)),
            copyrightHeader = COPYRIGHT_HEADER
        ) {
            import("$packageName.change")

            see(WritableValue(t_value).toString())
            authors(AUTHOR_LEON_LINHART)

            group("Value Operations") {
                t_value(
                    "get",
                    "Returns the value of this {@link ${this.scopeRoot.className}}.",

                    returnDoc = "the value of this {@code ${this.scopeRoot.className}}",
                    since = VERSION_1_0_0_0
                )

                Override..
                    default..t_value.box(
                    "getValue",
                    inheritDoc,

                    since = VERSION_1_0_0_0,
                    body = """
return this.get();
"""
                )
            }

            group("Listeners") {
                void(
                    "addListener",
                    """
                Attaches the specified listener to this {@link ObservableValue}.

                As long as the listener is attached it will be notified whenever the value of this {@code ObservableValue} changes via
                {@link ChangeListener#onChanged(ObservableValue, Object, Object)}.
                """,

                    ChangeListener(t_value).PARAM("listener", "the listener to be attached to this {@code ObservableValue}"),

                    exceptions = arrayOf(java.lang.NullPointerException::class.asType to "if {@code listener} is {@code null}"),
                    see = arrayOf("#addListener(${ChangeListener(t_value).asString(scopeRoot)})"),
                    since = VERSION_1_0_0_0
                )

                Override..
                    default..void(
                    "addListener",
                    inheritDoc,

                    JvmTypeReference("ChangeListener", packageName, JvmGenericType("?", t_value.box, upperBounds = false)).PARAM("listener", ""),

                    see = arrayOf(
                        "#removeListener(${ChangeListener(t_value).asString(scopeRoot)})"),
                    since = VERSION_1_0_0_0,
                    body = """
this.addListener(${ChangeListener(t_value).asString(scopeRoot)}.wrap(listener));
"""
                )

                void(
                    "removeListener",
                    "Detaches the specified listener from this value.",

                    ChangeListener(t_value).PARAM("listener", "the listener to be detached from this value"),

                    exceptions = arrayOf(java.lang.NullPointerException::class.asType to "if {@code listener} is {@code null}"),
                    see = arrayOf("#addListener(${ChangeListener(t_value).asString(scopeRoot)})"),
                    since = VERSION_1_0_0_0
                )
            }
        }
    }
}