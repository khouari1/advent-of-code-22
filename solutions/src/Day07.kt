import DirItem.Dir
import DirItem.File

fun main() {
    fun part1(input: List<String>): Int = getAllDirs(input).map { it.getDirSize() }.filter { it < 100_000 }.sum()

    fun part2(input: List<String>): Int {
        val totalDiskSpaceAvailable = 70_000_000
        val spaceRequired = 30_000_000
        val dirSizesAscending = getAllDirs(input).map { it.getDirSize() }.sorted()
        val outermostDirSize = dirSizesAscending.last()
        val spaceToFreeUp = spaceRequired - (totalDiskSpaceAvailable - outermostDirSize)
        return dirSizesAscending.first { it >= spaceToFreeUp }
    }

    // test if implementation meets criteria from the description, like:
    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95_437)
    check(part2(testInput) == 24_933_642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}

private fun getAllDirs(input: List<String>): List<Dir> {
    val rootDir = Dir("/")
    var cwd = rootDir
    val dirs = mutableListOf<Dir>()
    input.forEach { line ->
        val firstChar = line[0]
        when {
            firstChar == '$' -> {
                val command = line.substring(2, 4)
                when (command) {
                    "cd" -> {
                        cwd = when (val dirName = line.substring(5)) {
                            "/" -> rootDir
                            ".." -> cwd.parent!!
                            else -> {
                                val dir = Dir(dirName, cwd)
                                cwd.children.add(dir)
                                dirs.add(dir)
                                dir
                            }
                        }
                    }
                }
            }
            !line.startsWith("dir") -> {
                val (size, fileName) = line.split(" ")
                val file = File(fileName, size.toInt())
                cwd.children.add(file)
            }
        }
    }
    dirs.add(rootDir)
    return dirs
}

private fun Dir.getDirSize(): Int = children.sumOf {
    when (it) {
        is Dir -> it.getDirSize()
        is File -> it.size
    }
}

sealed class DirItem {

    data class Dir(val name: String, val parent: Dir? = null) : DirItem() {
        val children = mutableListOf<DirItem>()
    }

    data class File(val name: String, val size: Int) : DirItem()
}
