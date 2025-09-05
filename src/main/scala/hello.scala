// importing readLine
import scala.io.StdIn.readLine

// Description of Files & Folders
// Files & Folders must always have a name
trait Node {
    val name: String
}

// Case Classes
case class Folder(name: String, children: List[Node] = Nil) extends Node

case class File(name: String, val size: Int = 1) extends Node

// Makes the file (child node) aware of its parent folder
case class NodeContext(parentName: String, leftnodes: List[Node], rightNodes: List[Node])

case class Zipper(focus: Node, context: List[NodeContext])

// Commands
// Command - ls
def ls(z: Zipper): Unit =
    z.focus match
        case Folder(_, children) =>
        children.foreach {
            case Folder(name, _) => println(s"/$name")
            case File(name, size) => println(s"name   ${size}KB")
        }
        case _ =>
            None


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