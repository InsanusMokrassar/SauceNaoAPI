package dev.inmo.SauceNaoAPI

sealed class OutputType {
    abstract val typeCode: Int
}

object HtmlOutputType : dev.inmo.SauceNaoAPI.OutputType() {
    override val typeCode: Int = 0
}

object XmlOutputType : dev.inmo.SauceNaoAPI.OutputType() {
    override val typeCode: Int = 1
}

object JsonOutputType : dev.inmo.SauceNaoAPI.OutputType() {
    override val typeCode: Int = 2
}
