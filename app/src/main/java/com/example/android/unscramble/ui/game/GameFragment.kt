package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/*
* Por ejemplo, en tu app de Unscramble, la palabra desordenada, la puntuación y el recuento de palabras se muestran en un fragmento (controlador de IU).
* El código de toma de decisiones, como determinar la siguiente palabra desordenada y los cálculos de la puntuación y el recuento de palabras, deben estar en tu ViewModel.
* */

class GameFragment : Fragment() {

    // VARIABLE DE VINCULACÓN DEL DISEÑO
    private lateinit var binding: GameFragmentBinding

    //SE OBTIENE LA REFERENCIA AL VIEWMODEL MEDIANTE DELEGADOS
    //PORQUE ESTOS DELEGADOS PUEDEN SOBREVIVIR AL CICLO DE VIDA
    private val viewModel: GameViewModel by viewModels<GameViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //SE INFLA Y SE HACE MATCH CON VIEWBINDING
        //binding = GameFragmentBinding.inflate(inflater, container, false)

        //INFLADO CON DATABINDING
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)

        //SE RETORNA LA VIEW COMO LO SOLICITA EL mÉTODO
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //SE INICIALIZAN LAS VARIABLES DECLARADAS EN EL DISEÑO XML
        binding.gameViewModel = viewModel
        binding.maxNoOfWords  = MAX_NO_OF_WORDS
        //SE LE DEBE INDICAR AL BINDING, CUAL VA A SER EL DUEÑO DEL CICLO DE VIDA AL CUAL SE DEBE AMARRAR
        binding.lifecycleOwner = viewLifecycleOwner

        //SE CREAN LOS LISTENER A LOS BOTONES DE AVANZAR Y ENVIAR
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }

        //SE ACTUALIZA LA UI, OBSERVANDO LA VARIABLES LIVEDATA DEL VIEWMODEL

        //COMO SE IMPLEMENTO DATABINDING ACA YA NO ES NECESARIO OBSERVAR LOS VALORES
        //DEBIDO A QUE SE ACTUALIZAN DIRECTAMENTE EN EL XML

        /*viewModel.currentScrambledWord.observe(viewLifecycleOwner){ newWord ->
            binding.textViewUnscrambledWord.text = newWord
        }*/

        /*viewModel.score.observe( viewLifecycleOwner ){ newScore ->
            binding.score.text =
                getString(R.string.score, newScore)
        }*/

        /*viewModel.currentWordCount.observe( viewLifecycleOwner ){ newWordCount ->
            binding.wordCount.text =
                getString(R.string.word_count, newWordCount, MAX_NO_OF_WORDS)
        }*/

    }

    //SE RECUPERA LA PALABRA DEL USUARIO, SE VALIDA EN EL VIEWMODEL QUE SEA CORRECTA
    //ADEMAS DE VALIDA EN EL VIEWMODEL SI PUEDE SEGUIR JUGANDO O YA LLEGO AL LIMITE DE PALABRAS
    private fun onSubmitWord() {
        //PALABRA ESCRITA POR EL USUARIO
        val playerWord = binding.textInputEditText.text.toString()

        //SI LA PALABRA ES CORRECTA
        if (viewModel.isUserWordCorrect(playerWord)) {
            //NO SE MUESTRA EL ERROR
            setErrorTextField(false)
            //Se valida si se debe mostrar otra palabra o termino el juego
            if (!viewModel.nextWord()) {
                showFinalScoreDialog()
            }
        } else {
            setErrorTextField(true)
        }

    }

    //SE VALIDA EN EL VIEWMODEL SI SE PUEDE SEGUIR JUGANDO O YA LLEGO AL LIMITE DE PALABRAS
    private fun onSkipWord() {

        //SI PODEMOS SEGUIR JUGANDO, EL VIEWMODEL GENERA LA SIGUIENTE PALABRA
        if (viewModel.nextWord()) {
            setErrorTextField(false)
        } else {
            //De lo contrario mostramos el resultado final
            showFinalScoreDialog()
        }
    }

    //SI LLEGO AL LIMITE DE PALABRAS PARA JUGAR, SE MUESTRA UNA ALERTA CON EL PUNTAJE
    //Y OPCIONES DE SALIR O JUGAR DE NUEVO
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(resources.getString(R.string.congratulations))
            .setMessage(getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.exit)) { _, _ ->
                exitGame()
            }
            .setPositiveButton(getString(R.string.play_again)) { _, _ ->
                restartGame()
            }
            .show()
    }

   //MÉTODO QUE REINICIA LOS VALORES DE SCORE Y CONTADOR DE PALABRAS, USANDO EL VIEWMODEL
    private fun restartGame() {
        viewModel.reinitializeData()
        setErrorTextField(false)
    }

    //MÉTODO QUE FINALIZA LA ACTIVIDAD Y POR ENDE SALIMOS DE LA APP
    private fun exitGame() {
        activity?.finish()
    }

    //SE EVALUA EL BOOLEAN PASADO PARA DETERMINAR SI SE MUESTRA UN ERROR EN EL TEXT INPUT LAYOUT
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

}
