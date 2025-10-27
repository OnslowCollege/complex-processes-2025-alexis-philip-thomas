import javax.swing._
import java.awt._
import java.awt.event._

import filesystemBackend._
import filesystemBackend.CommandUtils._

object MyApp {

	// Controls/is the focus of the shell. MUST be variable.
	var state: Zipper = _

	def main(args: Array[String]): Unit = {
		// Initialise default file system and focus on it.
		val fileSystemContent = new Folder("home", scala.List (
            new Folder("myfolder", scala.List(
                new File("read.txt", 1))),
            new Folder("testfolder", scala.List(
                new File("testfile.exe", 1))),
            new File("testfile_display.exe", 1)
            ))
		state = Zipper(fileSystemContent, Nil)


		// Define the appearance and behaviour of the window.
		val frame = new JFrame()
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
		frame.setSize(800, 600)
		
		// Components. listViewing requires listModel.
		val pathLabel = new JLabel(printPath(state))
		val listModel = new DefaultListModel()
		val listViewing = new JList(listModel)
		val inputArea = new JTextField()
		val outputArea = new JTextArea()
		outputArea.setEditable(false)

		// Layout of components.

		val topPanel = new JPanel(new BorderLayout(10, 10))
		topPanel.add(pathLabel, BorderLayout.NORTH)
		topPanel.add(new JScrollPane(listViewing), BorderLayout.CENTER)
		topPanel.add(inputArea, BorderLayout.SOUTH)
		topPanel.setPreferredSize(new Dimension(1280, 450))

		val bottomPanel = new JPanel(new BorderLayout(10, 10))
		bottomPanel.add(new JLabel("output:"), BorderLayout.NORTH)
		bottomPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER)

		val mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel)
		mainSplit.setDividerSize(0)

		frame.getContentPane().add(mainSplit)

		// Refresh the listview, window title, pathlabel.
		def reload(): Unit = {
			pathLabel.setText(printPath(state))
			listModel.clear()
			frame.setTitle(printPath(state))
			state.focus match {
				case Folder(_, children) =>
					val folders: scala.List[Folder] = children.flatMap {
						case d: Folder => Some(d)
						case _ => None
					}
					val files: scala.List[File] = children.flatMap {
						case f: File => Some(f)
						case _ => None
					}

					folders.foreach { folder =>
						listModel.addElement("<DIR> " +  folder.name)
					}

					files.foreach { file =>
						val paddedOutput = "      " + file.name + " " + file.size + "KB"
						listModel.addElement(paddedOutput)
					}

				case File(_, _) =>
					None
					// inputArea.clear()
					
			}
		}

		reload()


		inputArea.addActionListener(new ActionListener() {
			def actionPerformed(e: ActionEvent): Unit = {
				val input: String = inputArea.getText.toLowerCase.trim
				val parts: Array[String] = input.split(" ")
			
				if (parts.size >= 1) {
					parts(0) match {
						case "ls" =>
							outputArea.append("Refreshed\n")
					}
				}
			}
		})
	
		frame.setVisible(true)
	}

}
