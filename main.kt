package svcs
import java.io.File

fun config(name: String = "") { // Get and set a username.
    val configFile = File("vcs/config.txt")
    if (name != "" || !configFile.exists()) configFile.writeText(name)
    if (configFile.readText() != "") println("The username is ${configFile.readText()}.")
    else println("Please, tell me who you are.")
}

fun add(name: String = "") { // Add a file to the index.
    val indexFile = File("vcs/index.txt")
    if (!indexFile.exists()) indexFile.writeText("")
    val addFile = File(name)
    if (addFile.exists()) {
        indexFile.appendText("${name}\n")
        println("The file '${name}' is tracked.")
    }
    else if (name == "") {
        if (indexFile.readText() == "") println("Add a file to the index.")
        else {
            val lines = indexFile.readLines()
            println("Tracked files:")
            for (line in lines) {
                println(line)
            }
        }
    }
    else println("Can't find '${name}'.")
}

fun hash(): Int {
    val trackFiles = File("vcs/index.txt").readLines() // get tracked files
    var textFiles = ""
    for (text in trackFiles) {
        textFiles += File(text).readText()  // get the text of all tracked files
    }
    return textFiles.hashCode()
}

fun newHash() {
    val indexFiles = File("vcs/index.txt")
    val hash = hash()
//    if (!File("vcs/commits").exists()) File("vcs/commits").mkdir()
    File("vcs/commits/$hash").mkdir()
    for (file in indexFiles.readLines()) {
        File("vcs/commits/$hash/$file").writeText(File(file).readText()) // read text from orig file and create a new file with this text
    }
}

fun log(name: String = "") {
    val logFile = File("vcs/log.txt")
    if (name != "") {
        val oldText = logFile.readText()
        logFile.writeText(name)
        logFile.appendText("\n" + oldText)
    }
    else if (logFile.readText() == "") println("No commits yet.")
    else println(logFile.readText())
}


fun change(): Boolean {
    val log = File("vcs/log.txt").readLines()
    return if (log.size > 2) {
//        println(log)
//        println(hash())
//        println(log[0].substringAfter(" "))
        val lastHash = log[0].substringAfter(" ")
        !(hash() == lastHash.toInt())
    } else true
}

fun commit(name: String = "") {
    if (name == "") println("Message was not passed.")
    else if (!change()) println("Nothing to commit.")
    else {
        log("commit ${hash()}\nAuthor: ${File("vcs/config.txt").readText()}\n${name}")
        newHash()
        println("Changes are committed.")
    }

}

fun restoreFile(hash: String) {
    val indexFiles = File("vcs/index.txt")
    for (file in indexFiles.readLines()) {
        File("vcs/commits/$hash/$file").copyTo(File(file), overwrite = true) // read text from old file and overwrite a tracked file
    }
}

fun checkout(name: String = "") { // restore a file
    val log = File("vcs/log.txt").readLines()
    if (name == "") println("Commit id was not passed.")
    else if (name != "") {
        for (hash in 0..log.size-3 step 3) {
            if (hash == name) {
                println("Switched to commit 0b4f05fcd3e1dcc47f58fed4bb189196f99da89a.")
                restoreFile(name)
                return
            }
        }
        println("Commit does not exist.")
    }
}

fun help() {
    println("""These are SVCS commands:
config     Get and set a username.
add        Add a file to the index.
log        Show commit logs.
commit     Save changes.
checkout   Restore a file.
    """)
}

fun main(args: Array<String>) {
    if (!File("vcs").exists()) File("vcs").mkdir() // creates a folder "vcs" if it doesn't exists
    if (!File("vcs/commits").exists()) File("vcs/commits").mkdir()
    if (!File("vcs/log.txt").exists()) File("vcs/log.txt").writeText("")
    var secArg = ""
    if (args.size > 1) {
        secArg = args[1]
    }
    if (args.isNotEmpty()) when (args[0]) {
        "--help" -> help()
        "config" -> config(secArg)
        "add" -> add(secArg)
        "log" -> log()
        "commit" -> commit(secArg)
        "checkout" -> checkout(secArg)
        else -> println("'${args[0]}' is not a SVCS command.")
    } else help()
}
