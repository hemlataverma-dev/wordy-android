package com.hemlata.wordy.presentation.search

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.hemlata.wordy.R
import com.hemlata.wordy.core.utils.PreferenceManager
import com.hemlata.wordy.core.utils.Resource
import com.hemlata.wordy.data.model.Meaning
import com.hemlata.wordy.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(), TextToSpeech.OnInitListener {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()

    @Inject
    lateinit var preferenceManager: PreferenceManager

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private var currentWord = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tts = TextToSpeech(requireContext(), this)

        setupSearch()
        observeViewModel()

        val word = arguments?.getString("word") ?: ""
        if (word.isNotEmpty()) {
            binding.etSearch.setText(word)
            viewModel.searchWord(word)
        }

        binding.btnDarkMode.setOnClickListener {
            val isDark = preferenceManager.isDarkMode
            preferenceManager.isDarkMode = !isDark
            AppCompatDelegate.setDefaultNightMode(
                if (!isDark) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

        binding.btnSpeak.setOnClickListener {
            speakWord(currentWord)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            ttsReady = result != TextToSpeech.LANG_MISSING_DATA &&
                    result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    private fun speakWord(word: String) {
        if (word.isEmpty()) {
            Toast.makeText(requireContext(), "Search a word first", Toast.LENGTH_SHORT).show()
            return
        }
        if (!ttsReady) {
            Toast.makeText(requireContext(), "Speech not ready", Toast.LENGTH_SHORT).show()
            return
        }
        tts?.speak(word, TextToSpeech.QUEUE_FLUSH, null, "wordy_tts")
        binding.btnSpeak.animate()
            .scaleX(1.2f).scaleY(1.2f).setDuration(150)
            .withEndAction {
                binding.btnSpeak.animate()
                    .scaleX(1f).scaleY(1f).setDuration(150).start()
            }.start()
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener { text ->
            val word = text.toString()
            if (word.isNotEmpty()) viewModel.searchWord(word)
            else viewModel.clearResult()
        }

        binding.btnSearch.setOnClickListener {
            val word = binding.etSearch.text.toString()
            if (word.isNotEmpty()) viewModel.searchWord(word)
            else Toast.makeText(
                requireContext(), "Please enter a word", Toast.LENGTH_SHORT
            ).show()
        }

        binding.btnFavourite.setOnClickListener {
            viewModel.toggleFavourite()
        }
    }

    private fun observeViewModel() {
        viewModel.wordResult.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.shimmerLayout.visibility = View.VISIBLE
                    binding.shimmerLayout.startShimmer()
                    binding.tvError.visibility = View.GONE
                    binding.scrollResult.visibility = View.GONE
                }

                is Resource.Success -> {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                    binding.scrollResult.visibility = View.VISIBLE

                    resource.data?.firstOrNull()?.let { word ->
                        currentWord = word.word
                        binding.tvWord.text = word.word

                        // show best phonetic
                        val phonetic = word.phonetics
                            .firstOrNull { !it.text.isNullOrEmpty() }?.text
                            ?: word.phonetic ?: ""
                        binding.tvPhonetic.text = phonetic

                        // build all meanings dynamically
                        binding.layoutMeanings.removeAllViews()
                        word.meanings.forEach { meaning ->
                            addMeaningSection(meaning)
                        }
                    }
                }

                is Resource.Error -> {
                    binding.shimmerLayout.stopShimmer()
                    binding.shimmerLayout.visibility = View.GONE
                    binding.scrollResult.visibility = View.GONE
                    binding.tvError.visibility = View.VISIBLE
                    binding.tvErrorMessage.text = resource.message
                    currentWord = ""
                }

                null -> {
                    binding.shimmerLayout.visibility = View.GONE
                    binding.tvError.visibility = View.GONE
                    binding.scrollResult.visibility = View.GONE
                    currentWord = ""
                }
            }
        }

        viewModel.isFavourite.observe(viewLifecycleOwner) { isFav ->
            if (isFav) {
                binding.btnFavourite.setImageResource(R.drawable.ic_heart_filled)
                binding.btnFavourite.setColorFilter(
                    ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
                )
            } else {
                binding.btnFavourite.setImageResource(R.drawable.ic_heart)
                binding.btnFavourite.clearColorFilter()
                binding.btnFavourite.imageTintList =
                    ContextCompat.getColorStateList(
                        requireContext(),
                        com.google.android.material.R.color
                            .material_on_surface_emphasis_medium
                    )
            }
        }
    }

    private fun addMeaningSection(meaning: Meaning) {
        val ctx = requireContext()
        val dp = resources.displayMetrics.density

        fun Int.dp() = (this * dp).toInt()

        // part of speech chip
        val posChip = TextView(ctx).apply {
            text = meaning.partOfSpeech
            textSize = 11f
            setTextColor(
                ContextCompat.getColor(ctx,
                    com.google.android.material.R.color.material_on_surface_emphasis_medium)
            )
            setPadding(14.dp(), 5.dp(), 14.dp(), 5.dp())
            background = ContextCompat.getDrawable(ctx, R.drawable.bg_poss_chip)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = 12.dp() }
        }
        binding.layoutMeanings.addView(posChip)

        // definitions label
        addSectionLabel("DEFINITIONS")

        // each definition
        meaning.definitions.forEachIndexed { index, def ->

            // definition text
            val defText = TextView(ctx).apply {
                text = "  ${index + 1}.  ${def.definition}"
                textSize = 15f
                setTextColor(
                    ContextCompat.getColor(ctx,
                        android.R.color.tab_indicator_text)
                )
                // use colorOnSurface via theme
                setTextColor(
                    requireActivity().theme.obtainStyledAttributes(
                        intArrayOf(com.google.android.material.R.attr.colorOnSurface)
                    ).let {
                        val c = it.getColor(0, 0)
                        it.recycle()
                        c
                    }
                )
                setPadding(0, 0, 0, 8.dp())
                setLineSpacing(4f, 1.0f)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }
            binding.layoutMeanings.addView(defText)

            // example if exists
            if (!def.example.isNullOrEmpty()) {
                val exampleCard = CardView(ctx).apply {
                    radius = 10.dp().toFloat()
                    setCardBackgroundColor(
                        requireActivity().theme.obtainStyledAttributes(
                            intArrayOf(com.google.android.material.R.attr.colorSurfaceVariant)
                        ).let {
                            val c = it.getColor(0, 0)
                            it.recycle()
                            c
                        }
                    )
                    cardElevation = 0f
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).also {
                        it.bottomMargin = 12.dp()
                    }
                }

                val exampleText = TextView(ctx).apply {
                    text = "\"${def.example}\""
                    textSize = 13f
                    setTextColor(
                        requireActivity().theme.obtainStyledAttributes(
                            intArrayOf(com.google.android.material.R.attr.colorOnSurfaceVariant)
                        ).let {
                            val c = it.getColor(0, 0)
                            it.recycle()
                            c
                        }
                    )
                    setPadding(14.dp(), 10.dp(), 14.dp(), 10.dp())
                    setTypeface(typeface, android.graphics.Typeface.ITALIC)
                    setLineSpacing(4f, 1.0f)
                }
                exampleCard.addView(exampleText)
                binding.layoutMeanings.addView(exampleCard)
            }
        }

        // synonyms
        if (meaning.synonyms.isNotEmpty()) {
            addSectionLabel("SYNONYMS")
            addChipGroup(meaning.synonyms.take(8), isClickable = true)
        }

        // antonyms
        if (meaning.antonyms.isNotEmpty()) {
            addSectionLabel("ANTONYMS")
            addChipGroup(meaning.antonyms.take(8), isClickable = false)
        }

        // divider between meanings
        val divider = View(ctx).apply {
            setBackgroundColor(
                requireActivity().theme.obtainStyledAttributes(
                    intArrayOf(com.google.android.material.R.attr.colorOutline)
                ).let {
                    val c = it.getColor(0, 0)
                    it.recycle()
                    c
                }
            )
            alpha = 0.2f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 1
            ).also {
                it.topMargin = 16.dp()
                it.bottomMargin = 16.dp()
            }
        }
        binding.layoutMeanings.addView(divider)
    }

    private fun addSectionLabel(text: String) {
        val dp = resources.displayMetrics.density
        val label = TextView(requireContext()).apply {
            this.text = text
            textSize = 10f
            letterSpacing = 0.1f
            setTypeface(typeface, android.graphics.Typeface.BOLD)
            setTextColor(
                requireActivity().theme.obtainStyledAttributes(
                    intArrayOf(com.google.android.material.R.attr.colorOnSurfaceVariant)
                ).let {
                    val c = it.getColor(0, 0)
                    it.recycle()
                    c
                }
            )
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also {
                it.bottomMargin = (8 * dp).toInt()
                it.topMargin = (4 * dp).toInt()
            }
        }
        binding.layoutMeanings.addView(label)
    }

    private fun addChipGroup(items: List<String>, isClickable: Boolean) {
        val dp = resources.displayMetrics.density
        val chipGroup = ChipGroup(requireContext()).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.bottomMargin = (12 * dp).toInt() }
        }
        items.forEach { item ->
            val chip = Chip(requireContext()).apply {
                text = item
                this.isClickable = isClickable
                this.isCheckable = false
                if (isClickable) {
                    setOnClickListener {
                        binding.etSearch.setText(item)
                        viewModel.searchWord(item)
                    }
                }
            }
            chipGroup.addView(chip)
        }
        binding.layoutMeanings.addView(chipGroup)
    }

    override fun onDestroyView() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        super.onDestroyView()
        _binding = null
    }
}