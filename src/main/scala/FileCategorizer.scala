import scala.collection.mutable._

import java.io.File
import java.nio.file._

/*
 * A matrix iteration implementaion of Levenshtein distance
 */
trait Levenshtein {
  def compare(lhs: String, rhs: String): Int = {
    val d = Array.ofDim[Int](lhs.length + 1, rhs.length + 1)

    val lhsRange = 1 to lhs.length
    val rhsRange = 1 to rhs.length

    for (i <- lhsRange) d(i)(0) = i
    for (i <- rhsRange) d(0)(i) = i

    for (i <- lhsRange)
      for (j <- rhsRange) {
        if (lhs(i - 1) == rhs(j - 1))
          d(i)(j) = d(i - 1)(j - 1)
        else
          d(i)(j) = List(
            d(i - 1)(j) + 1, // a deletion
            d(i)(j - 1) + 1, // an insertion
            d(i - 1)(j - 1) + 1 // a substitution
          ).min
      }

    d(lhs.length)(rhs.length)
  }
}

/*
 * Caculate and cache name transformation results according to the configurations
 * eg: mask number in the file name with 'N'
 */
trait Categorizable {
  this: { def getName(): String } =>

  val maskNum: Boolean
  val maskExt: Boolean

  lazy val categoryIndetifier = {
    def partition(n: String): (String, String) = {
      val p = n.lastIndexOf(".")
      if (p > 0)
        (n.substring(0, p), n.substring(p, n.length))
      else
        (n, "")
    }

    var (id, ext) = partition(getName toLowerCase)

    if (maskNum)
      id = id map (x => if (x >= '0' && x <= '9') 'N' else x)

    if (!maskExt)
      id = id + ext

    id
  }
}

/*
 * Partition a list of files with Categorizable trait && move file
 */
trait FileCategorizer extends Levenshtein {
  def categorize[T <: Categorizable](
    entities: List[T], threshold: Int, process: List[T] => Unit): Unit = {

    if (!entities.isEmpty) {
      val (cate, rest): (List[T], List[T]) = entities.tail.partition(
        f => compare(entities.head categoryIndetifier, f categoryIndetifier) < threshold)

      if (!cate.isEmpty)
        process(entities.head :: cate)

      categorize(rest, threshold, process)
    }
  }

  def moveFile(
    file: File, base: File, category: String, capitalize: Boolean = true): Unit = {

    val path = {
      if (capitalize) {
        val folders = {
          val folders = category.split('.')

          folders.map(folder => {
            if (folder.exists(x => x >= 'A' && x <= 'Z')) {
              folder
            } else
              folder.capitalize
          })
        }
        folders.mkString(File.separator)
      } else
        category.flatMap(x => if (x == '.') File.separator else "" + x)
    }

    val dest = new File(
      base.getAbsolutePath + File.separator + path + File.separator + file.getName)

    val destFolder = dest.getParentFile
    if (!destFolder.exists) destFolder.mkdirs()

    if (!dest.exists) {
      Files.move(file.toPath, dest.toPath, StandardCopyOption.ATOMIC_MOVE)
    } else {
      println("Error: " + dest + " is already exist")
    }

    println(dest.getAbsolutePath)
  }
}
