package com.example.calculatorapp;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView resultTv;
    MaterialButton buttonC, buttonDivide, buttonMultiply, buttonMinus, buttonPlus;
    MaterialButton button7, button8, button9, button4, button5, button6, button1, button2, button3;
    MaterialButton buttonDot, button0, buttonEquals;

    private String currentInput = "";
    private boolean lastNumeric = false;
    private boolean lastDot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTv = findViewById(R.id.expressionText);

        buttonC = findViewById(R.id.button_c);
        buttonDivide = findViewById(R.id.button_divide);
        buttonMultiply = findViewById(R.id.button_x);
        buttonMinus = findViewById(R.id.button_minus);
        buttonPlus = findViewById(R.id.button_plus);
        button7 = findViewById(R.id.button_7);
        button8 = findViewById(R.id.button_8);
        button9 = findViewById(R.id.button_9);
        button4 = findViewById(R.id.button_4);
        button5 = findViewById(R.id.button_5);
        button6 = findViewById(R.id.button_6);
        button1 = findViewById(R.id.button_1);
        button2 = findViewById(R.id.button_2);
        button3 = findViewById(R.id.button_3);
        buttonDot = findViewById(R.id.button_dot);
        button0 = findViewById(R.id.button_0);
        buttonEquals = findViewById(R.id.button_equals);

        buttonC.setOnClickListener(this);
        buttonDivide.setOnClickListener(this);
        buttonMultiply.setOnClickListener(this);
        buttonMinus.setOnClickListener(this);
        buttonPlus.setOnClickListener(this);
        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        buttonDot.setOnClickListener(this);
        button0.setOnClickListener(this);
        buttonEquals.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        MaterialButton button = (MaterialButton) v;
        String buttonText = button.getText().toString();

        if (buttonText.equals("C")) {
            clearAll();
        } else if (buttonText.equals("=")) {
            calculateResult();
        } else if (buttonText.equals(".")) {
            handleDecimalPoint();
        } else if (isOperator(buttonText)) {
            handleOperator(buttonText);
        } else {
            handleNumber(buttonText);
        }

        updateDisplay();
    }

    private void handleNumber(String number) {
        if (currentInput.equals("0")) {
            currentInput = number;
        } else {
            currentInput += number;
        }
        lastNumeric = true;
        lastDot = false;
    }

    private void handleOperator(String operator) {
        if (currentInput.isEmpty()) {
            return;
        }

        if (!lastNumeric && !currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        }

        currentInput += operator;
        lastNumeric = false;
        lastDot = false;
    }

    private void handleDecimalPoint() {
        if (lastDot || !lastNumeric) {
            return;
        }

        currentInput += ".";
        lastDot = true;
        lastNumeric = false;
    }

    private boolean isOperator(String text) {
        return text.equals("+") || text.equals("-") || text.equals("x") || text.equals("/");
    }

    private void clearAll() {
        currentInput = "";
        lastNumeric = false;
        lastDot = false;
        updateDisplay();
    }

    private void calculateResult() {
        if (currentInput.isEmpty() || !lastNumeric) {
            return;
        }

        try {
            String expression = currentInput.replaceAll("x", "*");
            double result = eval(expression);

            if (result == (int) result) {
                currentInput = String.valueOf((int) result);
            } else {
                currentInput = String.valueOf(result);
            }

            lastNumeric = true;
            lastDot = currentInput.contains(".");
            updateDisplay();
        } catch (Exception e) {
            currentInput = "Error";
            updateDisplay();
            currentInput = "";
        }
    }

    private double eval(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (; ; ) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (; ; ) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                return x;
            }
        }.parse();
    }

    private void updateDisplay() {
        if (currentInput.isEmpty()) {
            resultTv.setText("0");
        } else {
            resultTv.setText(currentInput);
        }
    }
}