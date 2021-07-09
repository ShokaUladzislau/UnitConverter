package converter

import kotlin.system.exitProcess

enum class LengthObj(val small: String, val single: String, val several: String, val ofMeter: Double) {
    METER("m", "meter", "meters", 1.0),
    KILOMETER("km", "kilometer", "kilometers", 1000.0),
    CENTIMETER("cm", "centimeter", "centimeters", 0.01),
    MILLIMETER("mm", "millimeter", "millimeters", 0.001),
    MILE("mi", "mile", "miles", 1609.35),
    YARD("yd", "yard", "yards", 0.9144),
    FOOT("ft", "foot", "feet", 0.3048),
    INCH("in", "inch", "inches", 0.0254)
}

enum class WeightObj(val small: String, val single: String, val several: String, val ofGram: Double) {
    GRAM("g", "gram", "grams", 1.0),
    KILOGRAM("kg", "kilogram", "kilograms", 1000.0),
    MILLIGRAM("mg", "milligram", "milligrams", 0.001),
    POUND("lb", "pound", "pounds", 453.592),
    OUNCE("oz", "ounce", "ounces", 28.3495)
}

enum class TempObj(val small: String, val single: String, val short: String, val several: String, val full: String, val ofCelsius: Double
) {
    KELVIN("k", "kelvin", "k", "kelvins", "kelvins", 1.0),
    CELSIUS("c", "degree Celsius", "dc", "celsius", "degrees Celsius", 1.0),
    FAHRENHEIT("f", "degree Fahrenheit", "df", "fahrenheit", "degrees Fahrenheit", 1.0 - 32.0 * (5 / 9))

}


fun String.toLengthObj(): LengthObj = LengthObj.values().filter {
    toLowerCase() == it.small || toLowerCase() == it.single || toLowerCase() == it.several
}[0]

fun String.toWeightObj(): WeightObj = WeightObj.values().filter {
    toLowerCase() == it.small || toLowerCase() == it.single || toLowerCase() == it.several
}[0]

fun String.toTempObj(): TempObj = TempObj.values().filter {
    toLowerCase() == it.small || toLowerCase() == it.single || toLowerCase() == it.short || toLowerCase() == it.several || toLowerCase() == it.full
}[0]

fun Double.convert(objFrom: LengthObj, objTo: LengthObj) = this * objFrom.ofMeter / objTo.ofMeter

fun Double.convert(objFrom: WeightObj, objTo: WeightObj) = this * objFrom.ofGram / objTo.ofGram

fun Double.convert(objFrom: TempObj, objTo: TempObj): Double {
    if (objFrom.small == "c" && objTo.small == "k") {
        return this * objFrom.ofCelsius / objTo.ofCelsius + 273.15
    } else if (objFrom.small == "c" && objTo.small == "f") {
        return this * (objFrom.ofCelsius / objTo.ofCelsius * 9 / 5) + 32
    } else if (objFrom.small == "f" && objTo.small == "c") {
        return ((this * objFrom.ofCelsius / objTo.ofCelsius) - 32) * 5 / 9
    } else if (objFrom.small == "f" && objTo.small == "k") {
        return ((this * objFrom.ofCelsius / objTo.ofCelsius) + 459.67) * 5 / 9
    } else if (objFrom.small == "k" && objTo.small == "f") {
        return ((this * objFrom.ofCelsius / objTo.ofCelsius) * 9 / 5) - 459.67
    } else if (objFrom.small == "k" && objTo.small == "c") {
        return this * objFrom.ofCelsius / objTo.ofCelsius - 273.15
    }
    return this * objFrom.ofCelsius / objTo.ofCelsius
}

fun transformInput(input: MutableList<String>): MutableList<String> {

    repeat(input.size) {
        for (i in 0 until input.size) {
            if (input[i] == "degree" || input[i] == "degrees") {
                if (input[i + 1] == "fahrenheit") {
                    input[i] = "f"
                    input.removeAt(i+1)
                    break
                } else if (input[i + 1] == "celsius") {
                    input[i] = "c"
                    input.removeAt(i+1)
                    break
                } else {
                    input[i] = "x"
                    input.removeAt(i+1)
                    break
                }
            }
        }
    }

    return input

}

fun main() {
    var objFromOK: Boolean
    var objToOK: Boolean
    var typeFrom = 0
    var typeTo = 0
    var errMsgFrom: String
    var errMsgTo: String

    while (true) {
        print("Enter what you want to convert (or exit): ")
        var input = readLine()!!.split(" ").toMutableList()

        for (i in 0 until input.size) {
            input[i] = input[i].toLowerCase()
        }


        input = transformInput(input)

        try {

            if (input[0] == "exit") {
                exitProcess(0)
            }

            val value = input[0].toDouble()

            objFromOK = true
            objToOK = true

            try {
                val objFrom = input[1].toLengthObj()
                typeFrom = 0
                errMsgFrom = objFrom.several
            } catch (e: Exception) {
                try {
                    val objFrom = input[1].toWeightObj()
                    typeFrom = 1
                    errMsgFrom = objFrom.several
                } catch (e: Exception) {
                    try {
                        val objFrom = input[1].toTempObj()
                        typeFrom = 2
                        errMsgFrom = objFrom.full
                    } catch (e: Exception) {
                        objFromOK = false
                        errMsgFrom = "???"
                    }
                }
            }

            try {
                val objTo = input[3].toLengthObj()
                typeTo = 0
                errMsgTo = objTo.several
            } catch (e: Exception) {
                try {
                    val objTo = input[3].toWeightObj()
                    typeTo = 1
                    errMsgTo = objTo.several
                } catch (e: Exception) {
                    try {
                        val objTo = input[3].toTempObj()
                        typeTo = 2
                        errMsgTo = objTo.full
                    } catch (e: Exception) {
                        objToOK = false
                        errMsgTo = "???"
                    }
                }
            }

            if (!objFromOK || !objToOK) {
                println("Conversion from $errMsgFrom to $errMsgTo is impossible")
            } else if (typeFrom != typeTo) {
                println("Conversion from $errMsgFrom to $errMsgTo is impossible")
            } else if (typeFrom == 0) {
                val objFrom = input[1].toLengthObj()
                val objTo = input[3].toLengthObj()
                val convertValue: Double = value.convert(objFrom, objTo)
                if (convertValue < 0) {
                    println("Length shouldn't be negative.")
                } else {
                    println(
                        "$value ${if (value == 1.0) objFrom.single else objFrom.several} " +
                                "is $convertValue ${if (convertValue == 1.0) objTo.single else objTo.several}"
                    )
                }
            } else if (typeFrom == 1) {
                val objFrom = input[1].toWeightObj()
                val objTo = input[3].toWeightObj()
                val convertValue: Double = value.convert(objFrom, objTo)
                if (convertValue < 0) {
                    println("Weight shouldn't be negative.")
                } else {
                    println(
                        "$value ${if (value == 1.0) objFrom.single else objFrom.several} " +
                                "is $convertValue ${if (convertValue == 1.0) objTo.single else objTo.several}"
                    )
                }
            } else if (typeFrom == 2) {
                val objFrom = input[1].toTempObj()
                val objTo = input[3].toTempObj()
                val convertValue: Double = value.convert(objFrom, objTo)
                println(
                    "$value ${if (value == 1.0) objFrom.single else objFrom.full} " +
                            "is $convertValue ${if (convertValue == 1.0) objTo.single else objTo.full}"
                )
            }
            println("")
        } catch (e: Exception) {
            println("Parse error\n")
        }
    }
}


