package com.mcmouse88.fragment_lifecycle

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.github.javafaker.Faker
import com.mcmouse88.fragment_lifecycle.databinding.FragmentRandomBinding
import kotlin.properties.Delegates
import kotlin.random.Random

class RandomFragment : Fragment(), HasUUID, NumberListener {

    private var _binding: FragmentRandomBinding? = null
    private val binding: FragmentRandomBinding
        get() = _binding ?: throw NullPointerException("FragmentRandomBinding is null")

    private var backgroundColor by Delegates.notNull<Int>()
    private lateinit var chuckNorrisFact: String

    private val textColor: Int
        get() = if (Color.luminance(backgroundColor) > 0.3) Color.BLACK
        else Color.WHITE

    private var uuidArguments: String
        get() = requireArguments().getString(ARG_UUID)!!
        set(value) = requireArguments().putString(ARG_UUID, value)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRandomBinding.inflate(inflater, container, false)
        setupUI()
        updateUI()

        Log.d(TAG, "$uuidArguments onCreateView")
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        getUUID()
        Log.d(TAG, "$uuidArguments: onAttach")
    }

    /**
     * Инициализацию глобальныъ переменных лучше делать в методе [onCreate()], так как при
     * переходе на следующий экран, и переходе обратно, метод [onCreate()] вызываться не будет,
     * а [onCreateView()] будет, в связи с чем переменные будут переинициализированы, и некоторые
     * данные могут быть утрачены. Также для сохранения состояния раньше использовались retain
     * фрагменты
     * ```css
     * retainInstance = true
     * ```
     * но в данный момент с появлением [ViewModel] стали устаревшими и не используются.
     * [ViewModel] кстати работают на основе этих retain фрагментов
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "$uuidArguments: onCreate")



        backgroundColor = savedInstanceState?.getInt(KEY_BACKGROUND_COLOR) ?: getRandomColor()
        chuckNorrisFact = savedInstanceState?.getString(KEY_NORRIS_FACT) ?: getNextNorrisFact()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "$uuidArguments: onViewCreated")
    }

    /**
     * Вызывается после всех методов создания фрагмента, и перед методом [onStart()], суть этого
     * метода в том, что при его вызове уже гарантированно создано активити, и с ним можно
     * работать, в качестве параметра в него приходит [Bundle], тот же который приходит в
     * [onCreate()], [onCreateView()] и [onViewCreated()]. Кроме того метод [onActivityCreated]
     * по сути дублирует все эти три метода, и делает так, что фрагмент знает о самом активити, и
     * о его жизненном цикле. Из-за этих недостатков он и был объявлен устаревшим [deprecated]
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "$uuidArguments: onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "$uuidArguments: onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "$uuidArguments: onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "$uuidArguments: onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "$uuidArguments: onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "$uuidArguments: onDestroyView")
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
        Log.d(TAG, "$uuidArguments: onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "$uuidArguments: onDestroy")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_BACKGROUND_COLOR, backgroundColor)
        outState.putString(KEY_NORRIS_FACT, chuckNorrisFact)
        Log.d(TAG, "$uuidArguments: onSaveInstanceState")
    }

    override fun getUUID() = uuidArguments

    override fun onNewScreenNumber(number: Int) {
        binding.tvNumber.text = getString(R.string.number, number)
    }

    private fun setupUI() {
        binding.btChangeUuid.setOnClickListener {
            uuidArguments = navigator().generateUUID()
            navigator().update()
            updateUI()
        }

        binding.btChangeBackground.setOnClickListener {
            backgroundColor = getRandomColor()
            updateUI()
        }

        binding.btChangeFact.setOnClickListener {
            chuckNorrisFact = getNextNorrisFact()
            updateUI()
        }

        binding.btLaunchNext.setOnClickListener {
            navigator().launchNext()
        }
    }

    private fun updateUI() {
        binding.apply {
            tvChuckNorrisFact.text = chuckNorrisFact
            tvUuid.text = uuidArguments
            // tvNumber.text =
            tvUuid.setTextColor(textColor)
            binding.root.setBackgroundColor(backgroundColor)
            tvChuckNorrisFact.setTextColor(textColor)
            tvNumber.setTextColor(textColor)
        }
    }

    private fun getRandomColor() = -Random.nextInt(0xFFFFFF)

    private fun getNextNorrisFact() = Faker.instance().chuckNorris().fact()

    companion object {
        private const val ARG_UUID = "arg_uuid"
        private const val KEY_BACKGROUND_COLOR = "key_background_color"
        private const val KEY_NORRIS_FACT = "key_norris_fact"
        private val TAG = RandomFragment::class.java.simpleName

        fun newInstance(uuid: String) = RandomFragment().apply {
            arguments = bundleOf(ARG_UUID to uuid)
        }
    }
}