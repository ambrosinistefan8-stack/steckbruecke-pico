package ch.spinnennetz.steckbruecke

internal enum class Part(val label: String, val meshName: String) {
    LEFT("Left support", "02 Left support"),
    RIGHT("Right support", "03 Right support"),
    BEAM("Crossbeam", "04 Beam"),
}

internal enum class Feedback {
    READY, SELECTED, SELECT_FIRST, WRONG_PLACEMENT, PLACED, COMPLETE,
}

internal data class Assembly private constructor(
    val placed: Int,
    val selected: Part?,
    val feedback: Feedback,
) {
    constructor() : this(0, null, Feedback.READY)

    val next: Part? get() = Part.entries.getOrNull(placed)
    val complete: Boolean get() = next == null

    fun isPlaced(part: Part): Boolean = part.ordinal < placed

    fun select(part: Part): Assembly =
        if (isPlaced(part)) this else copy(selected = part, feedback = Feedback.SELECTED)

    fun place(socket: Part): Assembly {
        val expected = next ?: return this
        val active = selected ?: return copy(feedback = Feedback.SELECT_FIRST)
        if (socket != expected || active != expected) {
            return copy(selected = null, feedback = Feedback.WRONG_PLACEMENT)
        }
        val count = placed + 1
        return Assembly(
            count,
            null,
            if (count == Part.entries.size) Feedback.COMPLETE else Feedback.PLACED,
        )
    }

    fun message(): String = when (feedback) {
        Feedback.READY -> "Pick the left support, then tap its mint ring."
        Feedback.SELECTED -> "${selected?.label} selected. Tap a socket."
        Feedback.SELECT_FIRST -> "Pick a loose part before choosing its socket."
        Feedback.WRONG_PLACEMENT -> "That fit is out of sequence. Next: ${next?.label}."
        Feedback.PLACED -> "Part fitted. Next: ${next?.label}."
        Feedback.COMPLETE -> "Bridge complete. Reset to practise again."
    }
}
