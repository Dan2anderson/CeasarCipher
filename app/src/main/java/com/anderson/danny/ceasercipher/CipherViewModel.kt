package com.anderson.danny.ceasercipher

import androidx.lifecycle.ViewModel

enum class UiState {
    EDIT, ENCODE, DECODE
}

class CipherViewModel : ViewModel() {
    private var userText: String = ""
    lateinit var mode: UiState

    private val letterListLower = listOf(
        'a',
        'b',
        'c',
        'd',
        'e',
        'f',
        'g',
        'h',
        'i',
        'j',
        'k',
        'l',
        'm',
        'n',
        'o',
        'p',
        'q',
        'r',
        's',
        't',
        'u',
        'v',
        'w',
        'x',
        'y',
        'z'
    )
    private val letterListUpper = listOf(
        'A',
        'B',
        'C',
        'D',
        'E',
        'F',
        'G',
        'H',
        'I',
        'J',
        'K',
        'L',
        'M',
        'N',
        'O',
        'P',
        'Q',
        'R',
        'S',
        'T',
        'U',
        'V',
        'W',
        'X',
        'Y',
        'Z'
    )
    private var cipherMapLower: HashMap<Char, Char> = hashMapOf()
    private var reverseCipherMapLower: HashMap<Char, Char> = hashMapOf()
    private var cipherMapUpper: HashMap<Char, Char> = hashMapOf()
    private var reverseCipherMapUpper: HashMap<Char, Char> = hashMapOf()

    fun setText(text: String) {
        if (mode == UiState.EDIT) {
            userText = text
        }
    }

    fun setOffset(character: Char) {
        makeMap(character.toLowerCase(), cipherMapLower, letterListLower)
        reverseCipherMapLower = reverseMap(cipherMapLower)
        makeMap(character.toUpperCase(), cipherMapUpper, letterListUpper)
        reverseCipherMapUpper = reverseMap(cipherMapUpper)
    }

    fun getModeText(): String {
        return when (mode) {
            UiState.EDIT -> userText
            UiState.ENCODE -> encode(userText)
            UiState.DECODE -> decode(userText)
        }
    }

    fun getUserText(): String {
        return userText
    }


    private fun encode(text: String): String {
        val sb = StringBuilder()
        text.forEach {
            if (it.isLetter()) {
                if (it.isLowerCase()) {
                    sb.append(cipherMapLower[it])
                } else {
                    sb.append(cipherMapUpper[it])
                }
            } else {
                sb.append(it)
            }
        }
        return sb.toString()
    }

    private fun decode(text: String): String {
        val sb = StringBuilder()
        text.forEach {
            if (it.isLetter()) {
                if (it.isLowerCase()) {
                    sb.append(reverseCipherMapLower[it])
                } else {
                    sb.append(reverseCipherMapUpper[it])
                }
            } else {
                sb.append(it)
            }
        }
        return sb.toString()
    }
}

private fun makeMap(character: Char, cipherMap: HashMap<Char, Char>, letterList: List<Char>) {
    var nextOffsetChar = character
    cipherMap.clear()
    var offsetIndex: Int
    for (letterKey in letterList) {
        cipherMap[letterKey] = nextOffsetChar
        offsetIndex = letterList.indexOf(nextOffsetChar)
        if (offsetIndex == letterList.size - 1) {
            offsetIndex = -1
        }
        nextOffsetChar = letterList[offsetIndex + 1]
    }
//    return cipherMap
}

private fun reverseMap(map: HashMap<Char, Char>): HashMap<Char, Char> {
    return hashMapOf<Char, Char>().also {
        map.forEach { (key, value) -> it[value] = key }
    }
}