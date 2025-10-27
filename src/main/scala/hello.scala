package filesystemBackend

// readLine import
import scala.io.StdIn.readLine
import scala.util.{Try, Success, Failure}

// Description of Files & Folders
trait Node { val name: String}

// Case Classes
case class Folder(name: String, children: List[Node] = Nil) extends Node

case class File(name: String, val size: Int = 1) extends Node

// Makes the file (child node) aware of its parent folder
case class NodeContext(parentName: String, leftnodes: List[Node], rightNodes: List[Node])

// Zipper - a focus on a node with context relating that node to the root folder
case class Zipper(focus: Node, context: List[NodeContext])

// To print path in terminal
def printPath(z: Zipper): String =
    def loop(z: Zipper, accumulated: List[String]): List[String] =
        z.context match
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
    loop(z, Nil).mkString("/")

// Commands
// Command - ls
def ls(z: Zipper): Unit =
    z.focus match
        case Folder(_, children) =>
            val folders: List[Folder] = children.collect { case d: Folder => d}.sortBy(_.name)
            val files: List[File] = children.collect { case f: File => f}.sortBy(_.name)
            folders.foreach(folder =>
                println(s"<DIR> ${folder.name}"))
            
            files.foreach(file =>
                val paddedOutput = s"${"".padTo(6, ' ')}${file.name.padTo(12, ' ')} ${file.size}KB"
                println(s"$paddedOutput"))
            
            val totalSize: Int = files.map(_.size).sum
            println(s"\n${folders.size} DIR(S)")
            println(s"${files.size} FILE(S)")
            println(s"Total: ${totalSize} KB")
        case _ =>
            None

// command cd 
def cd(name: String, z: Zipper): Option[Zipper] =
    z.focus match
        case Folder(parentName, children) =>
            val (left, targetAndRight) = children.span(_.name != name)
            targetAndRight match
                case (target @ Folder(_, _)) :: right =>
                    Some(Zipper(target, NodeContext(parentName, left, right) :: z.context))
                case (target @ File(_, _)) :: _ =>
                    None
                case Nil =>
                    None
        case _ =>
            None

// cd up the chain
def cdUp(z: Zipper): Option[Zipper] =
    z.context match
        case NodeContext(parentName, left, right) :: rest =>
            val newChildren = left ::: (z.focus :: right)
            Some(Zipper(Folder(parentName, newChildren), rest))
        case Nil =>
            None

// update a zipper's focused object
def updateFocus(z: Zipper, newFocus: Node): Zipper =
    Zipper(newFocus, z.context)

// command - mkdir
def mkDir(name: String, z: Zipper): Zipper =
    z.focus match
        case Folder(parentName, children) =>
            if (children.exists(_.name == name)) then
                println(s"'$name' already exists in current folder")
                z
            else
                if name.size >= 1  && name.size <= 12 then
                    val newFolder: Folder = Folder(name)
                    val newFocus: Folder = Folder(parentName, children :+ newFolder)
                    updateFocus(z, newFocus)
                else
                    println("name must conform to name conventions")
                    z

        case File(_, _) =>
            println(s"'$name' connot contain directories.")
            z

// command - touch
def touch(name: String, size: Int, z: Zipper): Zipper =
    z.focus match
        case Folder(parentName, children) =>
            if (children.exists(_.name == name)) then
                println(s"file '$name' already exist")
                z
            else
                if name.size >= 1 && name.size <= 12 then
                    if size >= minFileSize && size <= maxFileSize then
                        val newFile = File(name, size)
                        val newFocus = Folder(parentName, children :+ newFile)
                        updateFocus(z, newFocus)
                    else
                        println(s"file size be within range (${maxFileSize}KB).")
                        z
                else 
                    println("name must conform to name conventions")
                    z
        case File(_, _) =>
            println(s"'$name' cannot contain files within it.")
            z


