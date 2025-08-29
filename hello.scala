// imports
import scala.io.StdIn.readLine
import scala.collection.mutable.ListBuffer

// Description of Files & Folders
trait FilesystemNode {
    var name: String
    var size: Int
    var parentNode: FilesystemNode
}

class Folder extends FilesystemNode {
    var name: String
    var size: Int = 1
    var parentNode: Option[FilesystemNode]
}
trait FolderDetails {
    var name: String
    var size: Int
}

trait FileDetails {
    var name: String
    var size: Int
}

// Classes - Files & Folders
case class Folder(var name: String, var size: Int) extends FolderDetails {
    override def toString = s"$name/   (dir - $size KB)"
}

case class File(var name: String, var size: Int) extends FileDetails {
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