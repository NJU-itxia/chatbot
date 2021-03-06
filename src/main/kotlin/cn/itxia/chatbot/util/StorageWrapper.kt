package cn.itxia.chatbot.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import java.io.File

class StorageWrapper<T>(fileName: String, reference: TypeReference<MutableList<T>>) {

    companion object {
        private val mapper = ObjectMapper().registerModule(KotlinModule())
    }

    private val list: MutableList<T>

    private var file: File = StorageUtil.implementJsonFile(fileName)

    private val logger = getLogger()

    init {
        //read values from json file
        if (!file.exists()) {
            logger.info("文件${fileName}不存在,创建新文件.")
            file.createNewFile()
            file.writeText("[]")
        }
        list = mapper.readValue(file, reference)
    }

    private fun saveToJsonFile() {
        mapper.writeValue(file, list)
    }

    fun get(predict: (T) -> Boolean): T? {
        for (t in list) {
            if (predict(t)) {
                return t
            }
        }
        return null
    }

    fun getAll(predict: (T) -> Boolean): List<T> {
        return list.filter { predict(it) }
    }

    fun add(vararg listOfT: T) {
        listOfT.forEach { list.add(it) }
        saveToJsonFile()
    }

    fun remove(predict: (T) -> Boolean): Boolean {
        return list.removeIf(predict).also {
            saveToJsonFile()
        }
    }

}
