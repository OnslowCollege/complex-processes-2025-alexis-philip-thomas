import javax.swing._
import java.awt._
import java.awt.event._


object MyApp {
	
	def main(args: Array[String]): Unit = {


		// Define the appearance of the window.
		val frame = new JFrame("Scala Java Swing App")
		frame.setSize(300, 200)
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
		// frame.setLayout()

		val label = new JLabel("home/")

		val label2 = new JLabel("\n")

		val textField = new JTextField()

		val button = new JButton("confirm")

		val topPanel = new JPanel(new BorderLayout())
		topPanel.add(label, BorderLayout.NORTH)
		topPanel.add(label2, BorderLayout.CENTER)
		
		val bottomPanel = new JPanel(new BorderLayout())
		bottomPanel.add(textField, BorderLayout.NORTH)
		bottomPanel.add(button, BorderLayout.SOUTH)

		val midSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel)

		midSplit.setDividerSize(0)

		frame.getContentPane().add(midSplit)



		button.addActionListener(new ActionListener() {
			def actionPerformed(e: ActionEvent): Unit = {
				val input: String = textField.getText.toLowerCase.trim
				val parts: Array[String] = input.split(" ")
			
				if (parts.size >= 1) {
					parts(0) match {
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
							label2.setText("'" + input + "' is not a vaild command type please try again")
					}
				}
			}
		})
		

		

		

		/* This is redundant from FlowLayout testing.
		// Vertical alignment panels.
		val topPanel = new JPanel(new BorderLayout())
		topPanel.add(label, BorderLayout.NORTH)
		topPanel.add(label2, BorderLayout.CENTER)

		val bottomPanel = new JPanel(new BorderLayout())
		bottomPanel.add(textField, BorderLayout.NORTH)
		bottomPanel.add(button, BorderLayout.CENTER)

		val mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel)

		
		

		frame.getContentPane().add(mainSplit)
		*/

		/* Example Swing program
		val textArea = new JTextArea(10, 40)
		frame.getContentPane().add(BorderLayout.CENTER, textArea)
		val button = new JButton("Click")
		frame.getContentPane().add(BorderLayout.SOUTH, button)
		button.addActionListener(new ActionListener() {
			def actionPerformed(e: ActionEvent): Unit = {
				textArea.append("Button was clicked\n")
			}
		})
		*/
	
		frame.setVisible(true)
	}

}