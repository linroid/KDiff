package com.linroid.kdiff.io

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.usePinned
import platform.posix.SEEK_END
import platform.posix.SEEK_SET
import platform.posix.fclose
import platform.posix.fgets
import platform.posix.fopen
import platform.posix.fread
import platform.posix.fseek
import platform.posix.ftell

@OptIn(ExperimentalForeignApi::class)
actual class FileSource actual constructor(private val path: String) {
  actual fun readLines(): List<String> {
    val lines = mutableListOf<String>()
    val file = fopen(path, "r") ?: error("Cannot open file: $path")
    try {
      val buffer = ByteArray(4096)
      buffer.usePinned { pinned ->
        while (fgets(pinned.addressOf(0), buffer.size, file) != null) {
          val line = pinned.addressOf(0).toKString().trimEnd('\n', '\r')
          lines.add(line)
        }
      }
    } finally {
      fclose(file)
    }
    return lines
  }

  actual fun readBytes(): ByteArray {
    val file = fopen(path, "rb") ?: error("Cannot open file: $path")
    try {
      fseek(file, 0, SEEK_END)
      val size = ftell(file).toInt()
      fseek(file, 0, SEEK_SET)
      val buffer = ByteArray(size)
      buffer.usePinned { pinned ->
        fread(pinned.addressOf(0), 1u, size.toULong(), file)
      }
      return buffer
    } finally {
      fclose(file)
    }
  }

  actual fun close() {}
}
