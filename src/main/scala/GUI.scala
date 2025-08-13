import javafx.application.Application
import javafx.stage.Stage
import javafx.scene.Scene
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.VBox

object Main {
  def main(args: Array[String]): Unit = {
    Application.launch(classOf[MyApp], args: _*)
  }
}

class MyApp extends Application {
  override def start(stage: Stage): Unit = {
    val label = new Label("Hello from Scala JavaFX")
    val button = new Button("Click me")

    button.setOnAction(_ => label.setText("Button clicked"))

    val layout = new VBox(10, label, button)
    val scene = new Scene(layout, 300, 200)

    stage.setTitle("Scala JavaFX App")
    stage.setScene(scene)
    stage.show()
  }
}