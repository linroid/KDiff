package com.linroid.kdiff.binary

data class ControlBlock(
  val diffLength: Int,
  val extraLength: Int,
  val oldSeek: Int,
)
