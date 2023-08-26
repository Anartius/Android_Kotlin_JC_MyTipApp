package com.example.myjctipapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myjctipapp.components.InputField
import com.example.myjctipapp.ui.theme.MyJCTipAppTheme
import com.example.myjctipapp.utils.calculateTotalPerPerson
import com.example.myjctipapp.utils.calculateTotalTip
import com.example.myjctipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                MainContent()
            }
        }
    }
}


@Composable
fun MyApp(content: @Composable () -> Unit) {
    MyJCTipAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            content()
        }
    }
}


@Composable
fun TopHeader(totalPerPerson: Double = 0.0) {
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp),
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)

    ) {
        Column (
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Total Per Person",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "$${"%.2f".format(totalPerPerson)}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

        }
    }
}


@Composable
fun MainContent() {
    val splitByState = remember {
        mutableIntStateOf(1)
    }

    val splitByRange = IntRange(1, 100)

    val tipAmountState = remember {
        mutableDoubleStateOf(0.0)
    }

    val billPerPersonState = remember {
        mutableDoubleStateOf(0.00)
    }

    Column(
        modifier = Modifier.padding(12.dp)
    ) {
        BillForm(
            splitByRange = splitByRange,
            splitByState = splitByState,
            tipAmountState = tipAmountState,
            billPerPersonState = billPerPersonState
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    splitByRange: IntRange = 1..100,
    splitByState: MutableIntState,
    tipAmountState: MutableDoubleState,
    billPerPersonState: MutableDoubleState,
    onValChange: (String) -> Unit = {}
) {

    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val sliderPositionState = remember {
        mutableFloatStateOf(0f)
    }

    val tipPercentageState = remember {
        mutableIntStateOf(0)
    }

    val keyboardController = LocalSoftwareKeyboardController.current

    TopHeader(billPerPersonState.doubleValue)

    Surface (
        modifier = Modifier
            .padding(top = 12.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            OutlinedTextField(
                value = totalBillState.value,
                onValueChange = { inputString ->
                    var value = inputString.trim().replace(',', '.')

                    if (value.length == 1 && value.first() == '0') {
                        value = value.drop(1)
                    }
                    if (value.count { it == '.' } > 1) {
                        value = value.dropLast(1)
                    }

                    if (!("\\d+[.]?.{0,2}$".toRegex().matches(value))) {
                        value = value.dropLast(1)
                    }

                    totalBillState.value = value

                    if (totalBillState.value.isNotEmpty()) {

                        tipAmountState.doubleValue = calculateTotalTip(
                            totalBillState.value,
                            tipPercentageState.intValue
                        )

                        billPerPersonState.doubleValue = calculateTotalPerPerson(
                            totalBillState.value.toDouble(),
                            tipAmountState.doubleValue,
                            splitByState.intValue
                        )
                    } else {
                        billPerPersonState.doubleValue = 0.0
                    }

                },
                modifier = Modifier.fillMaxWidth(),
                enabled = true,
                singleLine = true,
                label = { Text(text = "Enter Bill") },
                leadingIcon = { Icon(
                    imageVector = Icons.Rounded.AttachMoney,
                    contentDescription = "Money Icon")},
                keyboardOptions =
                KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                )
            )

            if (validState) {
                Row(
                    modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        modifier = Modifier.align(
                            alignment = Alignment.CenterVertically
                        )
                    )

                    Spacer(modifier = Modifier.width(120.dp))

                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {

                                if (splitByState.intValue > splitByRange.first) {
                                    splitByState.intValue --
                                }

                                billPerPersonState.doubleValue = calculateTotalPerPerson(
                                    totalBillState.value.toDouble(),
                                    tipAmountState.doubleValue,
                                    splitByState.intValue
                                )
                            },
                            contentDescription = "Minus button"
                        )
                        Text(
                            text = "${splitByState.intValue}",
                            modifier = Modifier
                                .padding(horizontal = 9.dp)
                                .align(Alignment.CenterVertically)
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {

                                if (splitByState.intValue < splitByRange.last) {
                                    splitByState.intValue ++
                                }

                                billPerPersonState.doubleValue = calculateTotalPerPerson(
                                    totalBillState.value.toDouble(),
                                    tipAmountState.doubleValue,
                                    splitByState.intValue
                                )
                            },
                            contentDescription = "Plus button"
                        )
                    }
                }

                Row(
                    modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Tip",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                    )

                    Text(
                        text = "$ ${tipAmountState.doubleValue}",
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f)
                            .padding(end = 12.dp),
                        textAlign = TextAlign.End
                    )
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "${tipPercentageState.intValue}%")

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(
                        value = sliderPositionState.floatValue,
                        onValueChange = { newVal ->
                            sliderPositionState.floatValue = newVal
                            tipPercentageState.intValue = (sliderPositionState.floatValue * 100).toInt()

                            tipAmountState.doubleValue = calculateTotalTip(
                                totalBillState.value,
                                tipPercentageState.intValue
                            )

                            billPerPersonState.doubleValue = calculateTotalPerPerson(
                                totalBillState.value.toDouble(),
                                tipAmountState.doubleValue,
                                splitByState.intValue
                            )
                        },
                        modifier = Modifier.padding(horizontal = 16.dp),
                        steps = 100
                    )
                }

            } else {
                Box {}

                totalBillState.value = ""
                sliderPositionState.floatValue = 0.0f
                tipPercentageState.intValue = 0
                tipAmountState.doubleValue = 0.0
                billPerPersonState.doubleValue = 0.0
                splitByState.intValue = 1

            }

        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyJCTipAppTheme {
        MainContent()
    }
}