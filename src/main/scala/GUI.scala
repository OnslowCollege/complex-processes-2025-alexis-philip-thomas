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
      File("testfile_display.exe")
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