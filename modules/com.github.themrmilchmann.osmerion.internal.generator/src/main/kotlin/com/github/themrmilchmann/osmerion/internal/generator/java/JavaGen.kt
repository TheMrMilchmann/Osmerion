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
import java.lang.reflect.Modifier
import java.util.*

import com.github.themrmilchmann.osmerion.internal.generator.*

private val CATEGORY = "(\\d+)\\Q_\\E(.+)".toRegex()

private val WEIGHT_FIELD = 0
private val WEIGHT_METHOD = 1
private val WEIGHT_SUBTYPE = Integer.MAX_VALUE

interface Member : Comparable<Member> {

    var category: String
    val name: String

    fun getWeight(): Int

    fun PrintWriter.printMember(indent: String)

    override fun compareTo(other: Member) = name.compareTo(other.name)

}

abstract class JavaType(
    fileName: String,
    packageName: String,
    moduleName: String
): GeneratorTarget(fileName, "java", packageName, moduleName), Member {

    val annotations = mutableListOf<Annotation>()
    val body = TreeSet<Member> {
        alpha, beta ->

        val aC = alpha.category
        val bC = beta.category
        val cC = aC.compareTo(bC)
        val aW = alpha.getWeight()
        val bW = beta.getWeight()

        if (cC != 0) cC
        else if (aW > bW) 1
        else if (aW < bW) -1
        else alpha.compareTo(beta)
    }
    val imports = TreeSet<String> {
        alpha, beta ->

        val a = alpha.removePrefix("import ").removePrefix("static ")
        val b = alpha.removePrefix("import ").removePrefix("static ")

        if (a.startsWith("java.")) {
            if (b.startsWith("java.")) a.compareTo(b)
            else 1
        } else if (a.startsWith("javax.")) {
            if (b.startsWith("javax.")) a.compareTo(b)
            else 1
        } else a.compareTo(beta)
    }

    override var category: String = ""
    var authors: Array<out String>? = null
    var see: Array<out String>? = null
    var documentation: String = ""
    var since: String = ""

    fun authors(vararg authors: String) {
        this@JavaType.authors = authors
    }

    fun see(vararg see: String) {
        this@JavaType.see = see
    }

    fun addAnnotations(vararg annotations: Annotation) {
        this@JavaType.annotations.addAll(annotations)
    }

    fun addImports(vararg imports: String) {
        this@JavaType.imports.addAll(imports)
    }

    internal fun addInferredImport(type: Type) {
        if (packageName != type.packageName && type.packageName != "java.lang") {
            addImports("import ${type.getQualifiedName()};")
        }
    }

    override final fun PrintWriter.printMember(indent: String) = printType(indent)

    override final fun PrintWriter.printTarget() {
        println(COPYRIGHT_HEADER)
        println("package $packageName;")
        println()

        var specialImportId: String = ""

        imports.forEach {
            val import = it.removePrefix("import ").removePrefix("static ")

            if (specialImportId.isNotEmpty() && !import.startsWith(specialImportId)) println()

            if (import.startsWith("java.")) specialImportId = "java."
            else if (import.startsWith("javax.")) specialImportId = "javax."
            else specialImportId = ""

            println(it)
        }

        printType()
    }

    protected fun PrintWriter.printType(indent: String = "", subIndent: String = indent + INDENT) {
        println(documentation.toJavaDoc(indent, see = see, authors = authors, since = since))

        if (annotations.isNotEmpty()) print(printAnnotations(indent = indent, annotations = annotations))

        printTypeDeclaration(indent)
        print(" {")

        if (body.isNotEmpty()) {
            println()
            println()

            var prevCategory: String = ""

            body.forEach {
                if (it.category.isNotEmpty()) {
                    val mCat = CATEGORY.matchEntire(it.category) ?: throw IllegalArgumentException("Category name does not match pattern")
                    val category = mCat.groupValues[2]

                    if (category != prevCategory) {
                        println("$subIndent// ${CATEGORY_DIVIDER.substring(indent.length + 3)}")
                        println("$subIndent// # $category ${CATEGORY_DIVIDER.substring(indent.length + category.length + 6)}")
                        println("$subIndent// ${CATEGORY_DIVIDER.substring(indent.length + 3)}")
                        println()
                    }
                }

                it.run { printMember(subIndent) }

                prevCategory = category
            }
        }

        print("$indent}")
    }

    protected abstract fun PrintWriter.printTypeDeclaration(indent: String = "")

}

