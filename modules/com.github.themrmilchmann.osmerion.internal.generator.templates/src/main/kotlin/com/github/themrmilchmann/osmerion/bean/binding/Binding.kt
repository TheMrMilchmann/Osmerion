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

import com.github.themrmilchmann.osmerion.bean.property.*
import com.github.themrmilchmann.osmerion.bean.value.*
import com.github.themrmilchmann.osmerion.bean.value.change.*
import com.github.themrmilchmann.osmerion.internal.generator.*
import com.github.themrmilchmann.osmerion.internal.generator.java.*
import com.github.themrmilchmann.osmerion.internal.generator.java.Type
import com.github.themrmilchmann.osmerion.util.functional.function.*
import java.lang.reflect.*

private fun name(type: PrimitiveType) = "${type.abbrevName}Binding"
fun Binding(type: PrimitiveType) = if (types.contains(type)) Type(name(type), packageName) else throw IllegalArgumentException("")

private const val CAT_M_STATIC          = "0_"
private const val CAT_F_INSTANCE        = "1_Instance Fields"
private const val CAT_M_VALOPS          = "2_Value Operations"
private const val CAT_M_LISTENERS       = "3_Listeners"

val Binding = Profile {
    types.forEach {
        val t_value = it

        javaClass(name(t_value), packageName, MODULE_BASE, visibility = Modifier.PUBLIC.or(Modifier.ABSTRACT)) {
            addImport(Import("java.util", "ArrayList"))
            addImport(Import(getOsmerionPath("bean.value"), "*"))
            addImport(Import(getOsmerionPath("bean.value.change"), "*"))
            addImport(Import(getOsmerionPath("util.functional.function"), "*"))

            addInterfaces(ObservableValue(t_value))

            documentation = "An {@code ${ObservableValue(t_value)}} that is used to keep track of another observable."
            authors(AUTHOR_LEON_LINHART)
            since = VERSION_1_0_0_0

            types.filter { it != t_value }.forEach {
                this.method(
                    "wrap",
                    "Returns a new {@code ${name(t_value)}} that wraps around the given observable and uses the given converter to convert its value.",

                    ObservableValue(it).PARAM("observable", "the observable to be wrapped"),
                    FromToFunction(it, t_value).PARAM("converter", "the converter to convert the {@code $it} value to type {@code $t_value}"),

                    category = CAT_M_STATIC,
                    preserveOrder = false,

                    visibility = Modifier.PUBLIC.or(Modifier.STATIC),
                    returnDoc = "the wrapper binding",
                    since = VERSION_1_0_0_0,

                    body = """
return new ${name(t_value)}() {{
    ${ChangeListener(it)} listener = (observable, oldValue, newValue) -> {
        $t_value prevValue = this.value;
        this.value = converter.apply(newValue);

        if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
    };

    listener.onChanged(observable, observable.get(), observable.get());
    observable.addListener(listener);
}};
"""
                )
            }

            this.method(
                "wrap",
                "Returns a new {@code ${name(t_value)}} that wraps around the given observable and uses the given converter to convert its value.",

                ParametrizedType("ObservableValue", getOsmerionPath("bean.value"), "T").PARAM("observable", "the observable to be wrapped"),
                ParametrizedType(ToFunction(t_value), "T").PARAM("converter", "the converter to convert the {@code $it} value to type {@code $t_value}"),

                category = CAT_M_STATIC,
                preserveOrder = false,

                visibility = Modifier.PUBLIC.or(Modifier.STATIC),
                typeParameters = arrayOf(JavaTypeParameter(GenericType("T"), "the type of the value to be wrapped")),
                returnDoc = "the wrapper binding",
                since = VERSION_1_0_0_0,

                body = """
return new ${name(t_value)}() {{
    ChangeListener<T> listener = (observable, oldValue, newValue) -> {
        $t_value prevValue = this.value;
        this.value = converter.apply(newValue);

        if (this.changeListeners != null) this.changeListeners.forEach(l -> l.onChanged(this, prevValue, this.value));
    };

    listener.onChanged(observable, observable.getValue(), observable.getValue());
    observable.addListener(listener);
}};
"""
            )

            // #################################################################################################################################################
            // # Instance Fields ###############################################################################################################################
            // #################################################################################################################################################

            ParametrizedType("List", "java.util", ChangeListener(t_value).simpleName).field(
                "changeListeners",
                "The list of ChangeListeners attached to this binding.",

                category = CAT_F_INSTANCE,

                visibility = Modifier.PROTECTED,
                since = VERSION_1_0_0_0
            )

            t_value.field(
                "value",
                "",

                category = CAT_F_INSTANCE,

                visibility = Modifier.PROTECTED,
                since = VERSION_1_0_0_0
            )

            // #################################################################################################################################################
            // # Value Operations ##############################################################################################################################
            // #################################################################################################################################################

            t_value.method(
                "get",
                inheritDoc,

                category = CAT_M_VALOPS,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                since = VERSION_1_0_0_0,
                body = "return this.value;"
            )

            // #################################################################################################################################################
            // # Listening #####################################################################################################################################
            // #################################################################################################################################################

            void.method(
                "addListener",
                inheritDoc,

                ChangeListener(t_value).PARAM("listener", ""),

                category = CAT_M_LISTENERS,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                see = arrayOf("#removeListener(${ChangeListener(t_value)})"),
                since = VERSION_1_0_0_0,

                body = """
if (listener == null) throw new NullPointerException();
if (this.changeListeners == null) this.changeListeners = new ArrayList<>(1);

this.changeListeners.add(listener);
"""
            )

            void.method(
                "removeListener",
                inheritDoc,

                ChangeListener(t_value).PARAM("listener", ""),

                category = CAT_M_LISTENERS,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                see = arrayOf("#addListener(${ChangeListener(t_value)})"),
                since = VERSION_1_0_0_0,

                body = """
if (listener == null) throw new NullPointerException();
if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
"""
            )

            void.method(
                "removeListener",
                inheritDoc,

                ParametrizedType("ChangeListener", packageName, "? super ${t_value.boxedType.simpleName}").PARAM("listener", ""),

                category = CAT_M_LISTENERS,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                since = VERSION_1_0_0_0,

                body = """
if (listener == null) throw new NullPointerException();
if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
"""
            )
        }
    }
}