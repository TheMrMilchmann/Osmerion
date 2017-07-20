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
private const val CAT_M_CONSTRUCTORS    = "2_"
private const val CAT_M_VALOPS          = "3_Value Operations"
private const val CAT_M_LISTENERS       = "4_Listeners"

val AbstractProperty = Profile {
    types.forEach {
        val t_value = it

        javaClass(name(t_value), packageName, MODULE_BASE, superClass = ParametrizedType("Property", packageName, t_value.boxedType.simpleName), visibility = Modifier.PUBLIC.or(Modifier.ABSTRACT)) {
            addInterfaces(ReadOnlyProperty(t_value))
            addInterfaces(WritableValue(t_value))

            addImport(Import(getOsmerionPath("bean.value.change"), "*"))
            addImport(Import("java.util", "ArrayList"))

            documentation = "A basic {@code $t_value} property."
            see(SimpleProperty(t_value).toString())
            authors(AUTHOR_LEON_LINHART)
            since = VERSION_1_0_0_0

            t_value.field(
                "INITIAL_VALUE",
                "The initial value of an $fileName.",

                visibility = Modifier.PUBLIC.or(Modifier.STATIC).or(Modifier.FINAL),
                category = CAT_F_CONSTANTS,
                since = VERSION_1_0_0_0,
                value = t_value.nullValue
            )

            List(ChangeListener(t_value).toString()).field(
                "changeListeners",
                "The list of ChangeListeners attached to this property.",

                visibility = Modifier.PROTECTED,
                category = CAT_F_INSTANCE,
                since = VERSION_1_0_0_0
            )

            t_value.field(
                "value",
                "The current value of this property.",

                visibility = Modifier.PROTECTED,
                category = CAT_F_INSTANCE,
                since = VERSION_1_0_0_0
            )

            constructor(
                "Creates a new {@link $this} with the default initial value {@link #INITIAL_VALUE}",

                visibility = Modifier.PROTECTED,
                category = CAT_M_CONSTRUCTORS,
                since = VERSION_1_0_0_0,
                body = """
this(INITIAL_VALUE);
"""
            )

            constructor(
                "Creates a new {@link $this} with specified initial value.",

                t_value.PARAM("initialValue", "the initial value for this property"),

                visibility = Modifier.PROTECTED,
                category = CAT_M_CONSTRUCTORS,
                since = VERSION_1_0_0_0,
                body = """
this.value = initialValue;
"""
            )

            // #################################################################################################################################################
            // # VALOPS ########################################################################################################################################
            // #################################################################################################################################################

            t_value.method(
                "get",
                inheritDoc,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                category = CAT_M_VALOPS,
                since = VERSION_1_0_0_0,
                body = """
return this.value;
"""
            )

            t_value.boxedType.method(
                "getValue",
                inheritDoc,

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                category = CAT_M_VALOPS,
                since = VERSION_1_0_0_0,
                body = """
return this.get();
"""
            )

            t_value.method(
                "set",
                inheritDoc,

                t_value.PARAM("value", ""),

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                category = CAT_M_VALOPS,
                since = VERSION_1_0_0_0,
                body = """
if (this.isBound()) throw new UnsupportedOperationException("A bound property's value may not be set explicitly");

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

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                category = CAT_M_VALOPS,
                since = VERSION_1_0_0_0,
                body = """
return this.set(value);
"""
            )

            t_value.method(
                "validate",
                "Validates the specified value and returns the result.",

                t_value.PARAM("value", "the value to be validated"),

                visibility = Modifier.PROTECTED.or(Modifier.ABSTRACT),
                category = CAT_M_VALOPS,
                returnDoc = "the validated value",
                since = VERSION_1_0_0_0
            )

            // #################################################################################################################################################
            // # LISTENERS #####################################################################################################################################
            // #################################################################################################################################################

            void.method(
                "addListener",
                inheritDoc,

                ChangeListener(t_value).PARAM("listener", ""),

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                category = CAT_M_LISTENERS,
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

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                category = CAT_M_LISTENERS,
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

                visibility = Modifier.PUBLIC.or(Modifier.FINAL),
                annotations = listOf(Override),
                category = CAT_M_LISTENERS,
                since = VERSION_1_0_0_0,
                body = """
if (listener == null) throw new NullPointerException();
if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
"""
            )
        }
    }
}