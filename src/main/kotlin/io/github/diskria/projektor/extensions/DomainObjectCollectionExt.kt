package io.github.diskria.projektor.extensions

import org.gradle.api.DomainObjectCollection

@Suppress("UNCHECKED_CAST")
fun <SuperT : Any, SubT : SuperT> DomainObjectCollection<out SubT>.asGenericCollection(): DomainObjectCollection<SuperT> =
    this as DomainObjectCollection<SuperT>
