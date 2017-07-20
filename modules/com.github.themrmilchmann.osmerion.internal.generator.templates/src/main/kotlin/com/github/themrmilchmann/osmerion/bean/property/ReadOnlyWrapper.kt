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

import com.github.themrmilchmann.osmerion.bean.value.change.*
import com.github.themrmilchmann.osmerion.internal.generator.*
import com.github.themrmilchmann.osmerion.internal.generator.java.*
import com.github.themrmilchmann.osmerion.internal.generator.java.Type
import java.lang.reflect.*

private fun name(type: PrimitiveType) = "ReadOnly${type.abbrevName}Wrapper"
fun ReadOnlyWrapper(type: PrimitiveType) = if (types.contains(type)) Type(name(type), packageName) else throw IllegalArgumentException("")

private const val CAT_VALUE_OPS = "1_Value Operations"
private const val CAT_LISTENERS = "2_Listeners"

val ReadOnlyWrapper = Profile {
    types.forEach {
        val t_value = it
        val sup = ParametrizedType("ReadOnlyWrapper", packageName, "${t_value.boxedType.simpleName}, ${ReadOnlyProperty(t_value)}")

        javaClass(name(t_value), packageName, MODULE_BASE, superClass = sup, visibility = Modifier.FINAL) {
            addInterfaces(ReadOnlyProperty(t_value))

            documentation = """
            A read-only wrapper for {@code $t_value} properties.

            Method calls are redirected to the underlying property.
            """
            authors(AUTHOR_LEON_LINHART)
            since = VERSION_1_0_0_0

            constructor(
                """
                Creates a new {@code $fileName} wrapping around the given property.
                """,

                ReadOnlyProperty(t_value).PARAM("property", "the property to be wrapped"),

                since = VERSION_1_0_0_0,
                body = """
super(property);
"""
            )

            t_value.method(
                "get",
                inheritDoc,

                visibility = Modifier.PUBLIC,
                annotations = listOf(Override),
                category = CAT_VALUE_OPS,
                since = VERSION_1_0_0_0,
                body = """
return this.property.get();
"""
            )

            void.method(
                "addListener",
                inheritDoc,

                ChangeListener(t_value).PARAM("listener", ""),

                visibility = Modifier.PUBLIC,
                annotations = listOf(Override),
                category = CAT_LISTENERS,
                since = VERSION_1_0_0_0,
                body = """
this.property.addListener(listener);
"""
            )

            void.method(
                "removeListener",
                inheritDoc,

                ChangeListener(t_value).PARAM("listener", ""),

                visibility = Modifier.PUBLIC,
                annotations = listOf(Override),
                category = CAT_LISTENERS,
                since = VERSION_1_0_0_0,
                body = """
this.property.removeListener(listener);
"""
            )
        }
    }
}