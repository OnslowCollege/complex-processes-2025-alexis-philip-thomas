@main
// Classes - Files & Folders
class File extends Folder {
    var name: String
}

class Folder {
    var name: String
    var files: [File] =>> [List]
}

var folder = Folder(name: "MyFolder")
var file = File(name: "README.txt")

