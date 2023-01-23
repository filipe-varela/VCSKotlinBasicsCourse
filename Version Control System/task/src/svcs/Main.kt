package svcs

import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

const val PROGRAM_DESCRIPTION = """
These are SVCS commands:
config      Get and set a username.
add         Add a file to the index.
log         Show commit logs.
commit      Save changes.
checkout    Restore a file.
"""

const val HELP_OPTION = "--help"

fun setupVersionControl(): Map<String, File> {
    val vcsDir = File(".${File.separator}vcs")
    if (!vcsDir.exists())  vcsDir.mkdir()

    val indexFile: File = vcsDir.resolve("index.txt")
    if (!indexFile.exists()) indexFile.createNewFile()

    val configFile: File = vcsDir.resolve("config.txt")
    if (!configFile.exists()) configFile.createNewFile()

    val commitsDir: File = vcsDir.resolve("commits")
    if (!commitsDir.exists()) commitsDir.mkdir()

    val logFile: File = vcsDir.resolve("log.txt")
    if (!logFile.exists()) logFile.createNewFile()

    return mapOf(
        "vcs" to vcsDir,
        "index" to indexFile,
        "config" to configFile,
        "commitDir" to commitsDir,
        "log" to logFile
    )
}

fun main(args: Array<String>) {
    val vcsStructure: Map<String, File> = setupVersionControl()
    if (args.isEmpty()) {
        println(PROGRAM_DESCRIPTION)
        return
    }
    val commandInput = args[0]
    when (commandInput.split("> ")[0]) {
        "", "--help" -> println(PROGRAM_DESCRIPTION)
        "config" -> configChange(args, vcsStructure["config"]!!)
        "add" -> addTrack(args, vcsStructure["index"]!!)
        "log" -> logCommits(vcsStructure["log"]!!)
        "commit" -> commitChanges(args, vcsStructure)
        "checkout" -> checkoutToID(args,vcsStructure)
        else -> println("'${commandInput.split("> ")[0]}' is not a SVCS command.")
    }
}

fun checkoutToID(
    args: Array<String>,
    vcsStructure: Map<String, File>
) {
    if (args.size < 2) println("Commit id was not passed.")
    else {
        val checkoutDir = vcsStructure["commitDir"]!!.resolve(args[1])
        if (!checkoutDir.exists()) println("Commit does not exist.")
        else {
            checkoutDir.listFiles()!!.forEach {
                it.copyTo(it.parentFile.parentFile.parentFile.parentFile.resolve(it.name), overwrite = true)
            }
            println("Switched to commit ${args[1]}.")
        }
    }
}

fun commitChanges(
    args: Array<String>,
    vcsStructure: Map<String, File>
) {
    if (args.size < 2) println("Message was not passed.")
    else if (vcsStructure["index"]!!.readText().isEmpty()) println("Nothing to commit.")
    else {
        val name = vcsStructure["config"]!!.readText().ifEmpty { "name" }
        val commitMessage: String = args[1]
        val content = name + "_" + getTrackedFilesContent(vcsStructure)
        val commitDirName = hashCommit(content.replace(' ', '_')).toString(16).apply {
            if (this.length > 40) substring(0,41)
        }
        val newCommitDir = vcsStructure["commitDir"]!!.resolve(commitDirName)
        if (newCommitDir.exists()) println("Nothing to commit.")
        else {
            newCommitDir.mkdir()
            vcsStructure["index"]!!.readText().split('\n').forEach {
                if (it.isNotEmpty()) File(it).copyTo(newCommitDir.resolve(it))
            }
            vcsStructure["log"]!!.writeText(
                "commit $commitDirName\nAuthor: $name\n$commitMessage\n\n" +
                        vcsStructure["log"]!!.readText()
            )
            println("Changes are committed.")
        }
    }
}

private fun getTrackedFilesContent(vcsStructure: Map<String, File>): String {
    val filesTracked = vcsStructure["index"]!!.readText()
    var filesTrackedContent = "content_"
    if (filesTracked.isNotEmpty()) {
        filesTracked.split('\n').forEach {
            if (it.isNotEmpty()) {
                val currentFile = File(it)
                val lastModifiedDate = SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(
                    Date(currentFile.lastModified())
                )
                filesTrackedContent += lastModifiedDate + currentFile.readText()
            }
        }
    }
    return filesTrackedContent
}

fun hashCommit(commitValue: String, p: Int = 5): Int =
    if (commitValue.length == 1) {
        commitValue.first().code
    } else {
        commitValue.last().code + hashCommit(commitValue.substring(0, commitValue.length-1), p)*p
    }

//https://www.javacodemonk.com/md5-and-sha256-in-java-kotlin-and-android-96ed9628
private fun hashString(type: String, input: String): String {
    val bytes = MessageDigest
        .getInstance(type)
        .digest(input.toByteArray())
    //https://www.baeldung.com/kotlin/byte-arrays-to-hex-strings
    return bytes.joinToString { "%02x".format(it) }.uppercase()
}

fun logCommits(
    logFile: File
) {
    if (logFile.readText().isEmpty()) println("No commits yet.")
    else {
        println(logFile.readText())
    }
}

private fun configChange(
    args: Array<String>,
    configFile: File
) {
    if (args.size < 2) {
        val configContent = configFile.readText()
        println(
            if (configContent.isEmpty()) "Please, tell me who you are."
            else "The username is $configContent."
        )
    } else {
        configFile.writeText(args[1])
        println("The username is ${args[1]}.")
    }
}

fun addTrack(
    args: Array<String>,
    indexFile: File
) {
    if (args.size < 2) {
        if (indexFile.readText().isEmpty())
            println("Add a file to the index.")
        else
            println("Tracked files:\n${indexFile.readText()}")
    } else {
        val indexText = indexFile.readText()
        val fileToTrack = File(args[1])
        if (fileToTrack.exists() && fileToTrack.isFile) {
            if (!indexText.contains(fileToTrack.name)) {
                indexFile.appendText(fileToTrack.name + "\n")
                println("The file '${fileToTrack.name}' is tracked.")
            }
        } else println("Can't find '${args[1]}'.")
    }
}

