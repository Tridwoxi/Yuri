"""Chart generator for Yuri.java for more interesting patterns"""

from random import choice, randint, random
from typing import Generator


class MarkovChain:
    """Generic representation of a markov chain.

    Features stochastic transition matrix, state, and a generator."""

    def __init__(self, transition_matrix: list[list[float]], state: int = 0) -> None:
        self.state = state
        self.transition_matrix = transition_matrix

    def next(self) -> int:
        """Assigns and returns the next state by choosing from transition_matrix"""
        pick = random()
        cumulative = 0
        probabilities = self.transition_matrix[self.state]
        for index, probability in enumerate(probabilities):
            cumulative += probability
            if cumulative >= pick:
                self.state = index
                return self.state
        raise ValueError("Your row is broken")

    def generator(self, length: int = -1) -> Generator[int]:
        """Builds and returns the generator of the next state"""
        current = 0
        while current != length:
            current += 1
            yield self.next()

    def __str__(self) -> str:
        return f"<MarkovChain state: {self.state} transition_matrix: {self.transition_matrix}>"


class ChartBuilder:
    """Represents a Chart in the `.yrct` format.

    Should be driven by external methods with the predefined patterns (e.g.
    staircase). These predefined patterns accept a volume or depth parameter
    (that must be >= 1 and <= size of the chart) for the number of notes at
    once or how many times to repeat, and call _add_pattern. For a bit of
    spice, reversed patterns are used when volume is even. Developed on charts
    of size 4; untested otherwise.
    """

    def __init__(self, size: int, labels: str) -> None:
        self.time = 0
        self.size = size
        self.labels = labels
        self.chart = [[] for _ in range(size)]
        assert self.size == len(self.labels)

    def _add_pattern(self, pattern: list[int]) -> None:
        """Adds a given pattern of notes to the chart"""
        for note in pattern:
            self.chart[note].append(self.time)
        self.time += 1

    def staircase(self, volume: int) -> None:
        """Adds a staircase pattern"""
        if volume & 1:
            for i in range(self.size):
                self._add_pattern([(i + j) % self.size for j in range(volume)])
        else:
            for i in reversed(range(self.size)):
                self._add_pattern([(i + j) % self.size for j in range(volume)])

    def alternate(self, volume: int) -> None:
        """Adds an alternater pattern"""
        if volume & 1:
            self._add_pattern(list(range(volume)))
            self._add_pattern([self.size - i - 1 for i in range(volume)])
        else:
            self._add_pattern([self.size - i - 1 for i in range(volume)])
            self._add_pattern(list(range(volume)))

    def out_in_out(self, depth: int) -> None:
        """Adds an outside-inside-outside pattern"""
        if self.size >= 3:
            self._add_pattern([0, self.size - 1])
            for _ in range(depth):
                self._add_pattern(list(range(1, self.size - 1)))
                self._add_pattern([0, self.size - 1])

    def boom(self, depth: int) -> None:
        """Adds a nothing-everything-nothing pattern."""
        self._add_pattern([])
        for _ in range(depth):
            self._add_pattern(list(range(self.size)))
        self._add_pattern([])

    def streak(self, depth: int) -> None:
        """Adds a pattern that's not like the others idk it's like stepping"""
        # 1, 0, 3, 2
        patterns = [((i >> 1) << 1) + (~i & 1) for i in range(self.size)]
        if self.size & 1:
            patterns[len(patterns) - 1] -= 1
        for _ in range(depth):
            for pattern in patterns:
                self._add_pattern([pattern])

    def __str__(self) -> str:
        return "\n".join(
            f"{label}\n{' '.join(map(str, row))}"
            for label, row in zip(self.labels, self.chart)
        )


if __name__ == "__main__":
    # Assume cadente has 300 or so beats in it, idrk.
    # The script isnt even deterministisc
    # so it's not liek you can guess perfectly
    CHART_LENGTH = 60

    cb = ChartBuilder(4, "DFJK")
    funcs = [
        cb.staircase,
        cb.alternate,
        cb.out_in_out,
        cb.boom,
        cb.streak,
    ]

    tm = [
        [0.3, 0.1, 0.6, 0.0, 0.0],
        [0.1, 0.0, 0.1, 0.2, 0.6],
        [0.1, 0.0, 0.4, 0.5, 0.0],
        [0.4, 0.0, 0.0, 0.6, 0.0],
        [0.1, 0.7, 0.1, 0.1, 0.0],
    ]
    mc = MarkovChain(tm)

    cb.staircase(1) # Let's start off with something easy
    cb.staircase(1)
    for scary_factor in range(1, 3):
        for s in mc.generator(CHART_LENGTH):
            difficulty = choice([scary_factor, randint(1, 3)])
            funcs[s](difficulty)

    print(cb)
