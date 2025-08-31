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
      val input = textField.getText.toLowerCase.trim
      val parts = input.split("\\s+").filter(_.nonEmpty)
      

      if (parts(0) == "ls") {
        label.setText("will get back to it")
      } else  if (parts(0) == "cd") {
        if (parts(1) == "test") {
          label2.setText("detected test")
        } else {
          label2.setText(s"don't recongnize ${parts(1)}")
        }
      } else {
        label2.setText(s"dont recognize $input")
      }
    })

    val layout = new VBox(10, label, label2, textField, button)
    val scene = new Scene(layout, 300, 200)

    stage.setTitle("Scala JavaFX App")
    stage.setScene(scene)
    stage.show()
  }
}