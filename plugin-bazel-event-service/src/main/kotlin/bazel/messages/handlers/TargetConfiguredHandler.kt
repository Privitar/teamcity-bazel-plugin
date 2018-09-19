package bazel.messages.handlers

import bazel.HandlerPriority
import bazel.Verbosity
import bazel.atLeast
import bazel.bazel.events.BazelEvent
import bazel.bazel.events.TargetConfigured
import bazel.messages.Color
import bazel.messages.ServiceMessageContext
import bazel.messages.apply

class TargetConfiguredHandler : EventHandler {
    override val priority: HandlerPriority
        get() = HandlerPriority.Medium

    override fun handle(ctx: ServiceMessageContext) =
            if (ctx.event.payload is BazelEvent && ctx.event.payload.content is TargetConfigured) {
                val event = ctx.event.payload.content
                val description = "Target ${event.targetKind} \"${event.label}\" configured"
                if (ctx.verbosity.atLeast(Verbosity.Normal)) {
                    val blockName = "Target ${event.label}"
                    if (ctx.blockManager.createBlock(blockName, event.children)) {
                        ctx.onNext(ctx.messageFactory.createBlockOpened(blockName, ""))
                    }

                    ctx.onNext(ctx.messageFactory.createBuildStatus(description))

                    if (ctx.verbosity.atLeast(Verbosity.Detailed)) {
                        ctx.onNext(ctx.messageFactory.createMessage(
                                ctx.buildMessage()
                                        .append(description.apply(Color.BuildStage))
                                        .append(", aspect \"${event.aspect}\", test size \"${event.testSize}\"", Verbosity.Verbose)
                                        .append(", tags: \"${event.tags.joinToStringEscaped(", ")}\"", Verbosity.Verbose)
                                        .toString()))
                    }
                } else {
                    ctx.onNext(ctx.messageFactory.createBuildStatus(description))
                }

                true
            } else ctx.handlerIterator.next().handle(ctx)
}