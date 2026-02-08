package com.linroid.kdiff.example

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.arguments.argument
import com.linroid.kdiff.algorithm.MyersDiffAlgorithm
import com.linroid.kdiff.core.DiffEngine
import com.linroid.kdiff.core.Edit
import com.linroid.kdiff.io.FileSource

class KDiffCommand : CliktCommand(name = "kdiff") {
  override fun run() = Unit
}

class DiffCommand : CliktCommand(name = "diff") {
  override fun help(context: com.github.ajalt.clikt.core.Context) =
    "Generate a diff between two files"

  private val sourceFile by argument(help = "Source file path")
  private val targetFile by argument(help = "Target file path")

  override fun run() {
    val engine = DiffEngine(MyersDiffAlgorithm())
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
    val engine = DiffEngine(MyersDiffAlgorithm())
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
