// impoartant imports from files
import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout._
import javafx.geometry.Insets
import scala.util.{Try, Success, Failure}

// back end connection
import filesystemBackend._

object Main {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[MyApp], args: _*)
  }
}

class MyApp extends Application {
  // mutable variable for an immutable file system gui
  // starting as an empty zipper
  private var state: Zipper = _

  override def start(mainStage: Stage): Unit = {
    // file system structure that contain files from the backend.
    val fileSystemGui = Folder("home", List (
      Folder("myfolder", List(
          File("read.txt"))),
      Folder("testfolder", List(
          File("testfile.exe"))),
    ))
    state = Zipper(fileSystemGui, Nil)
    
    // all ui components for the visuals
    val pathLabel = Label(printPath(state))
    val listViewing = ListView[String]()
    val inputArea = TextField()
    val outputArea = TextArea()
    outputArea.setEditable(false)

    // creating a list view for thec current folder your in
    def reload(): Unit =
      pathLabel.setText(printPath(state))
      listViewing.getItems.clear()
      state.focus match
        case Folder(_, children) =>
          // when the node if focused on is a folder - prints out its children and relevant information
          
          // seperating children into folders and files by alphebtical order.
          val folders: List[Folder] = children.collect { case d: Folder => d}.sortBy(_.name)
          val files: List[File] = children.collect { case f: File => f}.sortBy(_.name)

          folders.foreach(folder => listViewing.getItems.add(s"<DIR> ${folder.name}"))
          files.foreach(file => 
            val paddedOutput = s"${"".padTo(6, ' ')}${file.name.padTo(12, ' ')} ${file.size}KB" 
            listViewing.getItems.add(s"$paddedOutput"))
        case File(_, _) =>
          None
        
        // clearing user input filed and changing the windows title to match the folder path.
        inputArea.clear
        mainStage.setTitle(printPath(state))

    reload()

    // taking commands from the users input area
    inputArea.setOnAction(_ => { 
      // spliting the users input different parts (command, target and modification) by " ".
      val input: String = inputArea.getText.toLowerCase.trim
      val parts: Array[String] = input.split(" ")
      
      if parts.size >= 1 then
        parts(0) match
          case "ls" =>
            // "refreshes" the focused nodes children.
            outputArea.appendText("refreshed.\n")
          case "cd" =>
            // changes the focused node to being the target. continues the shell with the new focus.
            // if that is possible.
            if parts.size == 2 then
              val target: String = parts(1)
              if target == ".." then
                cdUp(state) match
                  case Some(up) =>
                    state = up
                  case None =>
                    outputArea.appendText("the system cannot find the path specified")
              else
                cd(target, state) match
                  case Some(next) =>
                    state = next
                  case None =>
                    outputArea.appendText("the system cannot find the path specified")
          case "mkdir" =>
            // redefining the focus that adds the users folder in its list of children.
            if parts.size == 2 then
              val target: String = parts(1)
              state = mkDir(target, state)
            else
              outputArea.appendText("'mkdir' epects 1 parameter after command: mkdir <FOLDERNAME>")
          case "touch" =>
            // dedefining the focus that adds the users file in its list of children.
            if parts.size == 2 then
              val target: String = parts(1)
              state = touch(target, minFileSize, state)
            else if parts.size == 3 then
              val target: String = parts(1)
              val modifer: Try[Int] = Try(parts(2).toInt)
              modifer match
                case Success(value) =>
                  state = touch(target, value, state)
                case Failure(exception) =>
                  outputArea.appendText("File must be a number")
            else
              outputArea.appendText("'touch' expects a max of two parameters: touch <FILENAME> <FILESIZE>\n")
          case "rm" =>
            // redefining the focus to exclude the target file from its children.
            if parts.size == 2 then
              val target: String = parts(1)
              state = rm(target, state)
            else
              outputArea.appendText("'rm' expects 1 parameter: rm <FILENAME>\n")
          case "rmdir" =>
            // redefining the focus to exclude the target folder from its children.
            if parts.size == 2 then
              val target: String = parts(1)
              state = rmdir(target, state)
            else
              outputArea.appendText("'rmdir' expects 1 parameter: rmdir <FOLDERNAME>\n")
          case "taskkill" =>
            // Quit the GUI program by closing the window.
            mainStage.close
          
          case _ =>
            // user inputs anything not included above (a.k.a a command)
            outputArea.appendText(s"'${input}' is not recognised as a command please try again")
        reload()
    })

    // layout for the gui's visuals
    val vbox = VBox(
      10,
      pathLabel,
      listViewing,
      inputArea,
      Label("output: "),
      outputArea
    )
    vbox.setPadding(Insets(10))
    mainStage.setTitle(printPath(state))
    mainStage.setScene(Scene(vbox, 1280, 720))
    mainStage.show()
    }
}