package com.linroid.kdiff.io

expect class FileSource(path: String) {
  fun readLines(): List<String>
  fun close()
}
