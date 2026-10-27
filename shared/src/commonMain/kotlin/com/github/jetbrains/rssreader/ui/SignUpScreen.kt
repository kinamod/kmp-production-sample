package com.github.jetbrains.rssreader.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

private object InsureCoColors {
    val Red = Color(0xFFDA1E28)
    val RedDark = Color(0xFFA2191F)
    val Layer = Color(0xFF262626)
    val Field = Color(0xFF393939)
    val FieldBorder = Color(0xFF8D8D8D)
    val TextPrimary = Color(0xFFF4F4F4)
    val TextSecondary = Color(0xFFC6C6C6)
    val TextPlaceholder = Color(0xFF6F6F6F)
    val Interactive = Color(0xFF78A9FF)
    val SecondaryButton = Color(0xFF6F6F6F)
    val WarningBackground = Color(0xFFFFF4B4)
    val WarningText = Color(0xFF946C00)
    val Error = Color(0xFFFF8389)
}

private val SignUpSteps = listOf("Car details", "Driver info", "Coverage", "Review")
private const val VIN_LENGTH = 17
private const val MILEAGE_STEP = 1000

private enum class StepState { Complete, Current, Incomplete }

@OptIn(ExperimentalTime::class)
private fun yearOptions(): List<Int> {
    val currentYear = Clock.System.todayIn(TimeZone.currentSystemDefault()).year
    return (currentYear + 1 downTo 1980).toList()
}

