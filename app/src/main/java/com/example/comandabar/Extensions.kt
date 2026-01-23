package com.example.comandabar.extensions

fun Double.formatDouble(digits: Int): String {
    return "%.${digits}f".format(this)
}