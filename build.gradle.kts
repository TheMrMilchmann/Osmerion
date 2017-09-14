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
allprojects {
    group = osmerion()
    version = "0.1.0.0-SNAPSHOT"

    evaluationDependsOnChildren()

    repositories {
        mavenCentral()
        maven { setUrl("https://oss.sonatype.org/content/repositories/snapshots/") }
    }
}


tasks {
    val createMSPFile = "createMSPFile" {
        val mspFile = File(buildDir, "modulesourcepath.args")
        outputs.file(mspFile)

        doLast {
            mspFile.delete()
            mspFile.appendText("--module-source-path\n")
            mspFile.appendText("${project.projectDir.path}/modules/*/src/{main,main-generated}/java/")
        }
    }

    "javadoc"(Javadoc::class) {
        val documentedProjects = arrayOf(
            project(":modules:base"),

            project(":modules:internal.annotation")
        )

        destinationDir = File(projectDir, "doc/web/")

        source(documentedProjects.map {
            val java = it.the<JavaPluginConvention>()
            val sourceSets = java.sourceSets

            sourceSets["main"].java
        })

        options.optionFiles(File(buildDir, "modulesourcepath.args"))

        dependsOn(createMSPFile, documentedProjects.map { "${it.path}:compileJava" })
    }
}