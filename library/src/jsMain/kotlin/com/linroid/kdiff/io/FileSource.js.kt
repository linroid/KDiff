package com.linroid.kdiff.io

actual class FileSource actual constructor(private val path: String) {
  actual fun readLines(): List<String> {
    val fs = js("require('fs')")
    val content: String = fs.readFileSync(path, "utf-8") as String
    return content.split("\n")
  }

  actual fun close() {}
}
