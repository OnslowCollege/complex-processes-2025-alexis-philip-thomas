// importing readLine
import scala.io.StdIn.readLine

// Description of Files & Folders
trait Node {
    val name: String
}

// Classes - Files & Folders
case class Folder(name: String) extends Node {
    var _children: List[Node] = List.empty
    def children: List[Node] = _children
    def addChild(child: Node): Unit = {
        _children = _children :+ child
    }
    override def toString = s"$name/"
}

case class File(name: String, val size: Int = 1, val parent: Node) extends Node {
    def getParentFolder: String = parent.name
    override def toString = s"$name    $size KB"
}

val myFolder =  new Folder("MyFolder")
val readMe = new File("README.txt", 1, myFolder)

// Terminal
object FileSystem
    def main(args: Array[String]): Unit = {
        var input: String = ""
        while (input != "end") {
            println("Enter your command (Enter end to quit): ")
            var input = readLine()
            if (input == "ls") {
                println(s"${readMe.getParentFolder}")
            }
            if (input == "end") {
                println("Ending task")
                System.exit(0)
                }
            }
        }