fun Profile.javaClass(fileName: String, packageName: String, moduleName: String, superClass: Type? = null, init: JavaClass.() -> Unit) {
    val target = JavaClass(fileName, packageName, moduleName, superClass = superClass)
    init.invoke(target)
    this@javaClass.targets.add(target)
}

class JavaClass(
    override val name: String,
    packageName: String,
    moduleName: String,
    val superClass: Type? = null,
    vararg val typeParameters: Type,
    val visibility: Int = 0,
    val isMember: Boolean = false
): JavaType(name, packageName, moduleName) {

    init {
        if (superClass != null) addInferredImport(superClass)
    }

    private val v get() = StringBuilder().run {
        val isAbstract = Modifier.isAbstract(visibility)

        if (Modifier.isPublic(visibility)) append("public ")
        if (Modifier.isProtected(visibility)) {
            if (!isMember)
                throw IllegalArgumentException("Illegal modifier \"protected\"")
            else
                print("protected ")
        }
        if (Modifier.isPrivate(visibility)) {
            if (!isMember)
                throw IllegalArgumentException("Illegal modifier \"private\"")
            else
                print("private ")
        }

        if (isAbstract) print("abstract ")
        if (Modifier.isStatic(visibility)) {
            if (!isMember)
                throw IllegalArgumentException("Illegal modifier \"static\"")
            else
                print("static ")
        }
        if (Modifier.isFinal(visibility)) {
            if (isAbstract)
                throw IllegalArgumentException("Illegal modifier \"final\"")
            else
                print("final ")
        }
        if (Modifier.isTransient(visibility)) throw IllegalArgumentException("Illegal modifier \"transient\"")
        if (Modifier.isVolatile(visibility)) throw IllegalArgumentException("Illegal modifier \"volatile\"")
        if (Modifier.isSynchronized(visibility)) throw IllegalArgumentException("Illegal modifier \"synchronized\"")
        if (Modifier.isNative(visibility)) throw IllegalArgumentException("Illegal modifier \"native\"")
        if (Modifier.isStrict(visibility)) throw IllegalArgumentException("Illegal modifier \"strictfp\"")
        if (Modifier.isInterface(visibility)) throw IllegalArgumentException("Illegal modifier \"interface\"")

        toString()
    }

    private val interfaces = mutableListOf<Type>()

    fun addInterfaces(vararg interfaces: Type) {
        interfaces.forEach { addInferredImport(it) }
        this@JavaClass.interfaces.addAll(interfaces)
    }

    fun Type.field(
        name: String,
        documentation: String,
        visibility: Int = 0,
        category: String = "",
        since: String = "",
        see: Array<out String>? = null,
        annotations: List<Annotation>? = null
    ) {
        val v = StringBuilder().run {
            val isFinal = Modifier.isFinal(visibility)

            if (Modifier.isPublic(visibility)) append("public ")
            if (Modifier.isProtected(visibility)) append("protected ")
            if (Modifier.isPrivate(visibility)) append("private ")

            if (Modifier.isAbstract(visibility)) throw IllegalArgumentException("Illegal modifier \"abstract\"")
            if (Modifier.isStatic(visibility)) append("static ")
            if (isFinal) append("final ")
            if (Modifier.isTransient(visibility)) throw IllegalArgumentException("Illegal modifier \"transient\"")
            if (Modifier.isVolatile(visibility)) {
                if (isFinal)
                    throw IllegalArgumentException("Illegal modifier \"volatile\"")
                else
                    append("volatile ")
            }
            if (Modifier.isSynchronized(visibility)) throw IllegalArgumentException("Illegal modifier \"synchronized\"")
            if (Modifier.isNative(visibility)) throw IllegalArgumentException("Illegal modifier \"native\"")
            if (Modifier.isStrict(visibility)) throw IllegalArgumentException("Illegal modifier \"strictfp\"")
            if (Modifier.isInterface(visibility)) throw IllegalArgumentException("Illegal modifier \"interface\"")

            toString()
        }

        addInferredImport(this)

        val field = JavaField(this, name, documentation, v, category, since, see, annotations)
        this@JavaClass.body.add(field)
    }

    fun javaClass(fileName: String, packageName: String, moduleName: String, superClass: Type? = null, init: JavaClass.() -> Unit) {
        val target = JavaClass(fileName, packageName, moduleName, superClass = superClass, isMember = true)
        init.invoke(target)
        this@JavaClass.body.add(target)
    }

    fun javaInterface(fileName: String, packageName: String, moduleName: String, visibility: Int = 0, init: JavaInterface.() -> Unit) {
        val target = JavaInterface(fileName, packageName, moduleName, visibility, true)
        init.invoke(target)
        this@JavaClass.body.add(target)
    }

    fun Type.method(
        name: String,
        documentation: String,
        vararg parameters: JavaParameter,
        visibility: Int = 0,
        body: String? = null,
        category: String = "",
        returnDoc: String = "",
        since: String = "",
        see: Array<out String>? = null,
        annotations: List<Annotation>? = null
    ) {
        val v = StringBuilder().run {
            val isAbstract = Modifier.isAbstract(visibility)

            if (Modifier.isPublic(visibility)) append("public ")
            if (Modifier.isProtected(visibility)) append("protected ")
            if (Modifier.isPrivate(visibility)) {
                if (isAbstract)
                    throw IllegalArgumentException("Illegal modifier \"private\"")
                else
                    append("private ")
            }

            if (isAbstract) {
                if (body != null)
                    throw IllegalArgumentException("\"abstract\" modifier combined with method body")
                else if (!Modifier.isAbstract(this@JavaClass.visibility))
                    throw IllegalArgumentException("Illegal modifier \"abstract\"")
                else
                    append("abstract ")
            }
            if (Modifier.isStatic(visibility)) {
                if (isAbstract)
                    throw IllegalArgumentException("Illegal modifier \"static\"")
                else
                    append("static ")
            }
            if (Modifier.isFinal(visibility)) {
                if (isAbstract)
                    throw IllegalArgumentException("Illegal modifier \"final\"")
                else
                    append("final ")
            }
            if (Modifier.isTransient(visibility)) throw IllegalArgumentException("Illegal modifier \"transient\"")
            if (Modifier.isVolatile(visibility)) throw IllegalArgumentException("Illegal modifier \"volatile\"")
            if (Modifier.isSynchronized(visibility)) {
                if (isAbstract)
                    throw IllegalArgumentException("Illegal modifier \"synchronized\"")
                else
                    append("synchronized ")
            }
            if (Modifier.isNative(visibility)) {
                if (isAbstract)
                    throw IllegalArgumentException("Illegal modifier \"native\"")
                else
                    append("native ")
            }
            if (Modifier.isStrict(visibility)) {
                if (isAbstract)
                    throw IllegalArgumentException("Illegal modifier \"strictfp\"")
                else
                    append("strictfp ")
            }
            if (Modifier.isInterface(visibility)) throw IllegalArgumentException("Illegal modifier \"interface\"")

            toString()
        }

        addInferredImport(this)
        parameters.forEach { addInferredImport(it.type) }

        val method = JavaMethod(this, name, documentation, parameters, v, body, category, returnDoc, since, see, annotations)
        this@JavaClass.body.add(method)
    }

    override fun getWeight() = WEIGHT_SUBTYPE

    override fun PrintWriter.printTypeDeclaration(indent: String) {
        print(v)
        print("class ")
        print(fileName)

        if (typeParameters.isNotEmpty()) {
            print("<")
            print(StringJoiner(", ").apply {
                typeParameters.forEach { add(it.toString()) }
            })
            print(">")
        }

        if (superClass != null) {
            print(" extends ")
            print(superClass.toString())
        }

        if (interfaces.isNotEmpty()) {
            print(" implements ")
            print(StringJoiner(", ").apply {
                interfaces.forEach { add(it.toString()) }
            })
        }
    }

}

