// imports
import scala.io.StdIn.readLine
import scala.collection.mutable.ListBuffer

// Description of Files & Folders
trait FolderDetails {
    var name: String
    var size: Int
}

trait FileDetails {
    var name: String
    var size: Int
}

// Classes - Files & Folders
class Folder(var name: String, var size: Int) extends FolderDetails {
    override def toString = s"$name/   (dir - $size KB)"
}

class File(var name: String, var size: Int) extends FileDetails {
    override def toString = s"$name    $size KB"
}

// Terminal
object FileSystem
    def main(args: Array[String]): Unit = {
        val content = ListBuffer[Any]()
        content += (Folder("MyFolder", 1))
        content += (File("README.txt", 1))
        var input: String = ""
        while (input != "end") {
            println("Enter your command (Enter end to quit): ")
            var input = readLine()
            if (input == "ls") {
                println(s"$content")
            }
            if (input == "end") {
                println("Ending task")
                System.exit(0)
                }
            }
        }