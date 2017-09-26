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
import com.github.themrmilchmann.osmerion.*

private fun name(type: JavaPrimitiveType) = "Writable${type.abbrevName}Value"
fun WritableValue(type: JavaPrimitiveType) = if (types.contains(type)) JavaTypeReference(name(type), packageName) else throw IllegalArgumentException("")

private const val CAT_VALUE_OPS = "1_Value Operations"

val WritableValue = Profile {
    types.forEach {
        val t_value = it

        public..javaInterface(
            name(t_value),
            packageName,
            MODULE_BASE,
            SRCSET_MAIN_GEN,
            documentation = "A writable {@code $t_value} value.",
            since = VERSION_1_0_0_0,
            superInterfaces = arrayOf(JavaTypeReference("WritableValue", packageName, t_value.boxedType), ObservableValue(t_value)),
            copyrightHeader = COPYRIGHT_HEADER
        ) {
            authors(AUTHOR_LEON_LINHART)

            t_value(
                "set",
                "Sets the value of this {@link ${this.className}}.",

                t_value.PARAM("value", "the new value"),

                category = CAT_VALUE_OPS,

                returnDoc = "the previous value of this {@code ${this.className}}",
                since = VERSION_1_0_0_0
            )
        }
    }
}