fun Profile.javaInterface(fileName: String, packageName: String, moduleName: String, visibility: Int = 0, init: JavaInterface.() -> Unit) {
    val target = JavaInterface(fileName, packageName, moduleName, visibility, false)
    init.invoke(target)
    this@javaInterface.targets.add(target)
}

class JavaInterface(
    override val name: String,
    packageName: String,
    moduleName: String,
    val visibility: Int,
    val isMember: Boolean
): JavaType(name, packageName, moduleName) {

    private val v get() = StringBuilder().run {
        if (Modifier.isPublic(visibility)) append("public ")
        if (Modifier.isProtected(visibility)) {
            if (!isMember)
                throw IllegalArgumentException("Illegal modifier \"protected\"")
            else
                print("protected ")
        }
        if (Modifier.isPrivate(visibility)) {
            if (!isMember)
                throw IllegalArgumentException("Illegal modifier \"private\"")
            else
                print("private ")
        }

        if (Modifier.isAbstract(visibility)) println("WARNING: Redundant modifier \"abstract\"")
        if (Modifier.isStatic(visibility)) {
            if (!isMember)
                throw IllegalArgumentException("Illegal modifier \"static\"")
            else
                println("WARNING: Redundant modifier \"static\"")
        }
        if (Modifier.isFinal(visibility)) throw IllegalArgumentException("Illegal modifier \"final\"")
        if (Modifier.isTransient(visibility)) throw IllegalArgumentException("Illegal modifier \"transient\"")
        if (Modifier.isVolatile(visibility)) throw IllegalArgumentException("Illegal modifier \"volatile\"")
        if (Modifier.isSynchronized(visibility)) throw IllegalArgumentException("Illegal modifier \"synchronized\"")
        if (Modifier.isNative(visibility)) throw IllegalArgumentException("Illegal modifier \"native\"")
        if (Modifier.isStrict(visibility)) throw IllegalArgumentException("Illegal modifier \"strictfp\"")
        // if (Modifier.isInterface(visibility)) throw IllegalArgumentException("Illegal modifier \"interface\"")

        toString()
    }

    private val interfaces = mutableListOf<Type>()

    fun addInterfaces(vararg interfaces: Type) {
        interfaces.forEach { addInferredImport(it) }
        this@JavaInterface.interfaces.addAll(interfaces)
    }

    fun Type.field(
        name: String,
        documentation: String,
        visibility: Int = 0,
        category: String = "",
        since: String = "",
        see: Array<out String>? = null,
        annotations: List<Annotation>? = null
    ) {
        val v = StringBuilder().run {
            if (Modifier.isPublic(visibility)) println("WARNING: Redundant modifier \"public\"")
            if (Modifier.isProtected(visibility)) throw IllegalArgumentException("Illegal modifier \"protected\"")
            if (Modifier.isPrivate(visibility)) throw IllegalArgumentException("Illegal modifier \"private\"")

            if (Modifier.isAbstract(visibility)) throw IllegalArgumentException("Illegal modifier \"abstract\"")
            if (Modifier.isStatic(visibility)) println("WARNING: Redundant modifier \"static\"")
            if (Modifier.isFinal(visibility)) println("WARNING: Redundant modifier \"final\"")
            if (Modifier.isTransient(visibility)) throw IllegalArgumentException("Illegal modifier \"transient\"")
            if (Modifier.isVolatile(visibility)) throw IllegalArgumentException("Illegal modifier \"volatile\"")
            if (Modifier.isSynchronized(visibility)) throw IllegalArgumentException("Illegal modifier \"synchronized\"")
            if (Modifier.isNative(visibility)) throw IllegalArgumentException("Illegal modifier \"native\"")
            if (Modifier.isStrict(visibility)) throw IllegalArgumentException("Illegal modifier \"strictfp\"")
            if (Modifier.isInterface(visibility)) throw IllegalArgumentException("Illegal modifier \"interface\"")

            toString()
        }

        addInferredImport(this)

        val field = JavaField(this, name, documentation, v, category, since, see, annotations)
        this@JavaInterface.body.add(field)
    }

    fun javaClass(fileName: String, packageName: String, moduleName: String, superClass: Type? = null, init: JavaClass.() -> Unit) {
        val target = JavaClass(fileName, packageName, moduleName, superClass = superClass, isMember = true)
        init.invoke(target)
        this@JavaInterface.body.add(target)
    }

    fun javaInterface(fileName: String, packageName: String, moduleName: String, visibility: Int = 0, init: JavaInterface.() -> Unit) {
        val target = JavaInterface(fileName, packageName, moduleName, visibility,true)
        init.invoke(target)
        this@JavaInterface.body.add(target)
    }

    fun Type.method(
        name: String,
        documentation: String,
        vararg parameters: JavaParameter,
        visibility: Int = 0,
        body: String? = null,
        category: String = "",
        returnDoc: String = "",
        since: String = "",
        see: Array<out String>? = null,
        annotations: List<Annotation>? = null
    ) {
        val v = StringBuilder().run {
            val isStatic = Modifier.isStatic(visibility)

            if (Modifier.isPublic(visibility)) println("WARNING: Redundant modifier \"public\"")
            if (Modifier.isProtected(visibility)) throw IllegalArgumentException("Illegal modifier \"protected\"")
            if (Modifier.isPrivate(visibility)) {
                if (isStatic)
                    append("private ")
                else
                    throw IllegalArgumentException("Illegal modifier \"private\"")
            }

            if (Modifier.isAbstract(visibility)) {
                if (isStatic)
                    throw IllegalArgumentException("Illegal modifier \"abstract\"")
                else
                    println("WARNING: Redundant modifier \"abstract\"")
            }
            if (isStatic) append("static ")
            if (Modifier.isFinal(visibility)) {
                if (isStatic)
                    println("WARNING: Redundant modifier \"final\"")
                else
                    throw IllegalArgumentException("Illegal modifier \"final\"")
            }
            if (Modifier.isTransient(visibility)) throw IllegalArgumentException("Illegal modifier \"transient\"")
            if (Modifier.isVolatile(visibility)) throw IllegalArgumentException("Illegal modifier \"volatile\"")
            if (body != null) append("default ")
            if (Modifier.isSynchronized(visibility)) throw IllegalArgumentException("Illegal modifier \"synchronized\"")
            if (Modifier.isNative(visibility)) throw IllegalArgumentException("Illegal modifier \"native\"")
            if (Modifier.isStrict(visibility)) {
                if (isStatic)
                    append("strictfp ")
                else
                    throw IllegalArgumentException("Illegal modifier \"strictfp\"")
            }
            if (Modifier.isInterface(visibility)) throw IllegalArgumentException("Illegal modifier \"interface\"")

            toString()
        }

        addInferredImport(this)
        parameters.forEach { addInferredImport(it.type) }

        val method = JavaMethod(this, name, documentation, parameters, v, body, category, returnDoc, since, see, annotations)
        this@JavaInterface.body.add(method)
    }

    override fun getWeight() = WEIGHT_SUBTYPE

    override fun PrintWriter.printTypeDeclaration(indent: String) {
        print(v)
        print("interface ")
        print(fileName)

        if (interfaces.isNotEmpty()) {
            print(" extends ")
            print(StringJoiner(", ").apply {
                interfaces.forEach { add(it.toString()) }
            })
        }
    }

}

