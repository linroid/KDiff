package com.linroid.kdiff.example

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.linroid.kdiff.algorithm.DiffAlgorithm
import com.linroid.kdiff.core.DiffEngine
import com.linroid.kdiff.core.Edit
import com.linroid.kdiff.io.FileSource

private object SimpleDiffAlgorithm : DiffAlgorithm {
  override val name = "simple"

  override fun diff(source: List<String>, target: List<String>): List<Edit> {
    val edits = mutableListOf<Edit>()
    var prefixLen = 0
    while (
      prefixLen < source.size &&
      prefixLen < target.size &&
      source[prefixLen] == target[prefixLen]
    ) {
      prefixLen++
    }
    var suffixLen = 0
    while (
      suffixLen < source.size - prefixLen &&
      suffixLen < target.size - prefixLen &&
      source[source.size - 1 - suffixLen] == target[target.size - 1 - suffixLen]
    ) {
      suffixLen++
    }
    if (prefixLen > 0) edits.add(Edit.Equal(0, prefixLen))
    val deletedCount = source.size - prefixLen - suffixLen
    if (deletedCount > 0) edits.add(Edit.Delete(prefixLen, deletedCount))
    val insertedLines = target.subList(prefixLen, target.size - suffixLen)
    if (insertedLines.isNotEmpty()) edits.add(Edit.Insert(prefixLen, insertedLines))
    if (suffixLen > 0) edits.add(Edit.Equal(source.size - suffixLen, suffixLen))
    return edits
  }
}

class KDiffCommand : CliktCommand(name = "kdiff") {
  override fun run() = Unit
}

class DiffCommand : CliktCommand(name = "diff") {
  override fun help(context: com.github.ajalt.clikt.core.Context) =
    "Generate a diff between two files"

  private val sourceFile by argument(help = "Source file path")
  private val targetFile by argument(help = "Target file path")

  override fun run() {
    val engine = DiffEngine(SimpleDiffAlgorithm)
    val source = FileSource(sourceFile).readLines()
    val target = FileSource(targetFile).readLines()
    val patch = engine.generatePatch(source, target)

    for (edit in patch.edits) {
      when (edit) {
        is Edit.Equal ->
          echo("  (${edit.count} equal lines at position ${edit.position})")
        is Edit.Delete ->
          echo("- Delete ${edit.count} lines at position ${edit.position}")
        is Edit.Insert -> {
          echo("+ Insert ${edit.lines.size} lines at position ${edit.position}:")
          edit.lines.forEach { echo("+   $it") }
        }
      }
    }
  }
}

class PatchCommand : CliktCommand(name = "patch") {
  override fun help(context: com.github.ajalt.clikt.core.Context) =
    "Apply a diff to recover the target file"

  private val sourceFile by argument(help = "Source file path")
  private val targetFile by argument(help = "Target file path (used to generate the patch)")

  override fun run() {
    val engine = DiffEngine(SimpleDiffAlgorithm)
    val source = FileSource(sourceFile).readLines()
    val target = FileSource(targetFile).readLines()
    val patch = engine.generatePatch(source, target)

    val recovered = engine.applyPatch(source, patch)
    recovered.forEach { echo(it) }
  }
}

fun main(args: Array<String>) {
  KDiffCommand()
    .subcommands(DiffCommand(), PatchCommand())
    .main(args)
}
