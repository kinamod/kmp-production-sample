package com.github.jetbrains.rssreader.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val InsureRed = Color(0xFFDA1E28)
private val InsureDarkRed = Color(0xFF9E1B22)
private val InsureBlue = Color(0xFF0F62FE)
private val InsureGray = Color(0xFFF4F4F4)
private val InsureDivider = Color(0xFFE0E0E0)
private val InsureCharcoal = Color(0xFF393939)

private enum class InsuranceType(val title: String, val subtitle: String, val icon: String) {
    Car("Car Insurance", "Get comprehensive coverage for your vehicle", "\uD83D\uDE97"),
    Home("Home Insurance", "Protect your most important asset for your family", "\uD83C\uDFE0"),
    Both("Both Home and Car", "Insure both and get bundle savings", "\uD83D\uDE97\uD83C\uDFE0")
}

@Composable
fun InsuranceSignUpScreen(onBack: () -> Unit) {
    var selected by remember { mutableStateOf<InsuranceType?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SignUpHeader()
        Spacer(Modifier.size(24.dp))
        StepProgress(currentStep = 0, stepCount = 4)
        Spacer(Modifier.size(24.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(InsureGray)
                .padding(24.dp)
        ) {
            Text("What Will You Insure", color = Color.Black, fontSize = 24.sp)
            Spacer(Modifier.size(8.dp))
            Box(Modifier.fillMaxWidth().height(1.dp).background(InsureRed))
            Spacer(Modifier.size(24.dp))
            Text("Which insurance coverage are you looking for", color = Color.Black, fontSize = 14.sp)
            Spacer(Modifier.size(24.dp))
            InsuranceType.entries.forEach { type ->
                InsuranceTile(
                    type = type,
                    isSelected = selected == type,
                    onClick = { selected = type }
                )
                Spacer(Modifier.size(24.dp))
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(InsureRed))
            Spacer(Modifier.size(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FormButton(text = "Back", arrow = "\u2190", color = InsureCharcoal, onClick = onBack)
                Spacer(Modifier.size(16.dp))
                FormButton(
                    text = "Next",
                    arrow = "\u2192",
                    color = InsureRed,
                    enabled = selected != null,
                    onClick = {}
                )
            }
        }
    }
}

@Composable
private fun SignUpHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Brush.linearGradient(listOf(InsureRed, InsureDarkRed)))
            .padding(vertical = 28.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Sign Up for InsureCo", color = Color.White, fontSize = 28.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.size(12.dp))
        Text(
            "Get started with your insurance coverage in just a few steps",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StepProgress(currentStep: Int, stepCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InsureGray)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        repeat(stepCount) { index ->
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(if (index <= currentStep) InsureBlue else InsureDivider)
                )
                Spacer(Modifier.size(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .border(1.dp, InsureBlue.takeIf { index == currentStep } ?: InsureCharcoal, CircleShape)
                            .background(if (index == currentStep) InsureBlue else Color.Transparent)
                    )
                    Spacer(Modifier.size(8.dp))
                    Column {
                        Text("Step", color = Color.Black, fontSize = 14.sp)
                        Text("Optional label", color = InsureCharcoal, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun InsuranceTile(type: InsuranceType, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(if (isSelected) 2.dp else 0.dp, if (isSelected) InsureRed else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(type.icon, fontSize = 28.sp)
        Spacer(Modifier.size(16.dp))
        Column {
            Text(type.title, color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.size(8.dp))
            Text(type.subtitle, color = Color.Black, fontSize = 14.sp)
        }
    }
}

@Composable
private fun FormButton(
    text: String,
    arrow: String,
    color: Color,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = color.copy(alpha = 0.4f),
            disabledContentColor = Color.White
        )
    ) {
        Text(text)
        Spacer(Modifier.size(16.dp))
        Text(arrow)
    }
}
