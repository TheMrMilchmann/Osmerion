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

import com.github.themrmilchmann.osmerion.bean.value.change.*
import com.github.themrmilchmann.osmerion.internal.generator.*
import com.github.themrmilchmann.osmerion.internal.generator.java.*
import com.github.themrmilchmann.osmerion.internal.generator.java.Type
import java.lang.reflect.*

private fun name(type: PrimitiveType) = "Observable${type.abbrevName}Value"
fun ObservableValue(type: PrimitiveType) = if (types.contains(type)) Type(name(type), packageName) else throw IllegalArgumentException("")

private const val CAT_VALUE_OPS = "1_Value Operations"
private const val CAT_LISTENERS = "2_Listeners"

val ObservableValue = Profile {
    types.forEach {
        val t_value = it

        javaInterface(name(t_value), packageName, MODULE_BASE, visibility = Modifier.PUBLIC) {
            addInterfaces(ParametrizedType("ObservableValue", packageName, t_value.boxedType.simpleName))
            addImport(Import("$packageName.change", "*"))

            documentation = "An observable {@code ${t_value.simpleName}} value."
            authors(AUTHOR_LEON_LINHART)
            since = VERSION_1_0_0_0

            t_value.method(
                "get",
                "Returns the value of this {@link ${this.fileName}}.",

                category = CAT_VALUE_OPS,
                returnDoc = "the value of this {@code ${this.fileName}}",
                since = VERSION_1_0_0_0
            )

            t_value.boxedType.method(
                "getValue",
                inheritDoc,

                annotations = listOf(Override),
                category = CAT_VALUE_OPS,
                since = VERSION_1_0_0_0,
                body = """
return this.get();
"""
            )

            void.method(
                "addListener",
                """
                Attaches the specified listener to this {@link ObservableValue}.

                As long as the listener is attached it will be notified whenever the value of this {@code ObservableValue} changes via
                {@link ChangeListener#onChanged(ObservableValue, Object, Object)}.
                """,

                ChangeListener(t_value).PARAM("listener", "the listener to be attached to this {@code ObservableValue}"),

                category = CAT_LISTENERS,
                throws = arrayOf("NullPointerException if {@code listener} is {@code null}"),
                see = arrayOf("#removeListener(${ChangeListener(t_value)})"),
                since = VERSION_1_0_0_0
            )

            void.method(
                "addListener",
                inheritDoc,

                ParametrizedType("ChangeListener", packageName, "? super ${t_value.boxedType.simpleName}").PARAM("listener", ""),

                annotations = listOf(Override),
                category = CAT_LISTENERS,
                see = arrayOf("#addListener(${ChangeListener(t_value)})"),
                since = VERSION_1_0_0_0,
                body = """
this.addListener(${ChangeListener(t_value)}.wrap(listener));
"""
            )

            void.method(
                "removeListener",
                "Detaches the specified listener from this value.",

                ChangeListener(t_value).PARAM("listener", "the listener to be detached from this value"),

                category = CAT_LISTENERS,
                throws = arrayOf("NullPointerException if {@code listener} is {@code null}"),
                see = arrayOf("#addListener(${ChangeListener(t_value)})"),
                since = VERSION_1_0_0_0
            )
        }
    }
}