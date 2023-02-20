package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    private var _score = 0
    val score: Int
        get() = _score

    private var _currentWordCount = 0
    val currentWordCount: Int
        get() = _currentWordCount

    private lateinit var _currentScrambledWord: String
    val currentScrambledWord: String
        get() = _currentScrambledWord

    //Lista de palabras usadas en el Juego
    private var wordsList: MutableList<String> = mutableListOf()

    //Palabra actual
    private lateinit var currentWord: String

    private fun getNextWord() {

        //Se obtiene la palabra
        currentWord = allWordsList.random()

        //La palabra selecionada se convierte a array de char y se desordena
        val tempWord = currentWord.toCharArray()
        tempWord.shuffle()

        //Valida que la palabra desordenada no sea igual a la palabra original
        while (String(tempWord).equals(currentWord, false)) {
            tempWord.shuffle()
        }

        //Se valida si la palabra obtenida ya se uso en el juego
        if (wordsList.contains(currentWord)) {
            getNextWord()
        } else {
            _currentScrambledWord = String(tempWord)
            ++_currentWordCount
            wordsList.add( currentWord )
        }

    }

    init {
        Log.d("GameFragment", "GameViewModel created!")
        //Para que no aparezca por defecto Test la primera vez el mismo viewmodel
        //obtiene la palabra
        getNextWord()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }

    fun nextWord(): Boolean {
        //Valida si ya superamos el máximo de palabras permitidas para un juego
        return  if (currentWordCount < MAX_NO_OF_WORDS){
            getNextWord()
            true
        } else {
            false
        }
    }

    fun isUserWordCorrect(playerWord: String) : Boolean {
        return  if ( playerWord.equals(currentWord, true) ){
            increaseScore()
            true
        }else{
            false
        }
    }

    private fun increaseScore(){
        _score += SCORE_INCREASE
    }

    fun reinitializeData(){
        _score = 0
        _currentWordCount = 0
        wordsList.clear()

        getNextWord()
    }

}