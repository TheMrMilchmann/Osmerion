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
import com.github.themrmilchmann.kraton.gradle.tasks.*

buildscript {
    repositories {
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
    }

    dependencies {
        classpath("com.github.themrmilchmann.kraton:kraton-gradle:0.2.0-SNAPSHOT")
    }
}

plugins {
    kotlin("jvm")
}

configureKotlinProject()

tasks {
    "generate"(Generate::class) {
        dependsOn("compileKotlin")

        templatesRoot = File(project(":modules:internal.generator.templates").projectDir, "src/main/kotlin/")
        outputRoot = File(rootProject.projectDir, "modules")

        classpath = java.sourceSets["main"].runtimeClasspath
    }

    "clean-generated" {
        doLast {
            val modulesRoot = File(rootProject.projectDir, "modules")
            modulesRoot.listFiles().forEach {
                val generatedDir = File(it, "src/generated/")
                if (generatedDir.isDirectory) generatedDir.listFiles().forEach { delete(it) }
            }
        }
    }
}