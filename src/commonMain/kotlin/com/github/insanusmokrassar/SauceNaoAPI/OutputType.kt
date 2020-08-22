package com.github.insanusmokrassar.SauceNaoAPI

sealed class OutputType {
    abstract val typeCode: Int
}

object HtmlOutputType : OutputType() {
    override val typeCode: Int = 0
}

object XmlOutputType : OutputType() {
    override val typeCode: Int = 1
}

object JsonOutputType : OutputType() {
    override val typeCode: Int = 2
}
