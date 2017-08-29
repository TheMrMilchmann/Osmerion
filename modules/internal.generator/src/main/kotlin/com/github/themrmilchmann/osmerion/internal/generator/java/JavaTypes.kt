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

import java.io.*
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
fun List(content: String) = ParametrizedType("List", "java.util", content)

open class Import(
    val packageName: String,
    var typeQualifier: String
): Comparable<Import> {

    constructor(type: IType): this(type.packageName, type.simpleName)

    override fun compareTo(other: Import): Int {
        if (packageName.startsWith("java.") && !other.packageName.startsWith("java.")) return -1
        if (!packageName.startsWith("java.") && other.packageName.startsWith("java.")) return 1
        if (packageName.startsWith("javax.") && !other.packageName.startsWith("javax.")) return -1
        if (!packageName.startsWith("javax.") && other.packageName.startsWith("javax.")) return 1

        val cmp = packageName.compareTo(other.packageName)
        if (cmp != 0) return cmp

        if (typeQualifier == "*" || other.typeQualifier == "*") return 0

        return typeQualifier.compareTo(other.typeQualifier)
    }

    open fun PrintWriter.printImport() = println("import ${this@Import};")

    override fun toString() = "$packageName.$typeQualifier"

}

class StaticImport(
    packageName: String,
    typeQualifier: String
): Import(packageName, typeQualifier) {

    override fun PrintWriter.printImport() = println("import static ${this@StaticImport};")

}

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

class ParametrizedType(
    simpleName: String,
    packageName: String,
    val parameters: String
): Type(simpleName, packageName) {

    constructor(type: IType, parameters: String): this(type.simpleName, type.packageName, parameters)

    override fun toString(): String = super.toString() + "<$parameters>"

}

open class Type(
    override val simpleName: String,
    override val packageName: String
): IType {

    override fun toString() = simpleName

}

interface IType {

    val simpleName: String
    val packageName: String

    fun getQualifiedName() = "$packageName.$simpleName"

}

/* Primitive type conversion */

fun cast(from: IType, to: IType, value: String): String {
    if (from is PrimitiveType && to is PrimitiveType) {
        when (to) {
            byte -> when (from) {
                short, int, char, long -> return "($to) $value"
            }
            short -> when (from) {
                int, char, long -> return "($to) $value"
            }
            int -> when (from) {
                long, float, double -> return "($to) $value"
            }
            char -> when(from) {
                byte, short, int, long, float, double -> return "($to) $value"
            }
            long -> when (from) {
                float, double -> return "($to) $value"
            }
            float -> when (from) {
                double -> return "($to) $value"
            }
        }

        return value
    }

    return "($to) $value"
}

fun convert(from: IType, to: IType, value: String): String {
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