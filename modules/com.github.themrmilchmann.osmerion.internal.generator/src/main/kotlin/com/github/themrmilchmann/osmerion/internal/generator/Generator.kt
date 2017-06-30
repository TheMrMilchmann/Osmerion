/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package com.github.themrmilchmann.osmerion.internal.generator

import java.io.*
import java.lang.reflect.*
import java.nio.*
import java.nio.file.*
import java.nio.file.attribute.*
import java.util.concurrent.*
import java.util.concurrent.atomic.*
import java.util.function.*

fun main(args: Array<String>) {
    if (args.size != 2)
        throw IllegalArgumentException("The code Generator requires 2 paths as arguments: a) the template source path and b) the generation target path")

    val validateDirectory = { name: String, path: String ->
        if (!Files.isDirectory(Paths.get(path)))
            throw IllegalArgumentException("Invalid $name path: $path")
    }

    validateDirectory("template source", args[0])
    validateDirectory("generation target", args[1])

    generate(args[0], args[1]) {
        val pool = ForkJoinPool.commonPool()

        // Find the template methods
        val templates = java.util.TreeSet<Method> { o1, o2 -> o1.name.compareTo(o2.name) }
        apply(srcPath) {
            this.filterTo(templates) {
                methodFilter(it, Profile::class.java)
            }
        }

        val profiles = ArrayList<Profile>()
        for (template in templates) {
            val profile = template.invoke(null) as Profile? ?: continue
            profiles.add(profile)
        }

        try {
            val errors = AtomicInteger()

            CountDownLatch(profiles.size).let { latch ->
                fun generate(profile: Profile) {
                    pool.submit {
                        try {
                            profile.targets.forEach { this.generate(it) }
                        } catch (t: Throwable) {
                            errors.incrementAndGet()
                            t.printStackTrace()
                        }

                        latch.countDown()
                    }
                }

                profiles.forEach { generate(it) }
                latch.await()
            }

            if (errors.get() > 0) throw RuntimeException("Generation failed")
        } finally {
        	pool.shutdown()
        }
    }
}

private fun generate(
    srcPath: String,
    trgPath: String,
    generate: Generator.() -> Unit
) {
    Generator(srcPath, trgPath).generate()
}

class Generator(
    val srcPath: String,
    val trgPath: String
) {

    fun methodFilter(method: Method, javaClass: Class<*>) =
        // static
        method.modifiers and Modifier.STATIC != 0 &&
            // returns NativeClass
            method.returnType === javaClass &&
            // has no arguments
            method.parameterTypes.isEmpty()

    fun apply(packagePath: String, consume: Sequence<Method>.() -> Unit) {
        val packageDirectory = Paths.get(packagePath)
        if (!Files.isDirectory(packageDirectory))
            throw IllegalStateException()
        Files.walk(packageDirectory)
            .filter { KOTLIN_PATH_MATCHER.matches(it) }
            .sorted()
            .also {
                it.forEach {
                    try {
                        Class
                            .forName("${packageDirectory.relativize(it.parent).toString().replace('\\', '.')}.${it.fileName.toString().substringBeforeLast('.').upperCaseFirst}Kt")
                            .methods
                            .asSequence()
                            .consume()
                    } catch (e: ClassNotFoundException) {
                        e.printStackTrace()
                    }
                }
                it.close()
            }
    }

    internal fun generate(target: GeneratorTarget) {
        val output = Paths.get("$trgPath/${target.moduleName}/src/generated/${target.language}/${target.packageName.replace('.', '/')}/${target.fileName}.${target.appendix}")

        generateOutput(target, output) {
            it.printTarget()
        }
    }

}

internal val Path.lastModified get() = if (Files.isRegularFile(this))
    Files.getLastModifiedTime(this).toMillis()
else
    0L

private val KOTLIN_PATH_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**/*.kt")

internal fun Path.lastModified(
    maxDepth: Int = Int.MAX_VALUE,
    glob: String? = null,
    matcher: PathMatcher = if (glob == null) KOTLIN_PATH_MATCHER else FileSystems.getDefault().getPathMatcher("glob:$glob")
): Long {
    if (!Files.isDirectory(this))
        throw IllegalStateException()
    return Files
        .find(this, maxDepth, BiPredicate { path, _ -> matcher.matches(path) })
        .mapToLong(Path::lastModified)
        .reduce(0L, Math::max)
}

private fun ensurePath(path: Path) {
    val parent = path.parent ?: throw IllegalArgumentException("The given path has no parent directory.")
    if (!Files.isDirectory(parent)) {
        println("\tMKDIR: $parent")
        Files.createDirectories(parent)
    }
}

private fun readFile(file: Path) = Files.newByteChannel(file).use {
    val bytesTotal = it.size().toInt()
    val buffer = ByteBuffer.allocateDirect(bytesTotal)
    var bytesRead = 0
    do {
        bytesRead += it.read(buffer)
    } while (bytesRead < bytesTotal)
    buffer.flip()
    buffer
}

// Always use \n as line separator
private class OsmerionWriter(out: Writer): PrintWriter(out) {
    override fun println() = print(LN)
}

private fun <T> generateOutput(target: T, file: Path, lmt: Long? = null, generate: T.(PrintWriter) -> Unit) {
    ensurePath(file)

    if (Files.isRegularFile(file)) {
        // Generate in memory
        val baos = ByteArrayOutputStream(4 * 1024)
        OsmerionWriter(OutputStreamWriter(baos, Charsets.UTF_8)).use {
            target.generate(it)
        }

        // Compare the existing file content with the generated content.
        val before = readFile(file)
        val after = baos.toByteArray()

        fun somethingChanged(b: ByteBuffer, a: ByteArray): Boolean {
            if (b.remaining() != a.size)
                return true
            return (0..b.limit() - 1).any { b[it] != a[it] }
        }

        if (somethingChanged(before, after)) {
            println("\tUPDATING: $file")
            // Overwrite
            Files.newOutputStream(file).use {
                it.write(after)
            }
        } else if (lmt != null) {
            // Update the file timestamp
            Files.setLastModifiedTime(file, FileTime.fromMillis(lmt + 1))
        }
    } else {
        println("\tWRITING: $file")
        OsmerionWriter(Files.newBufferedWriter(file, Charsets.UTF_8)).use {
            target.generate(it)
        }
    }
}

/** Returns the string with the first letter uppercase. */
internal val String.upperCaseFirst
    get() = if (this.length <= 1)
        this.toUpperCase()
    else
        "${Character.toUpperCase(this[0])}${this.substring(1)}"