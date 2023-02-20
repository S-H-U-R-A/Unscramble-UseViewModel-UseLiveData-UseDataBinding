package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {

    //PRIPIEDADES MUTABLES OBSERVABLES PARA ACTUALIZAR LA UI
    private val _score: MutableLiveData<Int> = MutableLiveData(0)
    val score: LiveData<Int>
        get() = _score

    private val _currentWordCount: MutableLiveData<Int> = MutableLiveData(0)
    val currentWordCount: LiveData<Int>
        get() = _currentWordCount

    private val _currentScrambledWord: MutableLiveData<String> = MutableLiveData()
    val currentScrambledWord: LiveData<String>
        get() = _currentScrambledWord

    //LISTA DE PALABRAS USADAS EN CADA JUEGO
    private var wordsList: MutableList<String> = mutableListOf()

    //PALABRA ACTUAL OBTENIDA DEL ARREGLO DE PALABRAS
    private lateinit var currentWord: String

    //INICICALIZAMOS LA PRIMER PALABRA
    init {
        Log.d("GameFragment", "GameViewModel created!")
        //Para que no aparezca por defecto Test la primera vez el mismo viewmodel
        //obtiene la palabra
        getNextWord()
    }

    //OBTENER PROXIMA PALABRA
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
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = _currentWordCount.value?.inc()
            wordsList.add( currentWord )
        }

    }

    //VALIDA SI PODEMOS OBTENER OTRA PALABRA
    fun nextWord(): Boolean {
        //Valida si ya superamos el máximo de palabras permitidas para un juego
        return  if (currentWordCount.value!! < MAX_NO_OF_WORDS){
            getNextWord()
            true
        } else {
            false
        }
    }

    //VERIFICA SI LA PALABRA DIGITADA POR EL USUARIO ES CORRECTA
    fun isUserWordCorrect(playerWord: String) : Boolean {
        return  if ( playerWord.equals(currentWord, true) ){
            increaseScore()
            true
        }else{
            false
        }
    }

    //INCREMENTA LA PUNTUACIÓN
    private fun increaseScore(){
        _score.value = _score.value?.plus( SCORE_INCREASE )
    }

    //REINICIA LOS VALORES
    fun reinitializeData(){
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()

        getNextWord()
    }

    //PRUEBA DE CICLO DE VIDA
    override fun onCleared() {
        super.onCleared()
        Log.d("GameFragment", "GameViewModel destroyed!")
    }

}