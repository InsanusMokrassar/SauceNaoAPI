package dev.inmo.saucenaoapi

sealed class OutputType {
    abstract val typeCode: Int
}

object HtmlOutputType : dev.inmo.saucenaoapi.OutputType() {
    override val typeCode: Int = 0
}

object XmlOutputType : dev.inmo.saucenaoapi.OutputType() {
    override val typeCode: Int = 1
}

object JsonOutputType : dev.inmo.saucenaoapi.OutputType() {
    override val typeCode: Int = 2
}
