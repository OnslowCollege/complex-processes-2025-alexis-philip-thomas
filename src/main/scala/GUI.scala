import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control._
import javafx.scene.layout._
import javafx.geometry.Insets

import scala.util.{Try, Success, Failure}
import filesystemBackend._

object Main {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[MyApp], args: _*)
  }
}

class MyApp extends Application {
  private var state: Zipper = _
  override def start(mainStage: Stage): Unit = {
    val fileSystemGui = Folder("home", List (
      Folder("myfolder", List(
          File("read.txt"))),
      Folder("testfolder", List(
          File("testfile.exe"))),
    ))
    state = Zipper(fileSystemGui, Nil)

    val pathLabel = Label(printPath(state))
    val listViewing = ListView[String]()
    val inputArea = TextField()
    val outputArea = TextArea()
    outputArea.setEditable(false)

    def reload(): Unit =
      pathLabel.setText(printPath(state))
      listViewing.getItems.clear()
      state.focus match
        case Folder(_, children) =>
          val folders: List[Folder] = children.collect { case d: Folder => d}.sortBy(_.name)
          val files: List[File] = children.collect { case f: File => f}.sortBy(_.name)

          folders.foreach(folder => listViewing.getItems.add(s"<DIR> ${folder.name}"))
          files.foreach(file => 
            val paddedOutput = s"${"".padTo(6, ' ')}${file.name.padTo(12, ' ')} ${file.size}KB" 
            listViewing.getItems.add(s"$paddedOutput"))
        case File(_, _) =>
          None
        inputArea.clear
        mainStage.setTitle(printPath(state))

    reload()

    inputArea.setOnAction(_ => { 
      val input: String = inputArea.getText.toLowerCase.trim
      val parts: Array[String] = input.split(" ")
      
      if parts.size >= 1 then
        parts(0) match
          case "ls" =>
            outputArea.appendText("refreshed.\n")
          case "cd" =>
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
            if parts.size == 2 then
              val target: String = parts(1)
              state = mkDir(target, state)
            else
              outputArea.appendText("'mkdir' epects 1 parameter after command: mkdir <FOLDERNAME>")
          case "touch" =>
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
            if parts.size == 2 then
              val target: String = parts(1)
              state = rm(target, state)
            else
              outputArea.appendText("'rm' expects 1 parameter: rm <FILENAME>\n")
          case "rmdir" =>
            if parts.size == 2 then
              val target: String = parts(1)
              state = rmdir(target, state)
            else
              outputArea.appendText("'rmdir' expects 1 parameter: rmdir <FOLDERNAME>\n")
        reload()
    })

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