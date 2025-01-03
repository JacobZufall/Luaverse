package io.github.jacobzufall.luaverse.lua

import io.github.jacobzufall.luaverse.Settings
import io.github.jacobzufall.luaverse.utility.VersionString

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

import java.io.File

/**
 * Handles downloading, extracting, and building Lua.
 *
 * Download and extraction is done automatically on object initialization. Building is done by invoking the build()
 * method.
 */
class LuaSourceCode(version: VersionString) {
    var sourceFile: File? = null
    var extractedFiles: File? = null

    init {
        // Downloads the requested version of Lua from https://www.lua.org/ftp/.
        when (version.rawVersion) {
            "latest" -> sourceFile = fetchFileFromFtp(LuaVersionHandler.luaVersionFiles.firstNotNullOf { VersionString(it.key) })
            else -> LuaVersionHandler.luaVersionFiles[version.delimitedVersion]?.let { sourceFile = fetchFileFromFtp(VersionString(it)) } ?: println("Version does not exist.")
        }

        // Extracts the files downloaded in the previous step.
    }

    // TODO: Add handling for multiple downloads.
    private fun fetchFileFromFtp(version: VersionString): File? {
        val client: OkHttpClient = OkHttpClient()
        val request: Request =  Request.Builder().url("https://www.lua.org/ftp/lua-${version.withDelimiter(".")}.tar.gz").build()
        val response: Response = client.newCall(request).execute()

        if (response.isSuccessful) {
            val outputFile: File = File(Settings.directories["download"]!!["dir"].toString(), "lua-${version.rawVersion}.tar.gz")

            response.body?.byteStream().use { input ->
                outputFile.outputStream().use { output ->
                    input?.copyTo(output)
                }
            }

            return outputFile
        }

        return null
    }
}