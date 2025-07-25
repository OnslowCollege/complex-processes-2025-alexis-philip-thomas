package system
// Classes - Files & Folders
object DirectoryInteractive {
    def main(args: Array[String]): Unit = {
        val show = new File()
        show.FolderName()
        show.FileName()
    }
}

import system.DirectoryInteractive.main

class Folder {
    def FolderName(): Unit = {
        println("MyFolder")
    }
}

class File extends Folder {
    def FileName(): Unit = {
        println("README.txt")
    }
}

