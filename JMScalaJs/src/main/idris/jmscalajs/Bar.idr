module microservicescalajs.Bar

import IdrisJvm.FFI
import IdrisJvm.IO
import Java.Lang

%access public export

pythag : Int -> List (Int, Int, Int)
pythag max = [(x, y, z) | z <- [1..max], y <- [1..z], x <- [1..y],
                         x * x + y * y == z * z]

jpythag : Int -> String
jpythag n = show $ pythag n

helloIdris : String -> JVM_IO ()
helloIdris name = printLn name

jmain : StringArray -> JVM_IO ()
jmain args = do
 printLn $ jpythag 50
 helloIdris "Bar"

exports : FFI_Export FFI_JVM "universe/microservice/microservicescalajs/impl/idris/JBar" []
exports =
 Fun jpythag (ExportStatic "pythag") $
 Fun helloIdris (ExportStatic "helloIdris") $
 Fun jmain (ExportStatic "main") $
 End
