Translates srt subtitle files from a language to another using Google Translate.

> Works only on linux. bash required (until the port of translate.bsh to java).
> The only files required are "translate.bsh" and "trsubs-1.0-SNAPSHOT-jar-with-dependencies.jar". The latter can be renamed.
> If --input-language is omitted, "auto" will be used.
> You can find language letters from the google translate website.

---------------------------------------------------------------------------------
Example (run from this directory):

java -jar target/trsubs-1.0-SNAPSHOT-jar-with-dependencies.jar -i 100-bloody-acres.se.srt -o 100-bloody-acres.en.srt -s se -t en

---------------------------------------------------------------------------------
Usage help:
 -h,--help                    prints help
 -i,--input-file <arg>        The input srt file
 -o,--output-file <arg>       The output srt file
 -s,--input-language <arg>    The input's language (can be omitted optionally)
 -t,--output-language <arg>   the output's language

---------------------------------------------------------------------------------
Build from source:

mvn clean install
