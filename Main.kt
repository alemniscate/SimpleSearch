package search

import java.awt.image.BufferStrategy
import java.io.File

fun main(args: Array<String>) {
    val filename = args[1]
    val file = File(filename)
    val lines = file.readLines()
    val map = InvertedIndex(lines)

    val act = FindAction()
    var menu = selectMenu()
    while (menu != 0) {
        when (menu) {
            1 -> act.find(map, lines)
            2 -> act.print(lines)
            0 -> act.exit()
        }
        if (menu != 0) {
            menu = selectMenu()
        }
    }
}

class InvertedIndex(val lines: List<String>) {
    val map = mutableMapOf<String, MutableList<Int>>()

    init {
        for (i in lines.indices) {
            val strs = lines[i].toLowerCase().split(" ")
            for (str in strs) {
                if (str == "") continue
                if (map.containsKey(str)) {
                    addValue(str, i)
                } else {
                    createValue(str, i)
                }
/*
                val strs2 = str.split("@")
                for (str2 in strs2) {
                    if (map.containsKey(str2)) {
                        addValue(str2, i)
                    } else {
                        createValue(str2, i)
                    }
                }
                val strs3 = str.split(".")
                for (str3 in strs3) {
                    if (map.containsKey(str3)) {
                        addValue(str3, i)
                    } else {
                        createValue(str3, i)
                    }
                }
*/
            }
        }
    }

    fun addValue(key: String, index: Int) {
        var value = map[key]
        for (i in value!!) {
            if (index == i) return
        }
        value.add(index)
        map[key] = value
    }

    fun createValue(key: String, index: Int) {
        val value = mutableListOf<Int>()
        value.add(index)
        map[key] = value
    }

    fun find(words: String, strategy: String): MutableSet<Int> {
        return when (strategy) {
            "ALL" -> findAll(words)
            "ANY" -> findAny(words)
            "NONE" -> findNone(words)
            else -> mutableSetOf<Int>()
        }
    }

    fun findAll(words: String): MutableSet<Int> {
        var list = mutableSetOf<Int>()
        val keys = words.split(" ")
        for (key in keys) {
            if (map.containsKey(key)) {
                val value = map[key]
                if (list.isEmpty()) {
                    addAll(value, list)
                } else {
                    list = and(value, list)
                }
            }
        }
        return list
    }

    fun addAll(value: MutableList<Int>?, list: MutableSet<Int>) {
        for (i in value!!) {
            list.add(i)
        }
    }

    fun and(value: MutableList<Int>?, list: MutableSet<Int>): MutableSet<Int> {
        val newlist = mutableSetOf<Int>()
        for (i in value!!) {
            for (j in list) {
                if (i == j) {
                    newlist.add(i)
                }
            }
        }
        return newlist
    }

    fun findAny(words: String): MutableSet<Int> {
        val list = mutableSetOf<Int>()
        val keys = words.split(" ")
        for (key in keys) {
            if (map.containsKey(key)) {
                val value = map[key]
                for (i in value!!) {
                    list.add(i)
                }
            }
        }
        return list
    }

    fun findNone(words: String): MutableSet<Int> {
        val list = findAny(words)
        val newlist = mutableSetOf<Int>()
        for (i in lines.indices) {
            if (i !in list) {
                newlist.add(i)
            }
        }
        return newlist
    }
}

fun selectMenu(): Int {
    var menu = 9
    while (true) {
        println()
        println("=== Menu ===")
        println("1. Find a person")
        println("2. Print all people")
        println("0. Exi")
        menu = readLine()!!.toInt()
        println()
        if (menu in 0..2) {
            break
        }
        println("Incorrect option! Try again.")
    }
    return menu
}

class FindAction {
    fun find(map: InvertedIndex, lines: List<String>) {

        println("Select a matching strategy: ALL, ANY, NONE")
        val strategy = readLine()!!

        println("Enter a name or email to search all suitable people.")
        val words = readLine()!!.toLowerCase()

        val indices = map.find(words, strategy)
        if (indices.isEmpty()) {
            println("No matching people found.")
//            findex(word, lines)
        } else {
            println()
            println("${indices.size} persons found:")
            for (i in indices) {
                println(lines[i])
            }
        }
    }

    fun print(lines: List<String>) {
        println("=== List of people ===")
        for (line in lines) {
            println(line)
        }
    }

    fun exit() {
        println("Bye!")
    }
}

fun findex(word: String, lines: List<String>) {
    val founds = mutableListOf<String>()
    loop@ for (line in lines) {
        val strs = line.split(" ")
        for (str in strs) {
            val str1 = str.toLowerCase()
            if (str1 == word) {
                founds.add(line)
                continue@loop
            }
            if (str1.length > word.length) {
                for (i in str1.indices) {
                    if (i + word.length > str1.length) break
                    if (str1.substring(i, i + word.length) == word) {
                        founds.add(line)
                        continue@loop
                    }
                }
            }
        }
    }
    if (founds.isEmpty()) {
        println("No matching people found.")
    } else {
        println()
        println("Found people:")
        for (found in founds) {
            println(found)
        }
    }
}
