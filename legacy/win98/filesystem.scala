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

// Zipper - a focus on a node with context relating that node to the root folder
case class Zipper(focus: Node, context: List[NodeContext])

object CommandUtils {

    // To print path in terminal
    def printPath(z: Zipper): String = {
        def loop(z: Zipper, accumulated: List[String]): List[String] = {
            z.context match {
                case Nil => 
                    // If child file doesn't have parent folder
                    z.focus.name :: accumulated
                case NodeContext(parentName, left, right) :: rest =>
                    // If child file does have parent folder - loop over node repeatedly
                    /* Loop on a zipper created with a folder created with this object's parent's name,
                    and children including this node's siblings (left-side siblings joined to this node
                    and right-side siblings). The new zipper's context is the whole tail of the base zipper's
                    context. The accumulation of the loop is the new zipper's folder's name at the head of
                    the current accumulation/path, building the path. */
                    loop(Zipper(Folder(parentName, left ::: (z.focus :: right)), rest), z.focus.name :: accumulated)
            }
        }
                loop(z, Nil).mkString("/")
    }

    // Commands
    // Command - ls
    def ls(z: Zipper): Unit = {
        z.focus match {
            case Folder(_, children) =>
                children.foreach {
                    case Folder(name, _) => println(name)
                    case File(name, size) => println(name + "    " + size + "KB")
                }
            case _ =>
                None
        }
    }
}


object Main {
    // Content - files and folders
    def main(args: Array[String]): Unit = {
        val fileSystemContent = Folder("home", List (
            Folder("documents", List(
                File("cv.pdf"),
                File("data.dat")
            )),
            Folder("downloads", List(
                Folder("msinstaller", List(
                    File("instmsia.exe")
                ))
            )),
            Folder("music", List(
                File("GOLDEN.mp3")
            )),
            Folder("photos", List(
                File("passport.jpg"),
                File("photoid.png"),
                Folder("japan2026", List(
                    File("tokyo.png"),
                    File("kyoto.png"),
                    File("miyajima.png")
                ))
            )),
            File("autoexec.bat"),
            File("config.sys"),
            File("command.com")
        ))
        val zipper = Zipper(fileSystemContent, Nil)
        println(CommandUtils.printPath(zipper))
        CommandUtils.ls(zipper)
        // shell(zipper)
    }
}