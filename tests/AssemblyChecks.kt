package ch.spinnennetz.steckbruecke

fun main() {
    var current = Assembly()
    var combinations = 0
    for (expected in Part.entries) {
        check(current.next == expected)
        for (socket in Part.entries) {
            val empty = current.place(socket)
            check(empty.placed == current.placed && empty.feedback == Feedback.SELECT_FIRST)
            combinations++
            for (part in Part.entries.filterNot(current::isPlaced)) {
                val next = current.select(part).place(socket)
                if (part == socket && socket == expected) {
                    check(next.placed == current.placed + 1)
                    check(next.selected == null)
                } else {
                    check(next.placed == current.placed)
                    check(next.next == expected)
                    check(next.selected == null && next.feedback == Feedback.WRONG_PLACEMENT)
                }
                combinations++
            }
        }
        current = current.select(expected).place(expected)
        check(current.isPlaced(expected))
        check(current.select(expected) == current)
    }
    check(current.complete && current.placed == 3 && current.feedback == Feedback.COMPLETE)
    for (part in Part.entries) {
        check(current.place(part) == current)
    }
    val reset = Assembly()
    check(!reset.complete && reset.placed == 0 && reset.next == Part.LEFT)
    check(reset.selected == null && reset.feedback == Feedback.READY)
    println("PASS: $combinations tested input combinations, complete-state no-ops and reset")
}
