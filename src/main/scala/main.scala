import com.typesafe.scalalogging.slf4j.Logging

import scala.collection.mutable._
import java.io._

object App extends Logging with FileCategorizer with ConfigParser {
  System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

  def main(args: Array[String]) {

    parseArgs(args, config => {
      //println(config)

      val files = getDirectoryContents(config.target, config)
      categorize[File with Categorizable](files, config.threshold, list => {
        println("--------------------------------------------------")
        list.map(f => println(f.getName))

        val category: String =
          scala.Console.readLine(
            "please assign a category to this group, leave blank to skip\n")

        if (!category.isEmpty)
          list map (moveFile(_, config.base, category, config.smartCap))
      })
    })

  }

  /*
   * list all files under user specified directory (depth = 1) 
   * and put them in a immutable list
   */
  def getDirectoryContents(root: File, config: Config): List[File with Categorizable] = {
    val list = new ListBuffer[File with Categorizable];

    for (name <- root.list)
      list += new File(root.getAbsolutePath + File.separator + name) with Categorizable {
        val maskNum = config.maskNum
        val maskExt = config.maskExt
      }

    list.toList
  }
}
