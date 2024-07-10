package dev.sermah.geminibrowser.xml

class XMLBuilder(
    private val tag: String
) {
    private val _class = mutableSetOf<String>()
    private val _attributes = mutableMapOf<String, String>()
    private val _children = mutableListOf<Any>()

    fun putClass(klass: String) {
        _class.add(klass)
    }

    fun removeClass(klass: String) {
        _class.remove(klass)
    }

    fun putAttr(attr: String, value: String) {
        _attributes[attr] = value
    }

    fun removeAttr(attr: String, value: String) {
        _attributes.remove(attr)
    }

    fun appendChild(child: Any) {
        _children.add(child)
    }

    fun prependChild(child: Any) {
        _children.add(0, child)
    }

    fun putChild(idx: Int, child: Any) {
        _children.add(idx, child)
    }

    fun removeChild(idx: Int) {
        _children.removeAt(idx)
    }

    fun last(): Any? {
        return _children.lastOrNull()
    }

    fun build() = XMLElement(
        tag = tag,
        klass = _class,
        attributes = _attributes,
        children = _children
    )
}

class XMLElement(
    var tag: String,
    val klass: MutableSet<String> = mutableSetOf(),
    val attributes: MutableMap<String, String> = mutableMapOf(),
    val children: MutableList<Any> = mutableListOf(),
) {
    override fun toString(): String = buildString {
        asStringInternal(this)
    }

    private fun asStringInternal(builder: StringBuilder) {
        builder.apply {
            append("<")
            append(tag)
            if (klass.isNotEmpty()) {
                append(" class=\"")
                klass.forEach {
                    append(" ")
                    append(it)
                }
                append("\"")
            }
            attributes.forEach { (key, value) ->
                append(" ")
                append(key)
                append("=\"")
                append(value)
                append("\"")
            }
            append(">")
            children.forEach {
                if (it is XMLElement) {
                    it.asStringInternal(this)
                } else {
                    append(it.toString())
                }
            }
            append("</")
            append(tag)
            append(">")
        }
    }
}

fun buildXml(tag: String, block: XMLBuilder.() -> Unit): XMLElement =
    XMLBuilder(tag).apply {
        block()
    }.build()

fun XMLBuilder.tag(tag: String, block: XMLBuilder.() -> Unit = {}) {
    this.appendChild(buildXml(tag, block))
}

fun XMLBuilder.text(block: () -> String) {
    this.appendChild(block())
}

fun XMLBuilder.attributes(vararg attrs: Pair<String, String>) {
    attrs.forEach { this.putAttr(it.first, it.second) }
}

fun XMLBuilder.classes(vararg classes: String) {
    classes.forEach { this.putClass(it) }
}
