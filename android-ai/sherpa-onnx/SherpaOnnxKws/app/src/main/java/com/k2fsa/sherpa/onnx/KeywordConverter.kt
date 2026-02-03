package com.k2fsa.sherpa.onnx

import net.sourceforge.pinyin4j.PinyinHelper
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat
import java.util.regex.Pattern

/**
 * 关键词格式转换工具类
 * 将普通关键词转换为 sherpa-onnx 所需的格式：
 * - 英文：L AY1 T AH1 P @LIGHT_UP
 * - 中文：zh ōu w àng j ūn @周望军
 */
object KeywordConverter {
    
    // CMU 音素字典（常用单词的音素映射）
    // 注意：这是一个简化版本，实际应用中建议使用完整的CMU Pronouncing Dictionary
    // 或集成在线音素转换API（如 espeak-ng、Festival 等）
    private val cmuPhonemeDict = mapOf(
        "LIGHT" to listOf("L", "AY1", "T"),
        "UP" to listOf("AH1", "P"),
        "LOVELY" to listOf("L", "AH1", "V", "L", "IY0"),
        "CHILD" to listOf("CH", "AY1", "L", "D"),
        "HELLO" to listOf("HH", "AH0", "L", "OW1"),
        "WORLD" to listOf("W", "ER1", "L", "D"),
        "HI" to listOf("HH", "AY1"),
        "GOOGLE" to listOf("G", "UW1", "G", "AH0", "L"),
        "HEY" to listOf("HH", "EY1"),
        "SIRI" to listOf("S", "IH1", "R", "IY0"),
        "OK" to listOf("OW1", "K", "EY1"),
        "YES" to listOf("Y", "EH1", "S"),
        "NO" to listOf("N", "OW1"),
        "STOP" to listOf("S", "T", "AA1", "P"),
        "START" to listOf("S", "T", "AA1", "R", "T"),
        "CANCEL" to listOf("K", "AE1", "N", "S", "AH0", "L"),
        "NEXT" to listOf("N", "EH1", "K", "S", "T"),
        "PREVIOUS" to listOf("P", "R", "IY1", "V", "IY0", "AH0", "S"),
        "PLAY" to listOf("P", "L", "EY1"),
        "PAUSE" to listOf("P", "AO1", "Z"),
    )
    
    /**
     * 将关键词转换为所需格式
     * @param keyword 原始关键词（如 "LIGHT_UP" 或 "周望军"）
     * @return 转换后的格式（如 "L AY1 T AH1 P @LIGHT_UP" 或 "zh ōu w àng j ūn @周望军"）
     */
    fun convert(keyword: String): String {
        if (keyword.isBlank()) {
            return keyword
        }
        
        // 判断是中文还是英文
        return if (containsChinese(keyword)) {
            convertChinese(keyword)
        } else {
            convertEnglish(keyword)
        }
    }
    
    /**
     * 批量转换关键词（每行一个）
     * @param keywordsText 多行关键词文本
     * @return 转换后的多行文本
     */
    fun convertMultiple(keywordsText: String): String {
        return keywordsText.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString("\n") { convert(it) }
    }
    
    /**
     * 判断字符串是否包含中文字符
     */
    private fun containsChinese(text: String): Boolean {
        val pattern = Pattern.compile("[\\u4e00-\\u9fa5]")
        return pattern.matcher(text).find()
    }
    
    /**
     * 转换中文关键词
     * 例如：周望军 -> zh ōu w àng j ūn @周望军
     */
    private fun convertChinese(keyword: String): String {
        val format = HanyuPinyinOutputFormat().apply {
            caseType = HanyuPinyinCaseType.LOWERCASE
            toneType = HanyuPinyinToneType.WITH_TONE_MARK
            vCharType = HanyuPinyinVCharType.WITH_U_AND_COLON
        }
        
        val pinyinList = mutableListOf<String>()
        
        for (char in keyword) {
            if (char.toString().matches(Regex("[\\u4e00-\\u9fa5]"))) {
                // 中文字符，转换为拼音
                val pinyinArray = PinyinHelper.toHanyuPinyinStringArray(char, format)
                if (pinyinArray != null && pinyinArray.isNotEmpty()) {
                    // 取第一个拼音（多音字取第一个）
                    val pinyin = pinyinArray[0]
                    // 将拼音拆分为音节，每个音节用空格分隔
                    // 例如：zhōu -> zh ōu
                    val syllables = splitPinyinIntoSyllables(pinyin)
                    pinyinList.addAll(syllables)
                }
            } else {
                // 非中文字符，保持原样（如标点、数字、英文等）
                pinyinList.add(char.toString())
            }
        }
        
        val pinyinString = pinyinList.joinToString(" ")
        return "$pinyinString @$keyword"
    }
    
