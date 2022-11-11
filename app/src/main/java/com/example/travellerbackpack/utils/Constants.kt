package com.example.travellerbackpack.utils

import com.example.travellerbackpack.modal.Language

object Constants {
    fun languageOptions(): ArrayList<Language> {
        val list = ArrayList<Language>()
        list.add(Language("ar", "Arabic"))
        list.add(Language("nl", "Dutch"))
        list.add(Language("en", "English"))
        list.add(Language("fr", "French"))
        list.add(Language("ka", "Georgian"))
        list.add(Language("de", "German"))
        list.add(Language("el", "Greek"))
        list.add(Language("hi", "Hindi"))
        list.add(Language("id", "Indonesian"))
        list.add(Language("it", "Italian"))
        list.add(Language("ja", "Japanese"))
        list.add(Language("ko", "Korean"))
        list.add(Language("fa", "Persian"))
        list.add(Language("pt", "Portuguese"))
        list.add(Language("ru", "Russian"))
        list.add(Language("ro", "Romanian"))
        list.add(Language("es", "Spanish"))
        list.add(Language("sv", "Swedish"))
        list.add(Language("tr", "Turkish"))
        list.add(Language("ur", "Urdu"))

        return list
    }
}