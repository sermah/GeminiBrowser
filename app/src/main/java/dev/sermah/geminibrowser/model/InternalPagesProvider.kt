package dev.sermah.geminibrowser.model

interface InternalPagesProvider {
    /**
     * Returns HTML of an error page with title, description and some data.
     */
    fun getErrorPage(code: Int, data: Map<String, String>): String

    /**
     * Returns HTML of an error page with title, description and some data.
     */
    fun getErrorPage(throwable: Throwable, data: Map<String, String>): String
}