    /**
     * 将拼音拆分为音节
     * 例如：zhōu -> ["zh", "ōu"]
     *      wàng -> ["w", "àng"]
     *      nǐ -> ["n", "ǐ"]
     */
    private fun splitPinyinIntoSyllables(pinyin: String): List<String> {
        val syllables = mutableListOf<String>()
        
        // 定义所有可能的声母
        val initials = listOf("zh", "ch", "sh", "b", "p", "m", "f", "d", "t", "n", "l", 
                             "g", "k", "h", "j", "q", "x", "z", "c", "s", "r", "y", "w")
        
        var remaining = pinyin
        
        // 尝试匹配双字母声母（zh, ch, sh）
        for (initial in initials) {
            if (remaining.startsWith(initial, ignoreCase = true)) {
                syllables.add(initial.lowercase())
                remaining = remaining.substring(initial.length)
                break
            }
        }
        
        // 如果没有匹配到双字母声母，尝试单字母声母
        if (remaining == pinyin && remaining.isNotEmpty() && remaining[0].isLetter()) {
            val firstChar = remaining[0]
            // 检查是否是声母
            if (firstChar.lowercaseChar() in "bpmfdtnlgkhjqxzcsryw") {
                syllables.add(firstChar.lowercase().toString())
                remaining = remaining.substring(1)
            }
        }
        
        // 剩余部分作为韵母
        if (remaining.isNotEmpty()) {
            syllables.add(remaining)
        } else if (syllables.isEmpty()) {
            // 如果没有拆分成功，返回原拼音
            syllables.add(pinyin)
        }
        
        return syllables
    }
    
    /**
     * 判断字符是否为元音
     */
    private fun isVowel(char: Char): Boolean {
        return char.lowercaseChar() in "aeiou"
    }
    
    /**
     * 判断字符是否包含声调标记
     */
    private fun hasToneMark(char: Char): Boolean {
        return char in "āáǎàēéěèīíǐìōóǒòūúǔùǖǘǚǜ"
    }
    
    /**
     * 转换英文关键词
     * 例如：LIGHT_UP -> L AY1 T AH1 P @LIGHT_UP
     */
    private fun convertEnglish(keyword: String): String {
        // 处理下划线分隔的复合词
        val words = keyword.split("_", "-", " ").filter { it.isNotBlank() }
        val phonemes = mutableListOf<String>()
        
        for (word in words) {
            val wordPhonemes = getPhonemesForWord(word.uppercase())
            phonemes.addAll(wordPhonemes)
        }
        
        val phonemeString = phonemes.joinToString(" ")
        return "$phonemeString @$keyword"
    }
    
    /**
     * 获取单词的音素序列
     * 优先从字典查找，如果找不到则使用规则映射
     */
    private fun getPhonemesForWord(word: String): List<String> {
        // 先从字典查找
        cmuPhonemeDict[word]?.let {
            return it
        }
        
        // 如果字典中没有，使用简单的规则映射（字母到音素的近似映射）
        // 注意：这是一个简化版本，实际应用中应该使用完整的CMU字典或在线API
        return approximatePhonemes(word)
    }
    
    /**
     * 使用规则近似生成音素（简化版本）
     * 这是一个基础实现，对于复杂单词可能不够准确
     * 建议在实际使用中集成完整的CMU Pronouncing Dictionary或使用在线API
     * 
     * 注意：此方法生成的音素可能不够准确，仅作为备选方案
     * 对于生产环境，建议：
     * 1. 扩展 cmuPhonemeDict 字典
     * 2. 使用完整的CMU Pronouncing Dictionary文件
     * 3. 集成 espeak-ng 或其他TTS引擎获取音素
     * 4. 使用在线音素转换API
     */
    private fun approximatePhonemes(word: String): List<String> {
        val phonemes = mutableListOf<String>()
        var i = 0
        
        while (i < word.length) {
            val char = word[i].uppercaseChar()
            val nextChar = if (i + 1 < word.length) word[i + 1].uppercaseChar() else null
            val prevChar = if (i > 0) word[i - 1].uppercaseChar() else null
            
            when {
                // 双字母音素组合（优先匹配）
                char == 'C' && nextChar == 'H' -> {
                    phonemes.add("CH")
                    i += 2
                }
                char == 'S' && nextChar == 'H' -> {
                    phonemes.add("SH")
                    i += 2
                }
                char == 'T' && nextChar == 'H' -> {
                    phonemes.add("TH")
                    i += 2
                }
                char == 'N' && nextChar == 'G' -> {
                    phonemes.add("NG")
                    i += 2
                }
                char == 'D' && nextChar == 'H' -> {
                    phonemes.add("DH")
                    i += 2
                }
                char == 'Z' && nextChar == 'H' -> {
                    phonemes.add("ZH")
                    i += 2
                }
                // 单个字母（简化映射，不包含重音标记）
                char.isLetter() -> {
                    // 对于元音，添加一个默认的重音标记（实际应该根据音节判断）
                    when (char) {
                        'A' -> phonemes.add("AH1") // 简化：实际需要根据上下文判断
                        'E' -> phonemes.add("EH1")
                        'I' -> phonemes.add("IH1")
                        'O' -> phonemes.add("OW1")
                        'U' -> phonemes.add("UW1")
                        else -> phonemes.add(char.toString())
                    }
                    i++
                }
                else -> i++
            }
        }
        
        // 如果没有生成任何音素，至少返回字母（作为最后的备选）
        return if (phonemes.isEmpty()) {
            word.map { it.toString() }
        } else {
            phonemes
        }
    }
    
    /**
     * 添加自定义音素映射（用于扩展字典）
     * @param word 单词（大写）
     * @param phonemes 音素列表
     */
    fun addPhonemeMapping(word: String, phonemes: List<String>) {
        // 注意：由于 cmuPhonemeDict 是 private val，此方法需要重构为可变的 Map
        // 这里仅作为接口示例，实际使用时需要将字典改为 mutableMapOf
    }
}

