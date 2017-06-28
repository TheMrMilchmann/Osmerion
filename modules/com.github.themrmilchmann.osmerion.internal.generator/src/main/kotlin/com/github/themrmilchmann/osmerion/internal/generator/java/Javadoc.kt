/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package com.github.themrmilchmann.osmerion.internal.generator.java

private val REDUNDANT_WHITESPACE = "^[ \\t]+$".toRegex(RegexOption.MULTILINE)
private val BLOCK_NODE = "(?:div|h[1-6]|code|table|thead|tfoot|tbody|td|tr|ul|li|ol|dl|dt|dd)" // TODO: add more here if necessary
private val FRAGMENT = "(</?$BLOCK_NODE(?:\\s[^>]+)?>|^)([\\s\\S]*?)(?=</?$BLOCK_NODE(?:\\s[^>]+)?>|$)".toRegex()
private val CHILD_NODE = "<(?:tr|thead|tfoot|tbody|li|dt|dd)>".toRegex()
private val PARAGRAPH_PATTERN = "\\n\\n(?:\\n?[ \\t]*[\\S][^\\n]*)+".toRegex(RegexOption.MULTILINE)
private val PREFIX_PATTERN = "^(?:\uFFFF|[ \t]++(?![*]))".toRegex(RegexOption.MULTILINE)

private fun String.cleanup(linePrefix: String = "$INDENT * "): String {
    val dom = trim().replace(REDUNDANT_WHITESPACE, "")
    return StringBuilder(dom.length)
        .layoutDOM(dom, linePrefix)
        .replace(PREFIX_PATTERN, linePrefix)
}

private fun StringBuilder.layoutDOM(dom: String, linePrefix: String): StringBuilder {
    FRAGMENT.findAll(dom).forEach { match ->
        val (tag, text) = match.destructured

        if (tag.isNotEmpty()) {
            if (startNewLine(dom, match.range.start)) {
                if (!tag.startsWith("</") && !tag.matches(CHILD_NODE)) {
                    append('\n')
                    append(linePrefix)
                }
                append('\n')
                append(linePrefix)
            }
            append(tag)
        }

        text.trim().let {
            if (it.isNotEmpty())
                layoutText(it, linePrefix, forceParagraph = tag.isNotEmpty() && tag.startsWith("</"))
        }
    }

    return this
}

private fun StringBuilder.layoutText(text: String, linePrefix: String, forceParagraph: Boolean = false) {
    var to: Int = -1

    PARAGRAPH_PATTERN.findAll(text).forEach { match ->
        val from = match.range.start
        if (to == -1 && from > 0)
            appendParagraphFirst(linePrefix, text, from, forceParagraph)

        to = match.range.endInclusive + 1
        appendParagraph(linePrefix, text, from, to)
    }

    if (to == -1)
        appendParagraphFirst(linePrefix, text, text.length, forceParagraph)
    else if (to < text.length)
        appendParagraph(linePrefix, text, to, text.length)
}

private fun StringBuilder.appendParagraphFirst(linePrefix: String, text: String, end: Int, forceParagraph: Boolean = false) {
    if (forceParagraph)
        appendParagraph(linePrefix, text, 0, end)
    else
        append(text, 0, end)
}

private fun StringBuilder.appendParagraph(linePrefix: String, text: String, start: Int, end: Int) {
    append('\n')
    append(linePrefix)
    append('\n')
    append(linePrefix)

    append("<p>")
    append(text.substring(start, end).trim())
    append("</p>")
}

private fun startNewLine(dom: String, index: Int): Boolean {
    if (index == 0)
        return false

    for (i in (index - 1) downTo 0) {
        if (dom[i] == '\n')
            return true

        if (!dom[i].isWhitespace())
            break
    }

    return false
}

private fun String.layoutJavadoc(indent: String = INDENT): String {
    return if (this.indexOf('\n') == -1)
        "$indent/** $this */"
    else
        "$indent/**\n$indent * $this\n$indent */"
}

fun String.toJavaDoc(indent: String = INDENT, see: Array<out String>? = null, authors: Array<out String>? = null, since: String = "") =
    if (see == null && authors == null && since.isEmpty()) {
        this
            .cleanup("$indent * ")
            .layoutJavadoc(indent)
    } else {
        StringBuilder(if (this.isEmpty()) "" else this.cleanup("$indent * "))
            .apply {
                if (see != null) {
                    if (isNotEmpty()) append("\n$indent *")
                    see.forEach {
                        if (isNotEmpty()) append("\n$indent * ")
                        append("@see ")
                        append(it)
                    }
                }

                if (authors != null) {
                    if (isNotEmpty()) append("\n$indent *")
                    authors.forEach {
                        if (isNotEmpty()) append("\n$indent * ")
                        append("@author ")
                        append(it)
                    }
                }

                if (!since.isEmpty()) {
                    if (isNotEmpty()) if (authors == null) append("\n$indent *\n$indent * ") else append("\n$indent * ")
                    append("@since ")
                    append(since)
                }

            }
            .toString()
            .layoutJavadoc(indent)
    }

/** Specialized formatting for methods. */
internal fun JavaMethod.toJavaDoc(indent: String = ""): String {
    if (returnDoc.isEmpty() && throws == null && see == null && since.isEmpty()) {
        if (documentation.isEmpty() && parameters.all { it.documentation.isEmpty() })
            return ""

        if (parameters.none())
            return documentation.toJavaDoc(indent = indent)
    }

    return StringBuilder(if (documentation.isEmpty()) "" else documentation.cleanup("$indent * "))
        .apply {
            if (parameters.any { p -> if (documentation == inheritDoc) p.documentation.isNotEmpty() else p.documentation != inheritDoc }) {
                // Find maximum param name length
                val alignment = parameters.map { it.name.length }.fold(0) { left, right -> java.lang.Math.max(left, right) }
                val multilineAligment = paramMultilineAligment(indent, alignment)

                if (isNotEmpty()) append("\n$indent *")
                parameters.forEach {
                    printParam(it.name, it.documentation, indent, alignment, multilineAligment)
                }
            }

            if (!returnDoc.isEmpty()) {
                if (isNotEmpty()) append("\n$indent *\n$indent * ")
                append("@return ")
                append(returnDoc.cleanup("$indent *         "))
            }

            if (throws != null) {
                if (isNotEmpty()) append("\n$indent *")
                throws.forEach {
                    if (isNotEmpty()) append("\n$indent * ")
                    append("@throws ")
                    append(it)
                }
            }

            if (see != null) {
                if (isNotEmpty()) append("\n$indent *")
                see.forEach {
                    if (isNotEmpty()) append("\n$indent * ")
                    append("@see ")
                    append(it)
                }
            }

            if (!since.isEmpty()) {
                if (isNotEmpty()) append("\n$indent *\n$indent * ")
                append("@since ")
                append(since)
            }
        }
        .toString()
        .layoutJavadoc(indent)
}

// Used for aligning parameter javadoc when it spans multiple lines.
private fun paramMultilineAligment(indent: String, alignment: Int): String {
    val whitespace = " @param ".length + alignment + 1
    return StringBuilder("$indent *".length + whitespace).apply {
        append("$indent *")
        for (i in 0..whitespace - 1)
            append(' ')
    }.toString()
}

private fun StringBuilder.printParam(name: String, documentation: String, indent: String, alignment: Int, multilineAligment: String) {
    if (isNotEmpty()) append("\n$indent * ")
    append("@param $name")

    // Align
    for (i in 0..(alignment - name.length))
        append(' ')

    append(documentation.cleanup(multilineAligment))
}