package com.example.counting

import kotlin.random.Random

enum class Mode(val title: String, val emoji: String) {
    COUNT_OBJECTS("شمارش اشیا", "🍎"),
    SKIP_COUNT("شمارش پرشی", "🐸"),
    BEFORE_AFTER("قبل و بعد", "↔️"),
    TENS_ONES("ده‌تایی و یکی", "📦")
}

sealed interface Visual {
    data class Objects(val emoji: String, val count: Int) : Visual
    data class Sequence(val items: List<String>) : Visual
    data class TensOnes(val tens: Int, val ones: Int) : Visual
    data class Number(val value: Int) : Visual
}

data class Question(
    val prompt: String,
    val visual: Visual,
    val options: List<Int>,
    val answer: Int
)

object QuestionFactory {
    private val emojis = listOf("🍎", "⭐", "🐟", "🎈", "🌸", "🚗", "🐥", "🍪")

    fun create(mode: Mode): Question = when (mode) {
        Mode.COUNT_OBJECTS -> countObjects()
        Mode.SKIP_COUNT -> skipCount()
        Mode.BEFORE_AFTER -> beforeAfter()
        Mode.TENS_ONES -> tensOnes()
    }

    private fun countObjects(): Question {
        val n = Random.nextInt(1, 21)
        return Question(
            prompt = "چند تا می‌بینی؟",
            visual = Visual.Objects(emojis.random(), n),
            options = options(n, 1, 20),
            answer = n
        )
    }

    private fun skipCount(): Question {
        val step = listOf(2, 5, 10).random()
        val start = Random.nextInt(0, 21)
        val values = List(5) { start + step * it }
        val missing = Random.nextInt(0, 5)
        val items = values.mapIndexed { i, v -> if (i == missing) "؟" else v.fa() }
        return Question(
            prompt = "عددها ${step.fa()} تا ${step.fa()} تا زیاد می‌شوند. عدد جا افتاده کدام است؟",
            visual = Visual.Sequence(items),
            options = options(values[missing], 0, 100, step),
            answer = values[missing]
        )
    }

    private fun beforeAfter(): Question {
        val n = Random.nextInt(2, 99)
        val after = Random.nextBoolean()
        val answer = if (after) n + 1 else n - 1
        val prompt = if (after) {
            "چه عددی بعد از ${n.fa()} می‌آید؟"
        } else {
            "چه عددی قبل از ${n.fa()} می‌آید؟"
        }
        return Question(prompt, Visual.Number(n), options(answer, 0, 100), answer)
    }

    private fun tensOnes(): Question {
        val t = Random.nextInt(1, 10)
        val o = Random.nextInt(0, 10)
        val answer = t * 10 + o
        val distractors = listOf(o * 10 + t, answer + 10, answer - 10, answer + 1, answer - 1, answer + 2, answer - 2)
            .filter { it in 1..99 && it != answer }
            .distinct()
            .shuffled()
            .take(3)
        return Question(
            prompt = "این شکل چه عددی را نشان می‌دهد؟",
            visual = Visual.TensOnes(t, o),
            options = (listOf(answer) + distractors).shuffled(),
            answer = answer
        )
    }

    /** چهار گزینه می‌سازد که یکی از آن‌ها جواب درست است. */
    private fun options(answer: Int, min: Int, max: Int, step: Int = 1): List<Int> {
        val set = linkedSetOf(answer)
        var tries = 0
        while (set.size < 4 && tries < 200) {
            tries++
            val sign = if (Random.nextBoolean()) 1 else -1
            val candidate = answer + Random.nextInt(1, 4) * step * sign
            if (candidate in min..max) set.add(candidate)
        }
        var filler = min
        while (set.size < 4) {
            set.add(filler)
            filler += step
        }
        return set.toList().shuffled()
    }
}
