// readLine import
import scala.io.StdIn.readLine

// Terminal
@main def terminal() =
    println("Your command (Enter end to quit): ")
    var input: String = ""
    while (input != "end") {
        println("")
        var input = readLine()
        if (input != "end") {
            if (input == "dir") {
                println("MyFolder")
            }
        }
    println("Ending task")
    System.exit(0)
    }
    