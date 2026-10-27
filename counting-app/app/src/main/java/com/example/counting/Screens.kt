package com.example.counting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val TOTAL_ROUNDS = 10

/** عدد انگلیسی را به رقم فارسی تبدیل می‌کند. */
fun Int.fa(): String =
    toString().map { if (it in '0'..'9') '۰' + (it - '0') else it }.joinToString("")

// ───────────────────────── منوی اصلی ─────────────────────────

@Composable
fun MenuScreen(onSelect: (Mode) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "ریاضی دوم دبستان",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Palette.ink
        )
        Text("بیا با هم بشماریم! 🌟", fontSize = 20.sp, color = Palette.ink)
        Spacer(Modifier.height(8.dp))
        Mode.entries.forEachIndexed { index, mode ->
            ModeCard(mode, Palette.cards[index % Palette.cards.size]) { onSelect(mode) }
        }
    }
}

@Composable
private fun ModeCard(mode: Mode, color: Color, onClick: () -> Unit) {
    val shape = RoundedCornerShape(24.dp)
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = color),
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp)
            .clip(shape)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(mode.emoji, fontSize = 40.sp)
            Spacer(Modifier.size(16.dp))
            Text(mode.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Palette.ink)
        }
    }
}

// ───────────────────────── صفحه‌ی سؤال‌ها ─────────────────────────

@Composable
fun QuizScreen(mode: Mode, onBack: () -> Unit) {
    var round by remember { mutableIntStateOf(1) }
    var score by remember { mutableIntStateOf(0) }
    var question by remember { mutableStateOf(QuestionFactory.create(mode)) }
    var selected by remember { mutableStateOf<Int?>(null) }

    fun restart() {
        round = 1
        score = 0
        selected = null
        question = QuestionFactory.create(mode)
    }

    if (round > TOTAL_ROUNDS) {
        ResultScreen(score = score, onRestart = ::restart, onBack = onBack)
        return
    }

    val picked = selected
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack) { Text("🏠 منو", fontSize = 18.sp) }
            Text(
                "سؤال ${round.fa()} از ${TOTAL_ROUNDS.fa()}",
                fontSize = 18.sp,
                color = Palette.ink
            )
            Text("⭐ ${score.fa()}", fontSize = 20.sp, color = Palette.ink)
        }

        LinearProgressIndicator(
            progress = { (round - 1) / TOTAL_ROUNDS.toFloat() },
            modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
            color = Palette.good
        )

        Text(
            question.prompt,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Palette.ink
        )

        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier.padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                VisualView(question.visual)
            }
        }

        question.options.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { option ->
                    OptionButton(
                        value = option,
                        container = when {
                            picked == null -> Palette.primary
                            option == question.answer -> Palette.good
                            option == picked -> Palette.bad
                            else -> Palette.neutral
                        },
                        enabled = picked == null,
                        modifier = Modifier.weight(1f)
                    ) {
                        selected = option
                        if (option == question.answer) score += 1
                    }
                }
            }
        }

        if (picked != null) {
            val correct = picked == question.answer
            Text(
                if (correct) "آفرین! درست گفتی 🎉"
                else "جواب درست ${question.answer.fa()} بود. نگران نباش 💪",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                color = if (correct) Palette.good else Palette.bad
            )
            Button(
                onClick = {
                    round += 1
                    if (round <= TOTAL_ROUNDS) {
                        question = QuestionFactory.create(mode)
                        selected = null
                    }
                },
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth().height(60.dp)
            ) {
                Text(
                    if (round == TOTAL_ROUNDS) "دیدن نتیجه" else "سؤال بعدی",
                    fontSize = 22.sp
                )
            }
        }
    }
}

@Composable
private fun OptionButton(
    value: Int,
    container: Color,
    enabled: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            disabledContainerColor = container,
            disabledContentColor = Color.White
        ),
        modifier = modifier.height(72.dp)
    ) {
        Text(value.fa(), fontSize = 32.sp, fontWeight = FontWeight.Bold)
    }
}

// ───────────────────────── نتیجه ─────────────────────────

@Composable
private fun ResultScreen(score: Int, onRestart: () -> Unit, onBack: () -> Unit) {
    val stars = when {
        score >= 9 -> 3
        score >= 6 -> 2
        else -> 1
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
    ) {
        Text("🏆", fontSize = 80.sp)
        Text("⭐".repeat(stars), fontSize = 44.sp)
        Text(
            "تو ${score.fa()} سؤال از ${TOTAL_ROUNDS.fa()} سؤال را درست جواب دادی!",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = Palette.ink
        )
        Button(
            onClick = onRestart,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().height(60.dp)
        ) { Text("دوباره بازی کن", fontSize = 22.sp) }
        TextButton(onClick = onBack) { Text("بازگشت به منو", fontSize = 20.sp) }
    }
}

// ───────────────────────── تصویرهای سؤال ─────────────────────────

@Composable
private fun VisualView(visual: Visual) {
    when (visual) {
        is Visual.Objects -> ObjectsView(visual)
        is Visual.Sequence -> SequenceView(visual)
        is Visual.TensOnes -> TensOnesView(visual)
        is Visual.Number -> Text(
            visual.value.fa(),
            fontSize = 72.sp,
            fontWeight = FontWeight.Bold,
            color = Palette.primary
        )
    }
}

/** اشیا را در ردیف‌های پنج‌تایی نشان می‌دهد تا شمردن راحت‌تر شود. */
@Composable
private fun ObjectsView(v: Visual.Objects) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        List(v.count) { v.emoji }.chunked(5).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { Text(it, fontSize = 38.sp) }
            }
        }
    }
}

/** خط عددی از چپ به راست (مثل کتاب ریاضی). */
@Composable
private fun SequenceView(v: Visual.Sequence) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            v.items.forEach { item ->
                val missing = item == "؟"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(0.8f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (missing) Color(0xFFFFE082) else Color(0xFFBBDEFB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        item,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Palette.ink
                    )
                }
            }
        }
    }
}

@Composable
private fun TensOnesView(v: Visual.TensOnes) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("دسته‌های ده‌تایی", fontSize = 16.sp, color = Palette.ink)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            List(v.tens) { it }.chunked(5).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    row.forEach {
                        Box(
                            modifier = Modifier
                                .size(width = 52.dp, height = 44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Palette.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                10.fa(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
        if (v.ones > 0) {
            Text("تک‌ها", fontSize = 16.sp, color = Palette.ink)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                List(v.ones) { it }.chunked(5).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        row.forEach {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFB8C00))
                            )
                        }
                    }
                }
            }
        }
    }
}
