package com.linroid.kdiff.io

import java.io.File

actual class FileSource actual constructor(private val path: String) {
  private val file = File(path)

  actual fun readLines(): List<String> = file.readLines()

  actual fun readBytes(): ByteArray = file.readBytes()

  actual fun close() {}
}
