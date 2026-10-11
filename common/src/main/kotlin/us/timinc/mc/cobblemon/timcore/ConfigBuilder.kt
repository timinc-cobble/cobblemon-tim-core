package us.timinc.mc.cobblemon.timcore

import com.google.gson.GsonBuilder
import java.io.File
import java.io.FileReader
import java.io.PrintWriter

class ConfigBuilder<T> private constructor(private val clazz: Class<T>, private val path: String) {
    companion object {
        fun <T> load(clazz: Class<T>, path: String): T {
            return ConfigBuilder(clazz, path)._load()
        }
    }

    fun _load(): T {
        val gson = GsonBuilder()
            .disableHtmlEscaping()
            .setPrettyPrinting()
            .create()

        var config = gson.fromJson("{}" /*default value*/, clazz)
        val configFile = File("config/$path.json")
        configFile.parentFile.mkdirs()

        if (configFile.exists()) {
            try {
                FileReader(configFile).use { reader ->
                    config = gson.fromJson(reader, clazz) ?: config
                }
            } catch (e: Exception) {
                println("Error reading config file")
            }
        }

        val pw = PrintWriter(configFile)
        gson.toJson(config, pw)
        pw.close()

        return config
    }
}
