/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package com.github.themrmilchmann.osmerion.internal.generator

import java.io.*

class Profile(
    init: Profile.() -> Unit
) {

    val targets = ArrayList<GeneratorTarget>()

    init {
        init.invoke(this)
    }

    fun GeneratorTarget.registerTarget() {
        targets.add(this)
    }

}

abstract class GeneratorTarget(
    val fileName: String,
    val language: String,
    val packageName: String,
    val moduleName: String,
    val kind: String = "main",
    val appendix: String = language
) {

    abstract fun PrintWriter.printTarget()

}