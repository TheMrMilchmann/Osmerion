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
import com.github.themrmilchmann.osmerion.bean.value.change.*

private fun name(type: JvmPrimitiveType) = "ReadOnly${type.abbrevName}Wrapper"
fun ReadOnlyWrapper(type: JvmPrimitiveType) =
    if (types.contains(type)) JvmTypeReference(name(type), packageName) else throw IllegalArgumentException("")

val ReadOnlyWrapper = Profile {
    types.forEach {
        val t_value = it
        val sup = JvmTypeReference("ReadOnlyWrapper", packageName, t_value.box, ReadOnlyProperty(t_value))

        final..javaClass(
            name(t_value),
            packageName,
            MODULE_BASE,
            SRCSET_MAIN_GEN,
            documentation = """
                A read-only wrapper for {@code $t_value} properties.

                Method calls are redirected to the underlying property.
                """,
            since = VERSION_1_0_0_0,
            superClass = sup,
            interfaces = arrayOf(ReadOnlyProperty(t_value)),
            copyrightHeader = COPYRIGHT_HEADER
        ) {
            authors(AUTHOR_LEON_LINHART)

            constructor(
                """
                Creates a new {@code ${this.scopeRoot.className}} wrapping around the given property.
                """,

                ReadOnlyProperty(t_value).PARAM("property", "the property to be wrapped"),

                since = VERSION_1_0_0_0,
                body = "super(property);"
            )

            group("Value Operations") {
                Override..
                    public..t_value(
                    "get",
                    inheritDoc,

                    since = VERSION_1_0_0_0,
                    body = "return this.property.get();"
                )
            }

            group("Binding") {
                Override..
                    public..boolean(
                    "isBound",
                    inheritDoc,

                    since = VERSION_1_0_0_0,
                    body = "return this.property.isBound();"
                )
            }

            group("Listeners") {
                Override..
                    public..void(
                    "addListener",
                    inheritDoc,

                    ChangeListener(t_value).PARAM("listener", ""),

                    since = VERSION_1_0_0_0,
                    body = "this.property.addListener(listener);"
                )

                Override..
                    public..void(
                    "removeListener",
                    inheritDoc,

                    ChangeListener(t_value).PARAM("listener", ""),

                    since = VERSION_1_0_0_0,
                    body = "this.property.removeListener(listener);"
                )
            }
        }
    }
}