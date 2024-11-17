package com.example.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {

    private lateinit var tvDisplay: TextView
    private lateinit var button0: MaterialButton
    private lateinit var button1: MaterialButton
    private lateinit var button2: MaterialButton
    private lateinit var button3: MaterialButton
    private lateinit var button4: MaterialButton
    private lateinit var button5: MaterialButton
    private lateinit var button6: MaterialButton
    private lateinit var button7: MaterialButton
    private lateinit var button8: MaterialButton
    private lateinit var button9: MaterialButton
    private lateinit var buttonPlus: MaterialButton
    private lateinit var buttonMinus: MaterialButton
    private lateinit var buttonMultiply: MaterialButton
    private lateinit var buttonDivide: MaterialButton
    private lateinit var buttonAC: MaterialButton
    private lateinit var buttonBackspace: MaterialButton
    private lateinit var buttonPercent: MaterialButton
    private lateinit var buttonDot: MaterialButton
    private lateinit var buttonEquals: MaterialButton
    private lateinit var buttonParentheses: MaterialButton
    private lateinit var tvPreview: TextView

    private var currentNumber: String = ""
    private var previousNumber: String = ""
    private var operator: String = ""
    private var isOperatorPressed: Boolean = false
    private var expression: String = "" // Variável para armazenar a expressão completa

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicialize as variáveis com findViewById
        tvDisplay = findViewById(R.id.tvDisplay)
        button0 = findViewById(R.id.button0)
        button1 = findViewById(R.id.button1)
        button2 = findViewById(R.id.button2)
        button3 = findViewById(R.id.button3)
        button4 = findViewById(R.id.button4)
        button5 = findViewById(R.id.button5)
        button6 = findViewById(R.id.button6)
        button7 = findViewById(R.id.button7)
        button8 = findViewById(R.id.button8)
        button9 = findViewById(R.id.button9)
        buttonPlus = findViewById(R.id.buttonPlus)
        buttonMinus = findViewById(R.id.buttonMinus)
        buttonMultiply = findViewById(R.id.buttonMultiply)
        buttonDivide = findViewById(R.id.buttonDivide)
        buttonAC = findViewById(R.id.buttonAC)
        buttonBackspace = findViewById(R.id.buttonBackspace)
        buttonPercent = findViewById(R.id.buttonPercent)
        buttonDot = findViewById(R.id.buttonDot)
        buttonEquals = findViewById(R.id.buttonEquals)
        buttonParentheses = findViewById(R.id.buttonParentheses)
        tvPreview = findViewById(R.id.tvPreview)


        setupButtons()
    }

    private fun setupButtons() {
        // Configura botões numéricos
        val numericButtons = listOf(button0, button1, button2, button3, button4, button5, button6, button7, button8, button9)
        numericButtons.forEach { button ->
            button.setOnClickListener { onNumericButtonClicked(button.text.toString()) }
        }

        // Configura botões de operação
        buttonPlus.setOnClickListener { onOperatorButtonClicked("+") }
        buttonMinus.setOnClickListener { onOperatorButtonClicked("-") }
        buttonMultiply.setOnClickListener { onOperatorButtonClicked("*") }
        buttonDivide.setOnClickListener { onOperatorButtonClicked("/") }

        // Configura botões de funções especiais
        buttonAC.setOnClickListener { resetCalculator() }
        buttonBackspace.setOnClickListener { backspace() }
        buttonPercent.setOnClickListener { calculatePercentage() }
        buttonDot.setOnClickListener { addDecimalPoint() }
        buttonEquals.setOnClickListener { calculateResult() }
        buttonParentheses.setOnClickListener { addParentheses() }
    }

    private fun backspace() {
        if (expression.isNotEmpty()) {
            // Remove o último caractere do histórico de expressão
            expression = expression.dropLast(1)

            // Atualiza o display principal e o preview
            tvDisplay.text = if (expression.isNotEmpty()) expression else "0"
            tvPreview.text = "" // Limpa o preview após backspace
        }
    }


    private fun onNumericButtonClicked(value: String) {
        if (isOperatorPressed) {
            currentNumber = ""
            isOperatorPressed = false
        }
        currentNumber += value
        expression += value // Adiciona o número à expressão
        tvDisplay.text = expression // Atualiza o display com a expressão completa

        updatePreview()
    }

    private fun onOperatorButtonClicked(op: String) {
        if (operator.isEmpty() && currentNumber.isNotEmpty()) {
            if (previousNumber.isNotEmpty()) {
                calculateResult()
            }
            previousNumber = currentNumber
            operator = op
            isOperatorPressed = true
            expression += " $operator "
            tvDisplay.text = expression
        }
        updatePreview()
    }

    private fun resetCalculator() {
        currentNumber = ""
        previousNumber = ""
        operator = ""
        isOperatorPressed = false
        expression = "" // Reseta a expressão
        tvDisplay.text = "0" // Reseta o display principal
        tvPreview.text = "" // Limpa o preview
    }


    private fun calculatePercentage() {
        if (currentNumber.isNotEmpty()) {
            val number = currentNumber.toDouble() / 100
            currentNumber = number.toString()
            expression = expression.dropLast(currentNumber.length) + currentNumber
            tvDisplay.text = expression
        }
    }

    private fun addDecimalPoint() {
        if (!currentNumber.contains(".")) {
            currentNumber += "."
            expression += "."
            tvDisplay.text = expression
        }
    }


    private fun addParentheses() {
        val openParentheses = expression.count { it == '(' }
        val closeParentheses = expression.count { it == ')' }

        if (openParentheses > closeParentheses && (expression.isNotEmpty() && (expression.last().isDigit() || expression.last() == ')'))) {
            // Fecha parênteses se há mais abertos do que fechados e o último caractere é um dígito ou ')'
            expression += ")"
        } else {
            // Abre parênteses
            if (expression.isNotEmpty() && (expression.last().isDigit() || expression.last() == ')')) {
                expression += " * ("
            } else {
                expression += "("
            }
        }
        tvDisplay.text = expression

        updatePreview()
    }

    //private fun isOperator(char: Char): Boolean {
    //    return char == '+' || char == '-' || char == '*' || char == '/'
    //}

    @SuppressLint("SetTextI18n")
    private fun calculateResult() {
        try {
            // Prepara a expressão para cálculo
            val preparedExpression = expression.replace(Regex("(?<=[0-9)])(?=\\()"), " * ")
            val result = evaluateExpression(preparedExpression)

            // Exibe apenas o resultado no tvDisplay
            tvDisplay.text = result.toString()

            // Limpa a expressão e o preview
            tvPreview.text = ""
            expression = result.toString() // Atualiza a expressão para conter apenas o resultado
            currentNumber = result.toString()
            previousNumber = ""
            operator = ""
        } catch (e: Exception) {
            tvDisplay.text = "Error"
            expression = ""
            currentNumber = ""
            previousNumber = ""
        }
    }


    @SuppressLint("SetTextI18n")
    private fun updatePreview() {
        try {
            if (expression.isNotEmpty()) {
                val preparedExpression = expression.replace(Regex("(?<=[0-9)])(?=\\()"), " * ")
                val result = evaluateExpression(preparedExpression)
                tvPreview.text = "= $result"
            } else {
                tvPreview.text = ""
            }
        } catch (e: Exception) {
            tvPreview.text = ""
        }
    }


    private fun evaluateExpression(exp: String): Double {
        val stack = mutableListOf<Double>()
        val operators = mutableListOf<Char>()
        val tokens = Regex("([0-9.]+|[+\\-*/()]|[^\\s])").findAll(exp).map { it.value }

        for (token in tokens) {
            when {
                token.matches(Regex("[0-9.]+")) -> stack.add(token.toDouble())
                token in "+-*/" -> operators.add(token[0])
                token == "(" -> operators.add('(')
                token == ")" -> {
                    while (operators.isNotEmpty() && operators.last() != '(') {
                        val b = stack.removeAt(stack.size - 1)
                        val a = stack.removeAt(stack.size - 1)
                        val op = operators.removeAt(operators.size - 1)
                        stack.add(applyOperation(a, b, op))
                    }
                    if (operators.isNotEmpty() && operators.last() == '(') {
                        operators.removeAt(operators.size - 1)
                    }
                }
            }
        }
        while (operators.isNotEmpty()) {
            val b = stack.removeAt(stack.size - 1)
            val a = stack.removeAt(stack.size - 1)
            val op = operators.removeAt(operators.size - 1)
            stack.add(applyOperation(a, b, op))
        }
        return stack.last()
    }

    private fun applyOperation(a: Double, b: Double, op: Char): Double {
        return when (op) {
            '+' -> a + b
            '-' -> a - b
            '*' -> a * b
            '/' -> {
                if (b == 0.0) throw ArithmeticException("Division by zero")
                a / b
            }
            else -> throw IllegalArgumentException("Unknown operator: $op")
        }
    }
}
