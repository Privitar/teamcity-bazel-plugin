/*
 * Copyright 2000-2018 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * See LICENSE in the project root for license information.
 */

package jetbrains.buildServer.bazel

import com.intellij.openapi.diagnostic.Logger
import jetbrains.buildServer.agent.BuildFinishedStatus
import jetbrains.buildServer.agent.runner.CommandExecution
import jetbrains.buildServer.agent.runner.ProgramCommandLine
import jetbrains.buildServer.agent.runner.TerminationAction
import java.io.File

class CommandExecutionAdapter(
        private val _bazelRunnerBuildService: BazelRunnerBuildService)
    : CommandExecution {
    private val _processListeners by lazy { _bazelRunnerBuildService.listeners }

    var result: BuildFinishedStatus? = null
        private set

    override fun isCommandLineLoggingEnabled() = _bazelRunnerBuildService.isCommandLineLoggingEnabled

    override fun makeProgramCommandLine(): ProgramCommandLine = _bazelRunnerBuildService.makeProgramCommandLine()

    override fun beforeProcessStarted() = _bazelRunnerBuildService.beforeProcessStarted()

    override fun processStarted(programCommandLine: String, workingDirectory: File) {
        _processListeners.forEach {
            it.processStarted(programCommandLine, workingDirectory)
        }
    }

    override fun onStandardOutput(text: String) = _processListeners.forEach { it.onStandardOutput(text) }

    override fun onErrorOutput(text: String) = _processListeners.forEach { it.onStandardOutput(text) }

    override fun interruptRequested(): TerminationAction = _bazelRunnerBuildService.interrupt()

    override fun processFinished(exitCode: Int) {
        _bazelRunnerBuildService.afterProcessFinished()

        _processListeners.forEach {
            it.processFinished(exitCode)
        }

        result = _bazelRunnerBuildService.getRunResult(exitCode)
        if (result == BuildFinishedStatus.FINISHED_SUCCESS) {
            _bazelRunnerBuildService.afterProcessSuccessfullyFinished()
        }
    }

    companion object {
        private val LOG = Logger.getInstance(CommandExecutionAdapter::class.java.name)
    }
}