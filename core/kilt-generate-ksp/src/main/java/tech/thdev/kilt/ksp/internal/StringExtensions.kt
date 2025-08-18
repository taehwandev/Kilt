package tech.thdev.kilt.ksp.internal

internal fun String.mergePrefixBind(): String =
    "bind$this"

internal fun String.mergeSuffixDaggerModule(): String =
    "${this}Module"

internal fun String.removeSuffixImpl(): String =
    this.removeSuffix("Impl")
