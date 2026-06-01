package com.zouhu.unscramble.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.zouhu.unscramble.data.MAX_NO_OF_WORDS
import com.zouhu.unscramble.data.SCORE_INCREASE
import com.zouhu.unscramble.data.allWords
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    // Game UI state
    private val _uiState = MutableStateFlow(GameUiState())  // 创建一个可变状态流，初始值为 GameUiState()
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()    // 将可变状态流转换为只读状态流，并返回给外部使用

    var userGuess by mutableStateOf("")
        private set

    private lateinit var currentWord: String    // 保存当前的乱序词
    private var usedWords: MutableSet<String> = mutableSetOf()  // 存储游戏中用过的单词

    // 重置游戏
    init {
        resetGame()
    }

    /**
     * 从列表中随机选择一个单词并打乱单词的字母顺序
     */
    private fun pickRandomWordAndShuffle(): String {
        // Continue picking up a new random word until you get one that hasn't been used before
        currentWord = allWords.random()
        if (usedWords.contains(currentWord)) {
            return pickRandomWordAndShuffle()
        } else {
            usedWords.add(currentWord)
            return shuffleCurrentWord(currentWord)
        }
    }

    /**
     * 将单词的每个字母都随机打乱
     */
    private fun shuffleCurrentWord(word: String): String {
        val tempWord = word.toCharArray()
        // Scramble the word
        tempWord.shuffle()
        while (String(tempWord).equals(word)) {
            tempWord.shuffle()
        }
        return String(tempWord)
    }

    /**
     * 启动和重置游戏
     */
    fun resetGame() {
        usedWords.clear()
        _uiState.value = GameUiState(currentScrambledWord = pickRandomWordAndShuffle())
    }

    /**
     * 接受用户猜出的单词,更新userGuess
     */
    fun updateUserGuess(guessedWord: String){
        userGuess = guessedWord
    }

    /**
     * 检查用户猜测单词是否正确
     */
    fun checkUserGuess() {
        if (userGuess.equals(currentWord, ignoreCase = true)) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)
            updateGameState(updatedScore)
        } else {
            _uiState.update { currentState ->
                currentState.copy(isGuessedWordWrong = true)
            }
        }

        // Reset user guess
        updateUserGuess("")
    }

    /**
     * 继续下一轮：更新得分，增加当前单词数, 获取下一个单词
     */
    private fun updateGameState(updatedScore: Int) {
        if (usedWords.size == MAX_NO_OF_WORDS){
            //Last round in the game
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else{
            // Normal round in the game
            _uiState.update { currentState ->
                currentState.copy(
                    isGuessedWordWrong = false,
                    currentScrambledWord = pickRandomWordAndShuffle(),
                    currentWordCount = currentState.currentWordCount.inc(),
                    score = updatedScore
                )
            }
        }
    }

    /**
     * 当用户跳过某个单词时，您需要更新游戏变量并为下一轮游戏做好准备
     */
    fun skipWord() {
        updateGameState(_uiState.value.score)
        // Reset user guess
        updateUserGuess("")
    }
}