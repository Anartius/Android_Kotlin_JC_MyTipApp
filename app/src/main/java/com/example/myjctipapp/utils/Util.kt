package com.example.myjctipapp.utils

import kotlin.math.round

fun calculateTotalTip(totalBill: String, tipPercentage: Int): Double {

    return if (
        totalBill.isNotEmpty()
        && totalBill.toDouble() > 1
        && tipPercentage > 0
    ) {
        round((totalBill.toDouble() * tipPercentage) / 100)
    } else 0.0
}

fun calculateTotalPerPerson(
    totalBill: Double,
    tipAmount: Double,
    splitBy: Int
): Double {

    return (totalBill + tipAmount) / splitBy
}