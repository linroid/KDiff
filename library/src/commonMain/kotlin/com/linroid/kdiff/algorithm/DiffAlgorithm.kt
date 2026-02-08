package com.linroid.kdiff.algorithm

import com.linroid.kdiff.core.Edit

interface DiffAlgorithm {
  val name: String
  fun diff(source: List<String>, target: List<String>): List<Edit>
}
