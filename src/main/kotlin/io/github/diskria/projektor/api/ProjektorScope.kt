package io.github.diskria.projektor.api

@ProjektorDsl
internal interface ProjektorScope

@DslMarker
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.BINARY)
internal annotation class ProjektorDsl
