package com.linroid.kdiff.algorithm

import com.linroid.kdiff.binary.BinaryPatch

interface BinaryDiffAlgorithm {
  val name: String
  fun diff(source: ByteArray, target: ByteArray): BinaryPatch
}
