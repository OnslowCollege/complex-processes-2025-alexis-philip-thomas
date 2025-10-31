import javax.swing._
import java.awt._
import java.awt.event._

import scala.collection.immutable.List

import filesystemBackend._
import filesystemBackend.CommandUtils._

object MyApp {

	// Controls/is the focus of the shell. MUST be variable.
	var state: Zipper = _

	def main(args: Array[String]): Unit = {
		// Initialise default file system and focus on it.
		val fileSystemContent = Folder("home", List (
            Folder("myfolder", List(
                File("read.txt"))),
            Folder("testfolder", List(
                File("testfile.exe"))),
            File("testfile_display.exe")
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
					val folders: List[Folder] = children.collect { case d: Folder => d}.sortBy(_.name)
					val files: List[File] = children.collect { case f: File => f}.sortBy(_.name)

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

		// Command handler.
		inputArea.addActionListener(new ActionListener() {
			def actionPerformed(e: ActionEvent): Unit = {
				// Input is taken and split into components (e.g. <COMMAND> <TARGET> <MODIFIER>) by " ".
				val input: String = inputArea.getText.toLowerCase.trim
				val parts: Array[String] = input.split(" ")
				// Clear the input field.
				inputArea.setText("")
			
				if (parts.size >= 1) {
					parts(0) match {
						case "ls" =>
							outputArea.append("refreshed.\n")
						case "cd" =>
							// Change the focused node. Continue the shell with the new focus if possible.
							if (parts.size == 2) {
								val target: String = parts(1)
								if (target == "..") {
									cdUp(state) match {
										case Some(up) =>
											state = up
										case None =>
											outputArea.append("the system cannot find the path specified\n")
									}
								} else {
									cd(target, state) match {
										case Some(next) =>
											state = next
										case None =>
											outputArea.append("the system cannot find the path specified\n")
									}
								}
							} else {
								outputArea.append("'cd' expects 1 parameter(s): cd <FOLDERNAME>\n")
							}
						case "mkdir" =>
							// Redefine the focus to include a new folder in its children. Continue the shell with this new focus.
							if (parts.size == 2) {
								val target: String = parts(1)
								state = mkDir(target, state)
							} else {
								outputArea.append("'mkdir' epects 1 parameter after command: mkdir <FOLDERNAME>\n")
							}
						case "touch" =>
							// Redefine the focus to include a new file in its children. Continue the shell with this new focus.
							if (parts.size == 2) {
								val target: String = parts(1)
								state = touch(target, minFileSize, state)
							} else if (parts.size == 3) {
								val target: String = parts(1)
								// Try to cast the 2nd parameter (requested file size) to an integer.
								try {
									val modifer: Int = parts(2).toInt
									state = touch(target, modifer, state)
								} catch {
									case ex: Exception =>
										outputArea.append("File must be a number\n")
								}
							} else {
								outputArea.append("'touch' expects a max of two parameters: touch <FILENAME> <FILESIZE>\n")
							}
						case "rm" =>
							// Redefine the focus to exclude a file from its children. Continue the shell with this new focus.
							if (parts.size == 2) {
								val target: String = parts(1)
								state = rm(target, state)
							} else {
								outputArea.append("'rm' expects 1 parameter: rm <FILENAME>\n")
							}
						case "rmdir" =>
							// Redefine the focus to exclude a folder from its children. Continue the shell with this new focus.
							if (parts.size == 2) {
								val target: String = parts(1)
								state = rmdir(target, state)
							} else {
								outputArea.append("'rmdir' expects 1 parameter: rmdir <FOLDERNAME>\n")
							}

						case "kill" =>
							frame.dispose()
						
						case _ =>
							Nil
					}
					reload()
				}
			}

		})
	
		frame.setVisible(true)
	}

}
