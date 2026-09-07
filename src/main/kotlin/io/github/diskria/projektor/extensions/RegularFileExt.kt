package io.github.diskria.projektor.extensions

import org.gradle.api.file.RegularFile

fun RegularFile.writeTextCreatingParent(text: String) {
    asFile.apply {
        parentFile?.mkdirs()
        writeText(text)
    }
}
