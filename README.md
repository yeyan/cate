cate
====

A Scala/Java program which categorize files under a specified directory based on Levenshtein distance and file name masking.


usage
====

cate 1.0
Usage: cate [options] [<file>]

  -b <file> | --base <file>
        optional base directory
  -t <Int> | --thres <Int>
        optional Levenshtein threshold, default = 3
  -n | --unmask_numbers
        optional toggle number mask
  -e | --unmask_ext
        optional toggle extension mask
  -c | --disable_smart_capitialize
        optional toggle smart captialize
  <file>
        optional target directory
  --help
        prints this usage text
