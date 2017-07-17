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
import org.gradle.api.*
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.*
import java.io.File

fun Project.configureJavaProject(generated: Boolean = true, tests: Boolean = true) {
    afterEvaluate {
        val java = the<JavaPluginConvention>()
        java.sourceCompatibility = JavaVersion.VERSION_1_9
        java.targetCompatibility = JavaVersion.VERSION_1_9

        val sourceSets = java.sourceSets
        sourceSets["main"].java.outputDir = File(rootProject.projectDir, "config/modules/${project.name}")

        if (generated) {
            sourceSets["main"].java.srcDir("/src/main-generated/java")
            sourceSets["test"].java.srcDir("/src/test-generated/java")
        }
    }

    tasks {
        "compileJava"(JavaCompile::class) {
            options.forkOptions.javaHome = File(rootProject.rootDir, "config/jdk9/") // TODO temporary JDK9 workaround (until Gradle runs properly on jdk9)
            options.isFork = true
            options.isDebug = true

            options.compilerArgs.add("--module-path")
            options.compilerArgs.add(File(rootProject.projectDir, "config/modules/").absolutePath)
        }
    }

    if (tests) {
        val test: Test by tasks
        test.useTestNG()

        dependencies {
            "testCompile"("org.testng:testng:6.11")
        }
    }
}

fun Project.configureKotlinProject(stdlib: Boolean = true) {
    dependencies {
        if (stdlib) "compile"(kotlin("stdlib-jre8"))
    }
}

fun osmerion(path: String = "") = "com.github.themrmilchmann.osmerion${if (path.isNotEmpty()) ".$path" else "" }"