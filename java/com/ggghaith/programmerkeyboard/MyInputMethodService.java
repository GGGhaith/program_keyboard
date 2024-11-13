package com.ggghaith.programmerkeyboard;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputConnection;

import java.util.HashMap;
//keyboard activity
public class MyInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private long startTime;
    private HashMap<String, String> morseToEnglishMap = new HashMap<>();
    private String morseText = "";

    @Override
    public View onCreateInputView() {
        //creates a keyboard from the drawables
        KeyboardView keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        Keyboard keyboard = new Keyboard(this, R.xml.number_pad);
        //sets the keyboard
        keyboardView.setKeyboard(keyboard);
        //connects the keyboard
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }
//on start up
    @Override
    public void onCreate() {
        super.onCreate();
        loadMorseCodeData();
    }

    // Initialize the Morse Code translator
    //todo: make it a json file
    private void loadMorseCodeData() {
        morseToEnglishMap.put(".-", "a");
        morseToEnglishMap.put("-...", "b");
        morseToEnglishMap.put("-.-.", "c");
        morseToEnglishMap.put("-..", "d");
        morseToEnglishMap.put(".", "e");
        morseToEnglishMap.put("..-.", "f");
        morseToEnglishMap.put("--.", "g");
        morseToEnglishMap.put("....", "h");
        morseToEnglishMap.put("..", "i");
        morseToEnglishMap.put(".---", "j");
        morseToEnglishMap.put("-.-", "k");
        morseToEnglishMap.put(".-..", "l");
        morseToEnglishMap.put("--", "m");
        morseToEnglishMap.put("-.", "n");
        morseToEnglishMap.put("---", "o");
        morseToEnglishMap.put(".--.", "p");
        morseToEnglishMap.put("--.-", "q");
        morseToEnglishMap.put(".-.", "r");
        morseToEnglishMap.put("...", "s");
        morseToEnglishMap.put("-", "t");
        morseToEnglishMap.put("..-", "u");
        morseToEnglishMap.put("...-", "v");
        morseToEnglishMap.put(".--", "w");
        morseToEnglishMap.put("-..-", "x");
        morseToEnglishMap.put("-.--", "y");
        morseToEnglishMap.put("--..", "z");
        morseToEnglishMap.put("-----", "0");
        morseToEnglishMap.put(".----", "1");
        morseToEnglishMap.put("..---", "2");
        morseToEnglishMap.put("...--", "3");
        morseToEnglishMap.put("....-", "4");
        morseToEnglishMap.put(".....", "5");
        morseToEnglishMap.put("-....", "6");
        morseToEnglishMap.put("--...", "7");
        morseToEnglishMap.put("---..", "8");
        morseToEnglishMap.put("----.", "9");
        morseToEnglishMap.put(".-.-.-", ".");
        morseToEnglishMap.put("--..--", ",");
        morseToEnglishMap.put("..--..", "?");
        morseToEnglishMap.put(".----.", "'");
        morseToEnglishMap.put("-.-.--", "!");
        morseToEnglishMap.put("-..-.", "/");
        morseToEnglishMap.put("-.--.", "(");
        morseToEnglishMap.put("-.--.-", ")");
        morseToEnglishMap.put(".-...", "&");
        morseToEnglishMap.put(":---:", ":");
        morseToEnglishMap.put("..--.-", "_");
        morseToEnglishMap.put(".-.-.", "+");
        morseToEnglishMap.put("-....-", "-");
        morseToEnglishMap.put("..--.-", "_");
        morseToEnglishMap.put(".-.-.-", ".");
        morseToEnglishMap.put("---...", ";");
        morseToEnglishMap.put("-.-.-.", ";");
    }
    //on key button down
    @Override
    public void onPress(int primaryCode) {
        Log.d(TAG, "onPress: Key pressed");
        startTime = System.currentTimeMillis();  // gets the start time of when the key is just touched
    }
//on key button up
    @Override
    public void onRelease(int primaryCode) {
        // calculates click duration
        long duration = System.currentTimeMillis() - startTime;
        //gets a connection with the text field we will write on
        InputConnection inputConnection = getCurrentInputConnection();
        //if there is a text field
        if (inputConnection != null) {
            // checks which key was pressed(view number_pad.xml)
            if (primaryCode == -1) {
                //append '.' for short press and '-' for long press
                if (duration < 150) {
                    // add . to the sequence
                    morseText += ".";
                    //sends the . character
                    inputConnection.commitText(".", 1);
                } else if (duration<600) {
                    // adds - to the sequence
                    morseText += "-";
                    //sends the - character
                    inputConnection.commitText("-", 1);
                } else {
                    // clears the sequence
                    morseText = "";
                    // sends the space character
                    inputConnection.commitText(" ", 1);
                }
                //logs to logCat
                Log.d(TAG, "Current Morse Sequence: " + morseText); // Log the Morse sequence
            } else if (primaryCode == -2) {
                //tests the other key
                if (morseText!=""){
                    // if there was a sequence it translate the sequence
                    translateMorseToEnglish(inputConnection);
                }else {
                    //if there was no sequence it deletes characters
                    //gets selectedText
                    CharSequence selectedText = inputConnection.getSelectedText(0);
                    //if no text is selected
                    if (TextUtils.isEmpty(selectedText)) {
                        // remove the character before it
                        inputConnection.deleteSurroundingText(1, 0);
                    } else {
                        //if text is selected deletes it
                        inputConnection.commitText("", 1);
                    }
                }
            }
        }
    }

    // method to convert Morse to English
    private void translateMorseToEnglish(InputConnection inputConnection) {
        String morseSequence = morseText.trim(); //trims any extra spaces that might be generated in some cases
        Log.d(TAG, "Translating Morse Code: " + morseSequence);//logs to logcat

        StringBuilder translatedText = new StringBuilder();
        String[] morseChars = morseSequence.split(" ");  //split by space for individual Morse characters

        for (String morseChar : morseChars) {
            //gets the translation map to the morseChars
            String englishChar = morseToEnglishMap.get(morseChar);
            if (englishChar != null) {
                //if found append it
                translatedText.append(englishChar);
            } else {
                //if not found log
                Log.d(TAG, "No translation found for Morse character: " + morseChar);
            }
        }

        if (translatedText.length() > 0) {
            // removes the morse sequence
            inputConnection.deleteSurroundingText(morseSequence.length(), 0);
            // places the translation
            inputConnection.commitText(translatedText.toString(), 1);
            //logs
            Log.d(TAG, "Translated Morse to: " + translatedText.toString());
        } else {
            //more error detection code
            Log.d(TAG, "No translation for Morse sequence.");
        }

        // Clear the Morse code sequence after translation
        morseText = "";
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        // No action needed in onKey as it's handled in onRelease.
    }

    @Override
    public void onText(CharSequence charSequence) {}

    @Override
    public void swipeLeft() {}

    @Override
    public void swipeRight() {}

    @Override
    public void swipeDown() {}

    @Override
    public void swipeUp() {}
}
