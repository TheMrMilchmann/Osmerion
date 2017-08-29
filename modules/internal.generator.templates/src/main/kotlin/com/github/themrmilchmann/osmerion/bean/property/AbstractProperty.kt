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
package com.github.themrmilchmann.osmerion.bean.property

import com.github.themrmilchmann.osmerion.bean.value.*
import com.github.themrmilchmann.osmerion.bean.value.change.*
import com.github.themrmilchmann.osmerion.internal.generator.*
import com.github.themrmilchmann.osmerion.internal.generator.java.*
import com.github.themrmilchmann.osmerion.internal.generator.java.Type
import java.lang.reflect.*

private fun name(type: PrimitiveType) = "Abstract${type.abbrevName}Property"
fun AbstractProperty(type: PrimitiveType) = if (types.contains(type)) Type(name(type), packageName) else throw IllegalArgumentException("")

private const val CAT_F_CONSTANTS       = "0_"
private const val CAT_F_INSTANCE        = "1_"
private const val CAT_F_INSTANCE_B      = "2_"
private const val CAT_M_CONSTRUCTORS    = "3_"
private const val CAT_M_VALOPS          = "4_Value Operations"
private const val CAT_M_BINDING         = "5_Binding"
private const val CAT_M_LISTENERS       = "6_Listeners"

val AbstractProperty = Profile {
    types.forEach {
        val t_value = it

        javaClass(name(t_value), packageName, MODULE_BASE, superClass = ParametrizedType("Property", packageName, t_value.boxedType.simpleName), visibility = Modifier.PUBLIC.or(Modifier.ABSTRACT)) {
            addImport(Import(getOsmerionPath("bean.value.change"), "*"))
            addImport(Import("java.util", "ArrayList"))

            addInterfaces(ReadOnlyProperty(t_value))
            addInterfaces(WritableValue(t_value))

            documentation = "A basic {@code $t_value} property."
            see(SimpleProperty(t_value).toString())
            authors(AUTHOR_LEON_LINHART)
            since = VERSION_1_0_0_0

            t_value.field(
                "INITIAL_VALUE",
                "The initial value of an $fileName.",

                category = CAT_F_CONSTANTS,

                visibility = Modifier.PUBLIC.or(Modifier.STATIC).or(Modifier.FINAL),
                since = VERSION_1_0_0_0,

                value = t_value.nullValue
            )

            List(ChangeListener(t_value).toString()).field(
                "changeListeners",
                "The list of ChangeListeners attached to this property.",

                category = CAT_F_INSTANCE,

                visibility = Modifier.PROTECTED,
                since = VERSION_1_0_0_0
            )

            t_value.field(
                "value",
                "The current value of this property.",

                category = CAT_F_INSTANCE,

                visibility = Modifier.PROTECTED,
                since = VERSION_1_0_0_0
            )

            ObservableValue(t_value).field(
                "binding",
                "",

                category = CAT_F_INSTANCE_B,

                visibility = Modifier.PRIVATE
            )

            ChangeListener(t_value).field(
                "bindingListener",
                "",

                category = CAT_F_INSTANCE_B,

                visibility = Modifier.PRIVATE
            )

            constructor(
                "Creates a new {@link $this} with the default initial value {@link #INITIAL_VALUE}",

                category = CAT_M_CONSTRUCTORS,

                visibility = Modifier.PROTECTED,
                since = VERSION_1_0_0_0,

                body = "this(INITIAL_VALUE);"
            )

            constructor(
                "Creates a new {@link $this} with specified initial value.",

                t_value.PARAM("initialValue", "the initial value for this property"),

                category = CAT_M_CONSTRUCTORS,

                visibility = Modifier.PROTECTED,
                since = VERSION_1_0_0_0,

                body = "this.value = initialValue;"
            )

            // #################################################################################################################################################
            // # VALOPS ########################################################################################################################################
            // #################################################################################################################################################

            t_value.method(
                "get",
                inheritDoc,

                category = CAT_M_VALOPS,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                since = VERSION_1_0_0_0,

                body = "return this.value;"
            )

            t_value.boxedType.method(
                "getValue",
                inheritDoc,

                category = CAT_M_VALOPS,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                since = VERSION_1_0_0_0,

                body = "return this.get();"
            )

            t_value.method(
                "set",
                inheritDoc,

                t_value.PARAM("value", ""),

                category = CAT_M_VALOPS,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                since = VERSION_1_0_0_0,
                throws = arrayOf("IllegalStateException if the property is bound to a value"),

                body = """
if (this.isBound()) throw new IllegalStateException("A bound property's value may not be set explicitly");

return this.setImpl(value);
"""
            )

            t_value.method(
                "setImpl",
                "",

                t_value.PARAM("value", ""),

                category = CAT_M_VALOPS,

                visibility = Modifier.PRIVATE,

                body = """
$t_value oldValue = this.value;
value = this.validate(value);

if (oldValue != value) {
    this.value = value;

    if (this.changeListeners != null) this.changeListeners.forEach(listener -> listener.onChanged(this, oldValue, this.value));
}

return oldValue;
"""
            )

            t_value.boxedType.method(
                "setValue",
                inheritDoc,

                t_value.boxedType.PARAM("value", ""),

                category = CAT_M_VALOPS,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                since = VERSION_1_0_0_0,
                throws = arrayOf("IllegalStateException if the property is bound to a value"),

                body = "return this.set(value);"
            )

            t_value.method(
                "validate",
                "Validates the specified value and returns the result.",

                t_value.PARAM("value", "the value to be validated"),

                category = CAT_M_VALOPS,

                visibility = Modifier.PROTECTED.or(Modifier.ABSTRACT),
                returnDoc = "the validated value",
                since = VERSION_1_0_0_0
            )

            // #################################################################################################################################################
            // # Binding #######################################################################################################################################
            // #################################################################################################################################################

            void.method(
                "bind",
                """
                Binds this property's value to the value of a given {@link ${ObservableValue(t_value)}}. When a property is bound to a value it will always
                mirror the validated ({@link #validate($t_value)}) version of that value. Any attempt to set the value of a bound property explicitly will fail.
                A bound property may be unbound again by calling {@link #unbind()}.
                """,

                ObservableValue(t_value).PARAM("other", "the observable value to bind this property to"),

                category = CAT_M_BINDING,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                since = VERSION_1_0_0_0,
                throws = arrayOf(
                    "NullPointerException if the given {@code ObservableValue} is null",
                    "IllegalStateException if this property is already bound to a value"
                ),

                body = """
if (other == null) throw new NullPointerException("The value to bind a property to may not be null!");
if (this.binding != null) throw new IllegalStateException("The property is already bound to a value!");

this.binding = other;
this.binding.addListener(this.bindingListener = (observable, oldValue, newValue) -> this.setImpl(newValue));
"""
            )

            boolean.method(
                "isBound",
                inheritDoc,

                category = CAT_M_BINDING,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                since = VERSION_1_0_0_0,

                body = "return this.binding != null;"
            )

            void.method(
                "unbind",
                inheritDoc,

                category = CAT_M_BINDING,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                since = VERSION_1_0_0_0,

                body = """
if (this.binding == null) throw new IllegalStateException("The property is not bound to a value!");

this.binding.removeListener(this.bindingListener);
this.binding = null;
"""
            )

            // #################################################################################################################################################
            // # LISTENERS #####################################################################################################################################
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