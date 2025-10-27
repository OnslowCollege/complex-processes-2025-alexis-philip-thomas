package filesystemBackend

// Description of Files & Folders
// Files & Folders must always have a name
trait Node {
    val name: String
}

// Case Classes
case class Folder(name: String, children: List[Node]) extends Node

case class File(name: String, val size: Int) extends Node

// Makes the file (child node) aware of its parent folder
case class NodeContext(parentName: String, leftnodes: List[Node], rightNodes: List[Node])

// Zipper - a focus on a node with context relating that node to the root folder
case class Zipper(focus: Node, context: List[NodeContext])

object CommandUtils {

    // constants
    val minFileSize = 1
    val maxFileSize = 4194304


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
                // Omitted sortBy()
                val folders: List[Folder] = children.flatMap {
                    case d: Folder => Some(d)
                    case _ => None
                }

                val files: List[File] = children.flatMap {
                    case f: File => Some(f)
                    case _ => None
                }

                folders.foreach { folder =>
                    println("<DIR> " +  folder.name)
                }
                
                files.foreach { file =>
                    val paddedOutput = "      " + file.name + "   " + file.size + "KB"
                    println(paddedOutput)
                }
                
                val totalSize: Int = files.map(_.size).reduceLeft[Int](_ + _)
                println("\n" + folders.size + " DIR(S)")
                println(files.size + " FILE(S)")
                println("Total: " + totalSize + " KB")
            case _ =>
                None
        }
    }

    
    // command cd 
    def cd(name: String, z: Zipper): Option[Zipper] = {
        z.focus match {
            case Folder(parentName, children) =>
                val (left, targetAndRight) = children.span(_.name != name)
                targetAndRight match {
                    case (target @ Folder(_, _)) :: right =>
                        Some(Zipper(target, NodeContext(parentName, left, right) :: z.context))
                    case (target @ File(_, _)) :: _ =>
                        None
                    case Nil =>
                        None
                }
            case _ =>
                None
        }
    }

    // cd up the chain
    def cdUp(z: Zipper): Option[Zipper] = {
        z.context match {
            case NodeContext(parentName, left, right) :: rest =>
                val newChildren = left ::: (z.focus :: right)
                Some(Zipper(Folder(parentName, newChildren), rest))
            case Nil =>
                None
        }
    }


    // update a zipper's focused object
    def updateFocus(z: Zipper, newFocus: Node): Zipper = {
        Zipper(newFocus, z.context)
    }


    // command - mkdir
    def mkDir(name: String, z: Zipper): Zipper = {
        z.focus match {
            case Folder(parentName, children) =>
                if (children.exists(_.name == name)) {
                    println("'" + name + "' already exists in current folder")
                    z
                } else {
                    if (name.size >= 1  && name.size <= 12) {
                        // DEFAULT NIL.
                        val newFolder: Folder = Folder(name, Nil)
                        val newFocus: Folder = Folder(parentName, newFolder :: children)
                        updateFocus(z, newFocus)
                    } else {
                        println("name must conform to name conventions")
                        z
                    }
                }

            case File(_, _) =>
                println("'" + name + "' connot contain directories.")
                z
        }
    }

    // command - touch
    def touch(name: String, size: Int, z: Zipper): Zipper = {
        z.focus match {
            case Folder(parentName, children) =>
                if (children.exists(_.name == name)) {
                    println("file '" + name + "' already exist")
                    z
                } else {
                    if (name.size >= 1 && name.size <= 12) {
                        if (size >= minFileSize && size <= maxFileSize) {
                            val newFile = File(name, size)
                            val newFocus = Folder(parentName, newFile :: children)
                            updateFocus(z, newFocus)
                        } else {
                            println("file size be within range (" + maxFileSize + "KB).")
                            z
                        }
                    } else {
                        println("name must conform to name conventions")
                        z
                    }
                }
            case File(_, _) =>
                println("'" + name + "' cannot contain files within it.")
                z
        }
    }







    // the shell function. acts on the zipper. to change the shell, redefinite it with a new zipper.
    def shell(z: Zipper): Unit = {
        // input is taken and then split into components
        val input: String = readLine(printPath(z) + ">").trim.toLowerCase
        val inputparams: Array[String] = input.split(" ")

        if (inputparams.size >= 1) {
            inputparams(0) match {
                case "ls" =>
                    ls(z)
                    shell(z)
                case "cd" =>
                    if (inputparams.size == 2) {
                        val target: String = inputparams(1)
                        if (target == "..") {
                            cdUp(z) match {
                                case Some(up) =>
                                    shell(up)
                                case None =>
                                    println("the system connot find the path")
                                    shell(z)
                            }
                        } else {
                            cd(target, z) match {
                                case Some(next) =>
                                    shell(next)
                                case None =>
                                    println("system can't locate path.")
                                    shell(z)
                            }
                        }
                    }
                case "mkdir" =>
                    if (inputparams.size == 2) {
                        val target: String = inputparams(1)
                        shell(mkDir(target, z))
                    } else {
                        println("expected input after mkdir: mkdir <FOLDERNAME>")
                        shell(z)
                    }
                case "touch" =>
                    if (inputparams.size == 2) {
                        val target: String = inputparams(1)
                        shell(touch(target, minFileSize, z))
                    } else if (inputparams.size == 3) {
                        val target: String = inputparams(1)
                        try {
                            val modifier: Int = inputparams(2).toInt
                            shell(touch(target, modifier, z))
                        } catch {
                            case ex: Exception =>
                                println("file size must be an integer.")
                                shell(z)
                        }
                    } else {
                        println("touch expects a maximum of 2 paramters: touch <FILENAME> <FILESIZE>")
                    }
                case "kill" =>
                    println("killed")
            }
        } else {
            shell(z)
        }
    }
}


object Main {
    // Content - files and folders
    def main(args: Array[String]): Unit = {
        val fileSystemContent = Folder("home", List (
            Folder("myfolder", List(
                File("read.txt", 1))),
            Folder("testfolder", List(
                File("testfile.exe", 1))),
            File("testfile_display.exe", 1)
            ))
            val zipper = Zipper(fileSystemContent, Nil)
            CommandUtils.shell(zipper)
    }
}