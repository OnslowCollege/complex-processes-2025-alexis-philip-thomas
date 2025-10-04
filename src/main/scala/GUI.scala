import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.{Button, Label, TextField}
import javafx.scene.layout.VBox

object Main {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[MyApp], args: _*)
  }
}

class MyApp extends Application {
  override def start(stage: Stage): Unit = {
    val label = new Label("/home")
    val label2 = new Label("")
    val textField = new TextField()
    textField.setPromptText("enter your command")

    val button = new Button("confirm")

    button.setOnAction(_ => { 
      val input: String = textField.getText.toLowerCase.trim
      val parts: Array[String] = input.split(" ")
      
      if parts.size >= 1 then
        parts(0) match
          case "ls" =>
            label2.setText("detected ls")
          
          case "cd" =>
            label2.setText("detected cd")
          
          case "mkdir" =>
            label2.setText("detected mkdir")
          
          case "touch" =>
            label2.setText("detected touch")
          
          case "rm" =>
            label2.setText("detected rm")
          
          case "rmdir" =>
            label2.setText("detected rmdir")
          
          case "taskkill" =>
            label2.setText("detected taskkill")
          
          case _ =>
            label2.setText(s"'{$input}' is not a vaild command type please try again")
    })

    val layout = new VBox(10, label, label2, textField, button)
    val scene = new Scene(layout, 300, 200)

    stage.setTitle("Scala JavaFX App")
    stage.setScene(scene)
    stage.show()
  }
}