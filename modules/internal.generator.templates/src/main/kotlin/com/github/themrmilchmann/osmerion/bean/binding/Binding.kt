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
package com.github.themrmilchmann.osmerion.bean.binding

import com.github.themrmilchmann.kraton.*
import com.github.themrmilchmann.kraton.lang.java.*
import com.github.themrmilchmann.kraton.lang.jvm.*
import com.github.themrmilchmann.osmerion.*
import com.github.themrmilchmann.osmerion.bean.value.*
import com.github.themrmilchmann.osmerion.bean.value.change.*
import com.github.themrmilchmann.osmerion.util.functional.function.*

private fun name(type: JvmPrimitiveType) = "${type.abbrevName}Binding"
fun Binding(type: JvmPrimitiveType) =
    if (types.contains(type)) JvmTypeReference(name(type), packageName) else throw IllegalArgumentException("")

val Binding = Profile {
    types.forEach {
        val t_value = it

        public..abstract..javaClass(
            name(t_value),
            packageName,
            MODULE_BASE,
            SRCSET_MAIN_GEN,
            documentation = "An {@code ${ObservableValue(t_value)}} that is used to keep track of another observable.",
            since = VERSION_1_0_0_0,
            interfaces = arrayOf(ObservableValue(t_value)),
            copyrightHeader = COPYRIGHT_HEADER
        ) {
            import(java.util.ArrayList::class.asType)
            import(getOsmerionPath("bean.value"))
            import(getOsmerionPath("bean.value.change"))
            import(getOsmerionPath("util.functional.function"))

            authors(AUTHOR_LEON_LINHART)

            group {
                types.filter { it != t_value }.forEach {
                    public..static..this.scopeRoot(
                        "wrap",
                        "Returns a new {@code ${name(t_value)}} that wraps around the given observable and uses the given converter to convert its value.",

                        ObservableValue(it).PARAM("observable", "the observable to be wrapped"),
                        FromToFunction(it, t_value).PARAM("converter", "the converter to convert the {@code $it} value to type {@code $t_value}"),

                        returnDoc = "the wrapper binding",
                        since = VERSION_1_0_0_0,

                        body = """
return new ${name(t_value)}() {{
    ${ChangeListener(it).asString(scopeRoot)} listener = (observable, oldValue, newValue) -> {
        ${t_value.asString(scopeRoot)} prevValue = this.value;
        this.value = converter.apply(newValue);

        if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
    };

    listener.onChanged(observable, observable.get(), observable.get());
    observable.addListener(listener);
}};
"""
                    )
                }

                public..static..this.scopeRoot(
                    "wrap",
                    "Returns a new {@code ${name(t_value)}} that wraps around the given observable and uses the given converter to convert its value.",

                    JvmTypeReference("ObservableValue", getOsmerionPath("bean.value"), JvmGenericType("T")).PARAM("observable", "the observable to be wrapped"),
                    JvmTypeReference(ToFunction(t_value).className, ToFunction(t_value).packageName, JvmGenericType("T")).PARAM("converter", "the converter to convert the {@code $it} value to type {@code $t_value}"),

                    typeParameters = arrayOf(JvmGenericType("T") to "the type of the value to be wrapped"),
                    returnDoc = "the wrapper binding",
                    since = VERSION_1_0_0_0,

                    body = """
return new ${name(t_value)}() {{
    ChangeListener<T> listener = (observable, oldValue, newValue) -> {
        ${t_value.asString(this.scopeRoot)} prevValue = this.value;
        this.value = converter.apply(newValue);

        if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
    };

    listener.onChanged(observable, observable.getValue(), observable.getValue());
    observable.addListener(listener);
}};
"""
                )
            }

            // #################################################################################################################################################
            // # Instance Fields ###############################################################################################################################
            // #################################################################################################################################################

            protected..JvmTypeReference("List", "java.util", ChangeListener(t_value))(
                "changeListeners",
                null,
                "The list of ChangeListeners attached to this binding.",

                since = VERSION_1_0_0_0
            )

            protected..t_value(
                "value",
                null,
                "",

                since = VERSION_1_0_0_0
            )

            group("Value Operations") {
                public..final..t_value(
                    "get",
                    inheritDoc,

                    since = VERSION_1_0_0_0,
                    body = "return this.value;"
                )
            }


            group("Listeners") {
                Annotate(JvmTypeReference("Override", null))..
                    public..final..void(
                    "addListener",
                    inheritDoc,

                    ChangeListener(t_value).PARAM("listener", ""),

                    see = arrayOf("#removeListener(${ChangeListener(t_value).asString(scopeRoot)})"),
                    since = VERSION_1_0_0_0,

                    body = """
if (listener == null) throw new NullPointerException();
if (this.changeListeners == null) this.changeListeners = new ArrayList<>(1);

this.changeListeners.add(listener);
"""
                )

                Annotate(JvmTypeReference("Override", null))..
                    public..final..void(
                    "removeListener",
                    inheritDoc,

                    ChangeListener(t_value).PARAM("listener", ""),

                    see = arrayOf("#addListener(${ChangeListener(t_value).asString(scopeRoot)})"),
                    since = VERSION_1_0_0_0,

                    body = """
if (listener == null) throw new NullPointerException();
if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
"""
                )

                Annotate(JvmTypeReference("Override", null))..
                    public..final..void(
                    "removeListener",
                    inheritDoc,

                    JvmTypeReference("ChangeListener", packageName, JvmGenericType("?", t_value.box, upperBounds = false)).PARAM("listener", ""),

                    since = VERSION_1_0_0_0,

                    body = """
if (listener == null) throw new NullPointerException();
if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
"""
                )
            }
        }
    }
}