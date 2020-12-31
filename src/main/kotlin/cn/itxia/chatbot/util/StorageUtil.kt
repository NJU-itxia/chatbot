package cn.itxia.chatbot.util

import java.io.File

class StorageUtil {

    companion object {

        private const val baseDir = "data/"

        private const val imageDir = baseDir + "img/"

        private const val jsonDir = baseDir + "json/"

        init {
            File(imageDir).mkdirs()
            File(jsonDir).mkdirs()
        }

        private fun implementImagePath(fileName: String) = imageDir + fileName

        fun implementImageFile(fileName: String) = File(implementImagePath(fileName))

        private fun implementJsonPath(fileName: String) = jsonDir + fileName

        fun implementJsonFile(fileName: String) = File(implementJsonPath(fileName))

    }

}