class JavaField(
    val type: Type,
    override val name: String,
    val documentation: String,
    val visibility: String,
    override var category: String,
    val since: String,
    val see: Array<out String>?,
    val annotations: List<Annotation>?
): Member {

    override fun getWeight() = WEIGHT_FIELD

    override fun PrintWriter.printMember(indent: String) {
        println(documentation.toJavaDoc(indent = indent, see = see, since = since))

        val annotations = this@JavaField.annotations
        if (annotations != null) print(printAnnotations(indent = indent, annotations = annotations))

        print(indent)
        print(visibility)
        print(type.toString())
        print(" ")
        print(name)
        println(";")
    }

}

class JavaMethod(
    val type: Type,
    override val name: String,
    val documentation: String,
    val parameters: Array<out JavaParameter>,
    val visibility: String,
    val body: String?,
    override var category: String,
    val returnDoc: String,
    val since: String,
    val see: Array<out String>?,
    val annotations: List<Annotation>?
): Member {

    override fun compareTo(other: Member): Int {
        val cmp = super.compareTo(other)

        return if (cmp != 0) cmp else 1
    }

    override fun getWeight() = WEIGHT_METHOD

    override fun PrintWriter.printMember(indent: String) {
        println(toJavaDoc(indent = indent))

        val annotations = this@JavaMethod.annotations
        if (annotations != null) println(printAnnotations(indent = indent, annotations = annotations))

        print(indent)
        print(visibility)
        print("${type.toString()} ")
        print(name)
        print("(")

        if (parameters.isNotEmpty()) {
            print(StringJoiner(", ").apply {
                parameters.forEach {
                    add(StringJoiner(" ").run {
                        if (it.annotations != null) add(printAnnotations(annotations = it.annotations, separator = " "))
                        add(it.type.toString())
                        add(it.name)

                        toString()
                    })
                }
            })
        }

        print(")")

        if (body == null)
            println(";")
        else {
            print(" {")

            if (body.isNotEmpty()) {
                var body: String = body

                while (body.startsWith(LN)) body = body.removePrefix(LN)
                while (body.endsWith(LN)) body = body.removeSuffix(LN)

                println()
                body.lineSequence().forEach {
                    print(indent)
                    println(it)
                }

                print(indent)
            }

            println("}")
        }

        println()
    }

}

fun Type.PARAM(
    name: String,
    documentation: String,
    annotations: List<Annotation>? = null
) = JavaParameter(this, name, documentation, annotations = annotations)

class JavaParameter(
    val type: Type,
    val name: String,
    val documentation: String,
    val annotations: List<Annotation>? = null
)

internal fun printAnnotations(indent: String = "", annotations: Collection<Annotation>, separator: String = LN) =
    StringJoiner(separator).run {
        annotations.forEach { add("$indent@$it${if (it.parameters.isNotEmpty()) "(${it.parameters})" else ""}") }
        toString()
    }