@Composable
fun SignUpScreen(
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    var make by rememberSaveable { mutableStateOf("") }
    var model by rememberSaveable { mutableStateOf("") }
    var year by rememberSaveable { mutableIntStateOf(0) }
    var mileage by rememberSaveable { mutableIntStateOf(1000) }
    var milesPerYear by rememberSaveable { mutableIntStateOf(1000) }
    var vin by rememberSaveable { mutableStateOf("") }
    var showWarning by rememberSaveable { mutableStateOf(true) }
    var showErrors by rememberSaveable { mutableStateOf(false) }
    var completedSteps by rememberSaveable { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()
    val years = remember { yearOptions() }

    val vinInvalid = vin.isNotEmpty() && vin.length != VIN_LENGTH
    val isValid = make.isNotBlank() && model.isNotBlank() && year != 0 && !vinInvalid

    Box(
        modifier = modifier.verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 703.dp)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SignUpHeader()
            StepIndicator(currentStep = completedSteps)
            if (showWarning) {
                WarningBanner(
                    message = "Have your vehicle registration handy before you start.",
                    onDismiss = { showWarning = false }
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InsureCoColors.Layer)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(InsureCoColors.Red)
                )
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Car Details", fontSize = 24.sp, color = InsureCoColors.TextPrimary)
                        HorizontalDivider(color = InsureCoColors.Red.copy(alpha = 0.6f))
                    }
                    Text(
                        "Tell us about your car",
                        fontSize = 14.sp,
                        color = InsureCoColors.TextPrimary
                    )
                    FormField(
                        label = "Make",
                        errorText = "Enter the make of your car".takeIf { showErrors && make.isBlank() }
                    ) {
                        InsureCoTextField(
                            value = make,
                            onValueChange = { make = it },
                            placeholder = "e.g. Toyota, Ford",
                            isError = showErrors && make.isBlank()
                        )
                    }
                    FormField(
                        label = "Model",
                        errorText = "Enter the model of your car".takeIf { showErrors && model.isBlank() }
                    ) {
                        InsureCoTextField(
                            value = model,
                            onValueChange = { model = it },
                            placeholder = "e.g. Corolla, Bronco",
                            isError = showErrors && model.isBlank()
                        )
                    }
                    FormField(
                        label = "Year",
                        errorText = "Select a year".takeIf { showErrors && year == 0 }
                    ) {
                        YearSelect(
                            year = year.takeIf { it != 0 },
                            years = years,
                            isError = showErrors && year == 0,
                            onSelect = { year = it }
                        )
                    }
                    FormField(label = "Mileage", required = false) {
                        NumberField(value = mileage, onValueChange = { mileage = it })
                    }
                    FormField(label = "Miles driven per year", required = false) {
                        NumberField(value = milesPerYear, onValueChange = { milesPerYear = it })
                    }
                    FormField(
                        label = "VIN (optional)",
                        helperText = "$VIN_LENGTH digits",
                        errorText = "VIN must be exactly $VIN_LENGTH characters".takeIf { vinInvalid }
                    ) {
                        InsureCoTextField(
                            value = vin,
                            onValueChange = { input ->
                                vin = input.filter { it.isLetterOrDigit() }.uppercase().take(VIN_LENGTH)
                            },
                            placeholder = "",
                            isError = vinInvalid,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Characters
                            )
                        )
                    }
                    FormActions(
                        onCancel = {
                            make = ""
                            model = ""
                            year = 0
                            mileage = 1000
                            milesPerYear = 1000
                            vin = ""
                            showErrors = false
                            completedSteps = 0
                        },
                        onBack = onBack,
                        onNext = {
                            showErrors = true
                            if (isValid) {
                                completedSteps = 1
                                scope.launch { snackbarHostState.showSnackbar("Car details saved") }
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SignUpHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(InsureCoColors.Red, InsureCoColors.RedDark)),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Sign Up for InsureCo",
            fontSize = 28.sp,
            lineHeight = 34.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Text(
            "Get started with your insurance coverage in just a few steps",
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InsureCoColors.Layer, RoundedCornerShape(4.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SignUpSteps.forEachIndexed { index, label ->
            val state = when {
                index < currentStep -> StepState.Complete
                index == currentStep -> StepState.Current
                else -> StepState.Incomplete
            }
            Column(modifier = Modifier.weight(1f)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(
                            if (state == StepState.Incomplete) InsureCoColors.FieldBorder
                            else InsureCoColors.Interactive
                        )
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StepIcon(state = state, modifier = Modifier.padding(top = 2.dp).size(14.dp))
                    Column {
                        Text(
                            "Step ${index + 1}",
                            fontSize = 14.sp,
                            color = InsureCoColors.TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            label,
                            fontSize = 12.sp,
                            color = InsureCoColors.TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StepIcon(state: StepState, modifier: Modifier = Modifier) {
    if (state == StepState.Complete) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = InsureCoColors.Interactive,
            modifier = modifier
        )
        return
    }
    Canvas(modifier = modifier) {
        val strokeWidth = 1.dp.toPx()
        val radius = size.minDimension / 2 - strokeWidth / 2
        if (state == StepState.Current) {
            drawCircle(InsureCoColors.Interactive, radius, style = Stroke(strokeWidth))
            drawArc(
                color = InsureCoColors.Interactive,
                startAngle = 90f,
                sweepAngle = 180f,
                useCenter = true
            )
        } else {
            val dash = 2.dp.toPx()
            drawCircle(
                InsureCoColors.TextSecondary,
                radius,
                style = Stroke(strokeWidth, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash)))
            )
        }
    }
}

@Composable
private fun WarningBanner(message: String, onDismiss: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(InsureCoColors.WarningBackground)
            .border(1.dp, Color.Black)
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Outlined.Info,
            contentDescription = null,
            tint = InsureCoColors.WarningText,
            modifier = Modifier.size(16.dp).rotate(180f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            message,
            fontSize = 14.sp,
            color = InsureCoColors.WarningText,
            modifier = Modifier.weight(1f).padding(vertical = 12.dp)
        )
        TextButton(
            onClick = onDismiss,
            colors = ButtonDefaults.textButtonColors(contentColor = InsureCoColors.WarningText)
        ) {
            Text("Dismiss", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun FormField(
    label: String,
    required: Boolean = true,
    helperText: String? = null,
    errorText: String? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            fontSize = 12.sp,
            fontWeight = if (required) FontWeight.SemiBold else FontWeight.Normal,
            color = InsureCoColors.TextSecondary
        )
        Spacer(Modifier.height(8.dp))
        content()
        val supportingText = errorText ?: helperText
        if (supportingText != null) {
            Spacer(Modifier.height(4.dp))
            Text(
                supportingText,
                fontSize = 12.sp,
                color = if (errorText != null) InsureCoColors.Error else InsureCoColors.TextSecondary
            )
        }
    }
}

@Composable
private fun insureCoFieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = InsureCoColors.Field,
    unfocusedContainerColor = InsureCoColors.Field,
    errorContainerColor = InsureCoColors.Field,
    focusedTextColor = InsureCoColors.TextPrimary,
    unfocusedTextColor = InsureCoColors.TextPrimary,
    errorTextColor = InsureCoColors.TextPrimary,
    focusedIndicatorColor = InsureCoColors.Interactive,
    unfocusedIndicatorColor = InsureCoColors.FieldBorder,
    errorIndicatorColor = InsureCoColors.Error,
    focusedPlaceholderColor = InsureCoColors.TextPlaceholder,
    unfocusedPlaceholderColor = InsureCoColors.TextPlaceholder,
    cursorColor = InsureCoColors.TextPrimary,
    errorCursorColor = InsureCoColors.Error
)

@Composable
private fun InsureCoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isError: Boolean,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        isError = isError,
        keyboardOptions = keyboardOptions,
        shape = RectangleShape,
        colors = insureCoFieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun YearSelect(
    year: Int?,
    years: List<Int>,
    isError: Boolean,
    onSelect: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val borderColor = if (isError) InsureCoColors.Error else InsureCoColors.FieldBorder
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(InsureCoColors.Field)
                .drawBehind {
                    val strokeWidth = 1.dp.toPx()
                    val y = size.height - strokeWidth / 2
                    drawLine(borderColor, Offset(0f, y), Offset(size.width, y), strokeWidth)
                }
                .clickable { expanded = true }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                year?.toString() ?: "Select a year",
                color = if (year == null) InsureCoColors.TextPlaceholder else InsureCoColors.TextPrimary,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = InsureCoColors.TextPrimary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            years.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.toString()) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun NumberField(value: Int, onValueChange: (Int) -> Unit) {
    TextField(
        value = value.toString(),
        onValueChange = { text ->
            onValueChange(text.filter(Char::isDigit).take(7).toIntOrNull() ?: 0)
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        trailingIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onValueChange((value - MILEAGE_STEP).coerceAtLeast(0)) },
                    enabled = value > 0
                ) {
                    Text("\u2212", fontSize = 20.sp, color = InsureCoColors.TextPrimary)
                }
                VerticalDivider(
                    modifier = Modifier.height(16.dp),
                    color = InsureCoColors.FieldBorder
                )
                IconButton(onClick = { onValueChange(value + MILEAGE_STEP) }) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = "Increase",
                        tint = InsureCoColors.TextPrimary
                    )
                }
            }
        },
        shape = RectangleShape,
        colors = insureCoFieldColors(),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun FormActions(onCancel: () -> Unit, onBack: () -> Unit, onNext: () -> Unit) {
    val buttonPadding = PaddingValues(horizontal = 12.dp)
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onCancel,
            shape = RectangleShape,
            border = BorderStroke(1.dp, InsureCoColors.Interactive),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = InsureCoColors.Interactive),
            contentPadding = buttonPadding,
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            Text("Cancel")
        }
        Button(
            onClick = onBack,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = InsureCoColors.SecondaryButton,
                contentColor = Color.White
            ),
            contentPadding = buttonPadding,
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            Text("Back")
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
        }
        Button(
            onClick = onNext,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = InsureCoColors.Red,
                contentColor = Color.White
            ),
            contentPadding = buttonPadding,
            modifier = Modifier.weight(1f).height(48.dp)
        ) {
            Text("Next")
            Spacer(Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
        }
    }
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    AppTheme {
        SignUpScreen(onBack = {}, snackbarHostState = remember { SnackbarHostState() })
    }
}
