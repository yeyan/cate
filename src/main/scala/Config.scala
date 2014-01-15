import java.io._

/*
 * over all application configuration
 */
case class Config(
  val target: File,
  val base: File,
  val threshold: Int = 3,
  val maskNum: Boolean = true,
  val maskExt: Boolean = true,
  val smartCap: Boolean = true)

object Config {
  def apply(target: File): Config =
    Config(target, defaultBase(target))

  def apply(): Config =
    Config(new File("."))

  def defaultBase(target: File): File =
    new File(target.getCanonicalPath + File.separator + "Categories")
}

/*
 * console command line parser
 */
trait ConfigParser {
  var defaultBase = true

  val parser = new scopt.OptionParser[Config]("cate") {
    head("cate", "1.0")

    opt[File]('b', "base") valueName ("<file>") action { (x, c) =>
      {
        defaultBase = false
        c.copy(base = x)
      }
    } text ("optional base directory")

    opt[Int]('t', "thres") valueName ("<Int>") action { (x, c) =>
      c.copy(threshold = x)
    } text ("optional Levenshtein threshold, default = 3")

    opt[Unit]('n', "unmask_numbers") action { (_, c) =>
      c.copy(maskNum = false)
    } text ("optional toggle number mask")

    opt[Unit]('e', "unmask_ext") action { (_, c) =>
      c.copy(maskExt = false)
    } text ("optional toggle extension mask")

    opt[Unit]('c', "disable_smart_capitialize") action { (_, c) =>
      c.copy(smartCap = false)
    } text ("optional toggle smart captialize")

    arg[File]("<file>") optional () action { (x, c) =>
      {
        if (defaultBase)
          c.copy(target = x, base = Config.defaultBase(x))
        else
          c.copy(target = x)
      }
    } validate { x =>
      if (x.exists && x.isDirectory)
        success else failure("\"" + x.getPath + "\" is not a valid directory")
    } text ("optional target directory")

    help("help") text ("prints this usage text")
  }

  def parseArgs(args: Array[String], process: Config => Unit) = {
    parser.parse(args, Config()) map { config =>
      process(config)
    }
    // getOrElse {
    // arguments are bad, error message will have been displayed
    // }
  }
}
