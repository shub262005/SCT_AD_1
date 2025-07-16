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

        // Initialize buttons
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

        // Set click listeners
        View[] buttons = {buttonC, buttonDivide, buttonMultiply, buttonMinus, buttonPlus,
                button7, button8, button9, button4, button5, button6,
                button1, button2, button3, buttonDot, button0, buttonEquals};

        for (View button : buttons) {
            button.setOnClickListener(this);
        }
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
        if (currentInput.isEmpty()) return;

        if (!lastNumeric && !currentInput.isEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length() - 1);
        }

        currentInput += operator;
        lastNumeric = false;
        lastDot = false;
    }

    private void handleDecimalPoint() {
        if (lastDot || !lastNumeric) return;

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
    }

    private void calculateResult() {
        if (currentInput.isEmpty() || !lastNumeric) return;

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
        } catch (Exception e) {
            currentInput = "Error";
            updateDisplay();
            currentInput = "";
        }
    }

    private double eval(final String str) {
        return new EvalParser(str).parse();
    }

    private static class EvalParser {
        private final String str;
        private int pos = -1;
        private int ch;

        public EvalParser(String str) {
            this.str = str;
        }

        private void nextChar() {
            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
        }

        private boolean eat(int charToEat) {
            while (ch == ' ') nextChar();
            if (ch == charToEat) {
                nextChar();
                return true;
            }
            return false;
        }

        public double parse() {
            nextChar();
            double x = parseTerm();
            while (true) {
                if (eat('+')) x += parseTerm();
                else if (eat('-')) x -= parseTerm();
                else return x;
            }
        }

        private double parseTerm() {
            double x = parseFactor();
            while (true) {
                if (eat('*')) x *= parseFactor();
                else if (eat('/')) x /= parseFactor();
                else return x;
            }
        }

        private double parseFactor() {
            if (eat('+')) return parseFactor();
            if (eat('-')) return -parseFactor();

            double x;
            int startPos = this.pos;
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = Double.parseDouble(str.substring(startPos, this.pos));
            return x;
        }
    }

    private void updateDisplay() {
        resultTv.setText(currentInput.isEmpty() ? "0" : currentInput);
    }
}