// redefine yhe zipper's focused folder's childtren with one missing a target.
// function for removing
def removeChild(z: Zipper, name: String): Zipper = 
    z.focus match
        case Folder(parentName, children) =>
            val newFocus: Folder = Folder(parentName, children.filter(_.name != name))
            updateFocus(z, newFocus)
        case File(_, _) =>
            println(s" '$name' cannot contain files.")
            z

// command rm - remove a file
def rm(name: String, z: Zipper): Zipper =
    z.focus match
        case Folder(_, children) =>
            children.find(_.name == name) match
                case Some(File(_, _)) =>
                    removeChild(z, name)
                case Some(Folder(_, _)) =>
                    println(s"'$name' cannot be removed - it is a directory")
                    z
                case None =>
                    println("the system cannot find the path")
                    z
        case File(_, _) =>
            println(s"'$name' cannot contain files.")
            z

// command rmdir - remove a folder
def rmdir(name: String, z: Zipper): Zipper =
    z.focus match
        case Folder(_, children) =>
            children.find(_.name == name) match
                case Some(Folder(_, _)) =>
                    removeChild(z, name)
                case Some(File(_, _)) =>
                    println(s"'$name' cannot be removed - it is a file.")
                    z
                case None =>
                    println("the system cannot find the path")
                    z
        case File(_, _) =>
            println(s"'$name' cannot contain directories.")
            z


// the shell function. acts on the zipper. to change the shell, redefinite it with a new zipper.
@annotation.tailrec
def shell(z: Zipper): Unit = 
    // input is taken and then split into components
    val input: String = readLine(s"${printPath(z)}>").trim.toLowerCase
    val inputparams: Array[String] = input.split(" ")

    if inputparams.size >= 1 then
        inputparams(0) match
            case "ls" =>
                ls(z)
                shell(z)
            case "cd" =>
                if inputparams.size == 2 then
                    val target: String = inputparams(1)
                    if (target == "..") then
                        cdUp(z) match
                            case Some(up) =>
                                shell(up)
                            case None =>
                                println("the system connot find the path")
                                shell(z)
                    else
                        cd(target, z) match
                            case Some(next) =>
                                shell(next)
                            case None =>
                                println("system can't locate path.")
                                shell(z)
            case "mkdir" =>
                if inputparams.size == 2 then
                    val target: String = inputparams(1)
                    shell(mkDir(target, z))
                else
                    println("expected input after mkdir: mkdir <FOLDERNAME>")
                    shell(z)
            case "touch" =>
                if inputparams.size == 2 then
                    val target: String = inputparams(1)
                    shell(touch(target, minFileSize, z))
                else if inputparams.size == 3 then
                    val target: String = inputparams(1)
                    val modifier: Try[Int] = Try(inputparams(2).toInt)
                    modifier match
                        case Success(value) =>
                            shell(touch(target, value, z))
                        case Failure(exception) =>
                            println("file size must be an integer.")
                            shell(z)
                else
                    println("touch expects a maximum of 2 paramters: touch <FILENAME> <FILESIZE>")
                    shell(z)
            case "rm" =>
                if inputparams.size == 2 then
                    val target: String = inputparams(1)
                    shell(rm(target, z))
                else
                    println("'rm' expects 1 parameter(s): rm <FILENAME>")
                    shell(z)
            case "rmdir" =>
                if inputparams.size == 2 then
                    val target: String = inputparams(1)
                    shell(rmdir(target, z))
                else
                    println("'rmdir' expects 1 parameter(s): rmdir <FOLDERNAME> ")

            case "kill" =>
                println("killed")
    else
        shell(z)

// constants
val minFileSize = 1
val maxFileSize = 4194304

// Content - files and folders
object Filesystem:
    def main(args: Array[String]): Unit =
        val fileSystemContent = Folder("home", List (
            Folder("myfolder", List(
                File("read.txt"))),
            Folder("testfolder", List(
                File("testfile.exe"))),
            ))
            val zipper = Zipper(fileSystemContent, Nil)
            shell(zipper)