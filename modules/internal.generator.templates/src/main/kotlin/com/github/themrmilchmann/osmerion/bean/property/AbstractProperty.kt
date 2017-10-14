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

import com.github.themrmilchmann.kraton.*
import com.github.themrmilchmann.kraton.lang.java.*
import com.github.themrmilchmann.kraton.lang.jvm.*
import com.github.themrmilchmann.osmerion.*
import com.github.themrmilchmann.osmerion.bean.value.*
import com.github.themrmilchmann.osmerion.bean.value.change.*

private fun name(type: JvmPrimitiveType) = "Abstract${type.abbrevName}Property"
fun AbstractProperty(type: JvmPrimitiveType) = if (types.contains(type)) JvmTypeReference(name(type), packageName) else throw IllegalArgumentException("")

val AbstractProperty = Profile {
    types.forEach {
        val t_value = it

        public..abstract..javaClass(
            name(t_value),
            packageName,
            MODULE_BASE,
            SRCSET_MAIN_GEN,
            documentation = "A basic {@code $t_value} property.",
            since = VERSION_1_0_0_0,
            superClass = JvmTypeReference("Property", packageName, t_value.box),
            interfaces = arrayOf(
                ReadOnlyProperty(t_value),
                WritableValue(t_value)
            ),
            copyrightHeader = COPYRIGHT_HEADER
        ) {
            import(getOsmerionPath("bean.value.change"))
            import(java.util.ArrayList::class.asType)

            see(SimpleProperty(t_value).toString())
            authors(AUTHOR_LEON_LINHART)

            group {
                public..static..final..t_value(
                    "INITIAL_VALUE",
                    t_value.nullValue,
                    "The initial value of an ${scopeRoot.className}.",

                    since = VERSION_1_0_0_0
                )
            }

            group {
                protected..JvmTypeReference("List", "java.util", ChangeListener(t_value))(
                    "changeListeners",
                    null,
                    "The list of ChangeListeners attached to this property.",

                    since = VERSION_1_0_0_0
                )

                protected..t_value(
                    "value",
                    null,
                    "The current value of this property.",

                    since = VERSION_1_0_0_0
                )
            }

            group {
                private..ObservableValue(t_value)(
                    "binding",
                    null,
                    ""
                )

                private..ChangeListener(t_value)(
                    "bindingListener",
                    null,
                    ""
                )
            }

            group {
                protected..constructor(
                    "Creates a new {@link $this} with the default initial value {@link #INITIAL_VALUE}",

                    since = VERSION_1_0_0_0,
                    body = "this(INITIAL_VALUE);"
                )

                protected..constructor(
                    "Creates a new {@link $this} with specified initial value.",

                    t_value.PARAM("initialValue", "the initial value for this property"),

                    since = VERSION_1_0_0_0,
                    body = "this.value = initialValue;"
                )
            }


            // #################################################################################################################################################
            // # VALOPS ########################################################################################################################################
            // #################################################################################################################################################

            group("Value Operations") {
                Override..
                    public..final..t_value(
                    "get",
                    inheritDoc,

                    since = VERSION_1_0_0_0,
                    body = "return this.value;"
                )

                Override..
                    public..final..t_value.box(
                    "getValue",
                    inheritDoc,

                    since = VERSION_1_0_0_0,
                    body = "return this.get();"
                )

                Override..
                    public..final..t_value(
                    "set",
                    inheritDoc,

                    t_value.PARAM("value", ""),

                    since = VERSION_1_0_0_0,
                    exceptions = arrayOf(java.lang.IllegalStateException::class.asType to "if the property is bound to a value"),
                    body = """
if (this.isBound()) throw new IllegalStateException("A bound property's value may not be set explicitly");

return this.setImpl(value);
"""
                )

                private..t_value(
                    "setImpl",
                    "",

                    t_value.PARAM("value", ""),

                    body = """
${t_value.asString(scopeRoot)} oldValue = this.value;
value = this.validate(value);

if (oldValue != value) {
    this.value = value;

    if (this.changeListeners != null) this.changeListeners.forEach(listener -> listener.onChanged(this, oldValue, this.value));
}

return oldValue;
"""
                )

                Override..
                    public..final..t_value.box(
                    "setValue",
                    inheritDoc,

                    t_value.box.PARAM("value", ""),

                    since = VERSION_1_0_0_0,
                    exceptions = arrayOf(java.lang.IllegalStateException::class.asType to "if the property is bound to a value"),
                    body = "return this.set(value);"
                )

                protected..abstract..t_value(
                    "validate",
                    "Validates the specified value and returns the result.",

                    t_value.PARAM("value", "the value to be validated"),

                    returnDoc = "the validated value",
                    since = VERSION_1_0_0_0
                )
            }


            // #################################################################################################################################################
            // # Binding #######################################################################################################################################
            // #################################################################################################################################################

            group("Binding") {
                public..final..void(
                    "bind",
                    """
                Binds this property's value to the value of a given {@link ${ObservableValue(t_value)}}. When a property is bound to a value it will always
                mirror the validated ({@link #validate($t_value)}) version of that value. Any attempt to set the value of a bound property explicitly will fail.
                A bound property may be unbound again by calling {@link #unbind()}.
                """,

                    ObservableValue(t_value).PARAM("other", "the observable value to bind this property to"),

                    since = VERSION_1_0_0_0,
                    exceptions = arrayOf(
                        java.lang.NullPointerException::class.asType to "if the given {@code ObservableValue} is null",
                        java.lang.IllegalStateException::class.asType to "if this property is already bound to a value"
                    ),

                    body = """
if (other == null) throw new NullPointerException("The value to bind a property to may not be null!");
if (this.binding != null) throw new IllegalStateException("The property is already bound to a value!");

this.binding = other;
this.binding.addListener(this.bindingListener = (observable, oldValue, newValue) -> this.setImpl(newValue));
"""
                )

                public..final..boolean(
                    "isBound",
                    inheritDoc,

                    since = VERSION_1_0_0_0,
                    body = "return this.binding != null;"
                )

                public..final..void(
                    "unbind",
                    inheritDoc,

                    since = VERSION_1_0_0_0,
                    body = """
if (this.binding == null) throw new IllegalStateException("The property is not bound to a value!");

this.binding.removeListener(this.bindingListener);
this.binding = null;
"""
                )
            }

            // #################################################################################################################################################
            // # LISTENERS #####################################################################################################################################
            // #################################################################################################################################################

            group("Listeners") {
                Override..
                    public..final..void(
                    "addListener",
                    inheritDoc,

                    ChangeListener(t_value).PARAM("listener", ""),

                    see = arrayOf("#removeListener(${ChangeListener(t_value)})"),
                    since = VERSION_1_0_0_0,
                    body = """
if (listener == null) throw new NullPointerException();
if (this.changeListeners == null) this.changeListeners = new ArrayList<>(1);

this.changeListeners.add(listener);
"""
                )

                Override..
                    public..final..void(
                    "removeListener",
                    inheritDoc,

                    ChangeListener(t_value).PARAM("listener", ""),

                    since = VERSION_1_0_0_0,
                    body = """
if (listener == null) throw new NullPointerException();
if (!this.changeListeners.isEmpty()) this.changeListeners.remove(listener);
"""
                )

                Override..
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