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
package com.github.themrmilchmann.osmerion.internal.generator.java

import java.util.*

val void = VoidType("Void", "void")

val boolean = PrimitiveType("Boolean", "boolean", "false", -1)
val byte = PrimitiveType("Byte", "byte", "0", 8)
val short = PrimitiveType("Short", "short", "0", 16)
val char = PrimitiveType("Character", "char", "'\\u0000'", 32, "Char")
val int = PrimitiveType("Integer", "int", "0", 32, "Int")
val float = PrimitiveType("Float", "float", "0F", 32)
val long = PrimitiveType("Long", "long", "0L", 64)
val double = PrimitiveType("Double", "double", "0D", 64)

/* Frequently used type references */

fun Deprecated(since: String = "", forRemoval: Boolean = false): Annotation {
    var parameters = ""

    if (since.isNotEmpty() || forRemoval) {
        parameters = StringJoiner(", ").run {
            if (since.isNotEmpty()) add("since = $since")
            if (forRemoval) add("forRemoval = true")

            toString()
        }
    }

    return Annotation("Deprecated", "java.lang", parameters = parameters)
}
val FunctionalInterface = Annotation("FunctionalInterface", "java.lang")
val Override = Annotation("Override", "java.lang")

val Object = Type("Object", "java.lang")
val String = Type("String", "java.lang")

class Annotation(
    simpleName: String,
    packageName: String,
    val parameters: String = ""
): Type(simpleName, packageName)

class PrimitiveType(
    val boxedType: Type,
    simpleName: String,
    packageName: String,
    val nullValue: String,
    val size: Int,
    val abbrevName: String
): Type(simpleName, packageName) {

    constructor(boxedName: String, simpleName: String, nullValue: String, size: Int, abbrevName: String = boxedName):
        this(Type(boxedName, "java.lang"), simpleName, "java.lang", nullValue, size, abbrevName)

}

class VoidType(
    val boxedType: Type,
    simpleName: String,
    packageName: String
): Type(simpleName, packageName) {

    constructor(boxedName: String, simpleName: String):
        this(Type(boxedName, "java.lang"), simpleName, "java.lang")

}

class GenericType(
    simpleName: String
): Type(simpleName, "java.lang") // Use "java.lang" as package to trick import detection

open class Type(
    val simpleName: String,
    val packageName: String
) {

    fun getQualifiedName() = "$packageName.$simpleName"

    override fun toString() = simpleName

}

/* Primitive type conversion */

fun cast(from: Type, to: Type, value: String): String {
    if (from is PrimitiveType && to is PrimitiveType) {
        when (to) {
            byte -> when (from) {
                short, int, char, long -> return "(${to.simpleName}) $value"
            }
            short -> when (from) {
                int, char, long -> return "(${to.simpleName}) $value"
            }
            int -> when (from) {
                long, float, double -> return "(${to.simpleName}) $value"
            }
            char -> when(from) {
                byte, short, int, long, float, double -> return "(${to.simpleName}) $value"
            }
            long -> when (from) {
                float, double -> return "(${to.simpleName}) $value"
            }
            float -> when (from) {
                double -> return "(${to.simpleName}) $value"
            }
        }

        return value
    }

    return "(${to.simpleName}) $value"
}

fun convert(from: Type, to: Type, value: String): String {
    if (from is PrimitiveType && to is PrimitiveType) {
        when (from) {
            boolean -> when (to) {
                byte, short, int, float, double, long -> return cast(int, to, "($value ? 1 : ${to.nullValue})")
                char -> return "$value ? '\\u0001' : ${to.nullValue}"
            }
            float -> when (to) {
                byte, short -> return convert(int, to, "Float.floatToRawIntBits($value)")
                int, long -> return "Float.floatToRawIntBits($value)"
            }
            double -> when (to) {
                byte, short, int -> return convert(long, to, "Double.doubleToRawLongBits($value)")
                long -> return "Double.doubleToRawLongBits($value)"
            }
        }

        when (to) {
            boolean -> return "$value != ${from.nullValue}"
            float -> when (from) {
                byte, short, int, char -> return "Float.intBitsToFloat($value)"
            }
            double -> when (from) {
                byte, short, int, char, long -> return "Double.longBitsToDouble($value)"
            }
        }
    }

    return cast(from, to, value)
}

fun smaller(alpha: PrimitiveType, beta: PrimitiveType) = if (alpha.size < beta.size) alpha else beta;
fun larger(alpha: PrimitiveType, beta: PrimitiveType) = if (alpha.size > beta.size) alpha else beta;