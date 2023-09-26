package com.justai.jaicf.template.scenario

import com.justai.jaicf.activator.caila.caila
import com.justai.jaicf.builder.Scenario
import javax.swing.text.StyledEditorKit.BoldAction

var numberToGuessStr = ""
fun checkCowsAndBulls(numberToGuessStr:String, inputNumberStr:String):Pair<Boolean, String>
{
    if(numberToGuessStr == inputNumberStr)
        return Pair(true, "Угадал!")

    var cows = 0;
    var bulls = 0;
    for(i in 0..3)
    {
        for (j in 0..3)
        {
            if(numberToGuessStr[i]==inputNumberStr[j])
            {
                if(i == j)
                    bulls++;
                else
                    cows++;
            }
        }
    }
    var res = "Коровы: " + cows.toString() + " Быки: " + bulls.toString()
    return Pair(false, res)
}

fun validateInputNum(inputNumberStr: String): Boolean
{
    try
    {
        var inputNumber: Int = inputNumberStr.toInt()
        var uniqueDigits = inputNumberStr.groupBy { it }.keys
        if(uniqueDigits.count() == 4 && inputNumberStr.length == 4)
            return  true;
        else return  false;
    }
    catch(ex:Exception)
    {
        return false;
    }
}

fun generateRandomNumberStr():String
{
    var string = "0123456789";
    var t =  string.toList().shuffled()
    var slice = t.slice(0..3)
    var str = ""
    for(char in slice) str += char;
    return str;
}

val mainScenario = Scenario {
    state("start") {
        activators {
            regex("/start")
            intent("Привет")
        }
        action {
            reactions.run {
                image("https://media.giphy.com/media/ICOgUNjpvO0PC/source.gif")
                say("Привет! Чем могу помочь?")
                buttons(
                    "Хочу сыграть.",
                    "Ничем.",
                )
            }
        }
    }

    state("areyouready"){
        activators {
            intent("БиК")

        }
        action {
            reactions.run {
                say("Я загадаю тайное четырехзначное число, тебе надо будет отгадать его. После каждой попытки я буду говорить, сколько коров и сколько быков. Коровы - сколько цифр угадано без совпадения с их позициями в тайном числе. Быки - сколько цифр угадано вплоть до позиции в тайном числе. Начинаем?")
                buttons(
                    "Да!",
                    "Нет.",
                )
            }
        }
    }
    state("startgame"){
        activators {
            intent("Старт")

        }
        action {
            reactions.run {
                say("Я загадл число. Отгадай.")
                numberToGuessStr = generateRandomNumberStr()
            }
        }
    }

    state("try"){
        activators{
            regex("[0-9]{4}")
        }
        action {
            reactions.run{

                if (validateInputNum(request.input))
                {
                    var res  = checkCowsAndBulls(numberToGuessStr,request.input)
                    say(res.second)
                    if(res.first)
                    {
                        say("Сыграем еще?")
                        buttons(
                            "Да!",
                            "Нет.")
                    }
                }
                else
                {
                    say("Неверный формат числа")
                }
            }
        }
    }

    state("пока") {
        activators {
            intent("Пока")
        }

        action {
            reactions.sayRandom(
                "До скорого!",
                "Пока-пока!"
            )
            reactions.image("https://media.giphy.com/media/EE185t7OeMbTy/source.gif")
        }
    }


    state("маленькийразговор", noContext = true) {
        activators {
            anyIntent()
        }

        action(caila) {
            activator.topIntent.answer?.let { reactions.say(it) } ?: reactions.go("/fallback")
        }
    }

    fallback {
        reactions.sayRandom(
            "Я не понимаю тебя...",
            "Не понял, повтори, пожалуйста."
        )
    }
}